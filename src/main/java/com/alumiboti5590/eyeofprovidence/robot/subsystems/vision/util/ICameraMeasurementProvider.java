package com.alumiboti5590.eyeofprovidence.robot.subsystems.vision.util;

import java.util.Optional;

/**
 * <h3>ICameraMeasurementProvider</h3>
 * * Defines a standardized contract for robot vision subsystems that compute field-relative 
 * localization measurements.
 * <p>
 * This interface decouples the main drivetrain odometry logic from specific camera hardware 
 * or tracking APIs (such as PhotonVision or Limelight). By implementing this provider, any 
 * single camera, multi-camera array, or alternative coprocessor subsystem can seamlessly 
 * stream standardized tracking metrics to a central pose estimator.
 * </p>
 */
public interface ICameraMeasurementProvider {
    
    /**
     * Retrieves the latest field-relative position estimation captured during the current execution frame.
     * <p>
     * The wrapper object {@link GeneralPoseEstimate} packages three critical components together:
     * </p>
     * <ul>
     * <li><b>Pose2d:</b> The estimated coordinates of the robot on the field map projection.</li>
     * <li><b>Timestamp:</b> The exact FPGA microsecond time when the camera image was captured, ensuring latency compensation is accurate.</li>
     * <li><b>Standard Deviations:</b> A dynamic confidence matrix weighing how much the odometry should trust this measurement.</li>
     * </ul>
     * * @return An {@link Optional} enclosing a structured {@link GeneralPoseEstimate} if targets 
     * are successfully tracked and verified; an empty {@link Optional} if tracking is lost 
     * or the position data fails confidence thresholds.
     */
    public Optional<GeneralPoseEstimate> getGeneralPoseEstimate();
}