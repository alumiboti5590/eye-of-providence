package com.alumiboti5590.eyeofprovidence.robot.subsystems.vision;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import com.alumiboti5590.eyeofprovidence.robot.subsystems.vision.util.GeneralPoseEstimate;
import com.alumiboti5590.eyeofprovidence.robot.subsystems.vision.util.ICameraMeasurementProvider;

/**
 * <h3>PhotonVisionCameraSubsystem</h3>
 * * Manages a single coprocessor camera running the PhotonVision software. This subsystem 
 * handles raw targeting data from AprilTags and processes global field-relative localization.
 * * <p>To prevent race conditions and multi-consumer queue-draining bugs (where reading a 
 * frame destroys it for other parts of the codebase), this class flushes the camera network 
 * queue exactly once per scheduler cycle in {@link #periodic()} and caches the results 
 * thread-safely in a local list.</p>
 */
public class PhotonVisionCameraSubsystem extends SubsystemBase implements ICameraMeasurementProvider {
    
    /** Default host address and port for the PhotonVision coprocessor dashboard. */
    public static final String HOST = "photonvision.local";
    public static final int PORT = 5800;

    /** Base standard deviation vector [x, y, theta] applied when tracking a single and multi-tag AprilTag frames. */
    public static final Matrix<N3, N1> kSingleTagStdDevs = VecBuilder.fill(4, 4, 8);
    public static final Matrix<N3, N1> kMultiTagStdDevs = VecBuilder.fill(0.5, 0.5, 1);

    /** Field 2D dashboard visualizer to project the camera's isolated pose estimates. */
    private final Field2d m_field = new Field2d();

    /** Underlying hardware communication instance wrapper. */
    private final PhotonCamera camera;
    
    /** Core pose estimator used to map camera targeting geometry into global field coordinates. */
    private final PhotonPoseEstimator photonEstimator;
    
    /** Stores the dynamically adjusted standard deviation matrix calculated for the current loop frame. */
    private Matrix<N3, N1> curStdDevs;

    /** Caches the absolute latest frame capture packet obtained during the periodic sweep. */
    private PhotonPipelineResult pipelineResult;
    
    /** Local thread cache holding all unread pipeline frames collected at the start of the scheduler frame. */
    private List<PhotonPipelineResult> m_unreadResults = new ArrayList<>();

    /**
     * Constructs a PhotonVisionCameraSubsystem assuming the default seasonal FRC AprilTag field layout.
     * * @param cameraName     The user-defined name of the camera configured inside the PhotonVision dashboard.
     * @param robotToCamera  The 3D transformation offset translating from the center of the robot to the camera lens.
     */
    public PhotonVisionCameraSubsystem(String cameraName, Transform3d robotToCamera) {
        this(cameraName, robotToCamera, AprilTagFieldLayout.loadField(AprilTagFields.kDefaultField));
    }

    /**
     * Primary constructor providing complete dependency injection for custom or modified AprilTag field layouts.
     * * @param cameraName          The user-defined name of the camera configured inside the PhotonVision dashboard.
     * @param robotToCamera       The 3D transformation offset translating from the center of the robot to the camera lens.
     * @param aprilTagFieldLayout The specific physical coordinates layout configuration of AprilTags across the field.
     */
    public PhotonVisionCameraSubsystem(String cameraName, Transform3d robotToCamera, AprilTagFieldLayout aprilTagFieldLayout) {
        super(cameraName + "_PhotonVisionCameraSubsystem");

        this.camera = new PhotonCamera(cameraName);
        this.photonEstimator = new PhotonPoseEstimator(aprilTagFieldLayout, robotToCamera);
        this.pipelineResult = new PhotonPipelineResult();
    }

    /**
     * Exposes the raw underlying PhotonCamera handle.
     * * @return The active PhotonCamera object instance.
     */
    public PhotonCamera getCamera() {
        return this.camera;
    }

    /**
     * Retrieves the network identifier string assigned to this camera instance.
     * * @return The string name identifier of the camera.
     */
    public String getCameraName() {
        return getCamera().getName();
    }

    /**
     * Retrieves the most up-to-date single target telemetry frame saved during the current execution frame.
     * * @return The latest processed PhotonPipelineResult packet.
     */
    public PhotonPipelineResult getPipelineResult() {
        return this.pipelineResult;
    }

    /**
     * Collects all individual AprilTag targets detected inside the current cached vision frame.
     * * @return A List of all successfully tracked PhotonTrackedTarget objects; returns empty list if no data exists.
     */
    public List<PhotonTrackedTarget> getPipelineTargets() {
        if (this.pipelineResult == null) {
            return new ArrayList<PhotonTrackedTarget>();
        }
        return this.pipelineResult.getTargets();
    }

    /**
     * Iterates through active targets to extract a primitive long array of visible tag IDs.
     * * @return A long primitive array populated with all visible AprilTag IDs (e.g., [1, 2, 7]).
     */
    public long[] getPipelineTargetFiducialIds() {
        List<PhotonTrackedTarget> targets = getPipelineTargets();
        long[] fiducialIds = new long[targets.size()];
        for (int i = 0; i < targets.size(); i++) {
            fiducialIds[i] = targets.get(i).getFiducialId();
        }
        return fiducialIds;
    }

    /**
     * Query utility to locate a specific AprilTag by its index ID within the active tracking pipeline.
     * * @param fiducialId The integer ID of the desired AprilTag target.
     * @return The matching PhotonTrackedTarget object instance, or null if that specific tag is not currently visible.
     */
    public PhotonTrackedTarget getPipelineTarget(int fiducialId) {
        for (var target : getPipelineTargets()) {
            if (target.getFiducialId() == fiducialId) {
                return target;
            }
        }
        return null;
    }

    /**
     * Primary loop operation executing automatically once per robot scheduler cycle.
     * Drains the hardware NetworkTables queue entirely into a local cache list to safely allow
     * multiple internal down-stream consumers to reference the data without dropping packets.
     */
    @Override
    public void periodic() {
        // Poll the camera EXACTLY once per loop frame. This safely flushes the network queue 
        // and transfers ownership of the results to our local execution frame cache list.
        m_unreadResults = camera.getAllUnreadResults();
        
        if (!m_unreadResults.isEmpty()) {
            // Extrapolate and retain the absolute newest capture frame from the list for standard 2D targeting calculations
            var result = m_unreadResults.get(m_unreadResults.size() - 1);
            pipelineResult = result;
        }

        // Push spatial tracking metrics to the SmartDashboard visualizer layout
        SmartDashboard.putData(camera.getName() + " Pose", m_field);
    }

    /**
     * Integrates custom diagnostic metrics directly into WPILib's telemetry framework for LiveWindow or AdvantageScope.
     * * @param builder The UI framework SendableBuilder being configured.
     */
    @Override
    public void initSendable(SendableBuilder builder) {
        super.initSendable(builder);
        builder.addBooleanProperty("Camera Connected", () -> camera.isConnected(), null);
        builder.addDoubleProperty("# Detected Targets", () -> this.getPipelineTargets().size(), null);
        builder.addIntegerArrayProperty("Detected Target IDs", this::getPipelineTargetFiducialIds, null);
    }

    /**
     * Processes the cached sequence of local camera updates gathered during the periodic phase to 
     * compute global field coordinate tracking.
     * * @return An Optional containing an EstimatedRobotPose with 3D coordinate data and timestamping telemetry,
     * or an empty Optional if valid tag tracking was completely lost.
     */
    public Optional<EstimatedRobotPose> getEstimatedGlobalPose() {
        Optional<EstimatedRobotPose> visionEst = Optional.empty();
        
        // Loop sequentially through our local thread cache instead of requesting updates from the hardware interface again
        for (var change : m_unreadResults) {
            visionEst = photonEstimator.estimateCoprocMultiTagPose(change);
            updateEstimationStdDevs(visionEst, change.getTargets());
        }
        return visionEst;
    }

    /**
     * Interface-compliant getter wrapper generated to easily deliver localization packets straight into robot odometry correction algorithms.
     * * @return An Optional enclosing a generalized 2D pose snapshot along with calculated confidence weights.
     */
    public Optional<GeneralPoseEstimate> getGeneralPoseEstimate() {
        Optional<EstimatedRobotPose> optEstimatedPose = this.getEstimatedGlobalPose();
        if (!optEstimatedPose.isPresent()) {
            return Optional.empty();
        }
        
        EstimatedRobotPose estimatedPose = optEstimatedPose.get();
        // Flatten the 3D space calculation down onto a 2D map projection for visual feedback
        m_field.setRobotPose(estimatedPose.estimatedPose.toPose2d());
        
        return Optional.of(new GeneralPoseEstimate(
                estimatedPose.estimatedPose.toPose2d(),
                estimatedPose.timestampSeconds, 
                getEstimationStdDevs()
        ));
    }

    /**
     * Dynamically adjusts measurement confidence metrics based on the number of targets seen and their distances.
     * Greater distances or singular tag visibility result in higher variance scale outputs (lower confidence).
     * * @param estimatedPose The evaluated 3D robot positioning calculated by the estimator.
     * @param targets       The collection of raw tracked visual elements parsed from the camera frame.
     */
    private void updateEstimationStdDevs(Optional<EstimatedRobotPose> estimatedPose, List<PhotonTrackedTarget> targets) {
        if (estimatedPose.isEmpty()) {
            // Default to high standard deviation values if pose tracking is unverified
            curStdDevs = kSingleTagStdDevs;
        } else {
            var estStdDevs = kSingleTagStdDevs;
            int numTags = 0;
            double avgDist = 0;

            // Iterate through visible targets to compute the mean distance between the camera estimation and verified tag locations
            for (var tgt : targets) {
                var tagPose = photonEstimator.getFieldTags().getTagPose(tgt.getFiducialId());
                if (tagPose.isEmpty())
                    continue; // Skip the target if its ID doesn't exist within our field layout mapping
                
                numTags++;
                avgDist += tagPose
                        .get()
                        .toPose2d()
                        .getTranslation()
                        .getDistance(estimatedPose.get().estimatedPose.toPose2d().getTranslation());
            }

            if (numTags == 0) {
                curStdDevs = kSingleTagStdDevs;
            } else {
                avgDist /= numTags;
                
                if (numTags > 1) {
                    // Switch to high-accuracy base constants if multiple targets are verified
                    estStdDevs = kMultiTagStdDevs;
                }
                
                if (numTags == 1 && avgDist > 4) {
                    // If relying on only 1 tag further than 4 meters, completely discard the estimation by setting infinite standard deviation
                    estStdDevs = VecBuilder.fill(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
                } else {
                    // Exponentially scale uncertainty higher as the average distance from target structures increases
                    // Formula: estStdDevs = estStdDevs * (1 + (avgDist^2 / 30))
                    estStdDevs = estStdDevs.times(1 + (avgDist * avgDist / 30));
                }
                curStdDevs = estStdDevs;
            }
        }
    }

    /**
     * Retrieves the calculated covariance/standard deviation matrix tracking confidence for the current loop's localization.
     * * @return A standard deviation column matrix [x, y, theta]. Smaller metrics mean the vision estimate is more highly trusted.
     */
    public Matrix<N3, N1> getEstimationStdDevs() {
        return curStdDevs;
    }
}