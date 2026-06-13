package com.alumiboti5590.eyeofprovidence.robot.subsystems.vision.util;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;

/**
 * <h3>GeneralPoseEstimate</h3>
 * * An immutable data transfer object (DTO) that wraps a complete vision-based global 
 * localization measurement.
 * <p>
 * This class packages the robot's calculated 2D position, the precise timestamp of the 
 * physical camera capture, and the calculated measurement confidence metrics. It provides 
 * a unified, implementation-agnostic packet easily digested by WPILib pose estimators 
 * (e.g., {@code SwerveDrivePoseEstimator}) for sensor fusion.
 * </p>
 * * @see ICameraMeasurementProvider
 */
public class GeneralPoseEstimate {
    
    /** The estimated 2D position and rotation of the robot relative to the field origin. */
    public final Pose2d pose2d;
    
    /** * The absolute capture timestamp in seconds, derived from the FPGA clock.
     * This is critical for latency compensation, allowing the drivetrain odometry to 
     * apply this vision correction backward to the exact moment the camera shutter fired.
     */
    public final double timestampSeconds;
    
    /** * A 3x1 column matrix representing the standard deviations of the measurement [x, y, theta].
     * Drivetrain pose estimators use these values as weights: higher values mean the 
     * measurement is noisy and should be less trusted, while lower values cause the odometry 
     * to aggressively snap to the vision estimate.
     */
    public final Matrix<N3, N1> measurementStdDevs;

    /**
     * Constructs a new immutable GeneralPoseEstimate packet.
     * * @param pose2d              The estimated field-relative 2D pose of the robot.
     * @param timestampSeconds     The FPGA timestamp (in seconds) matching the moment of camera frame exposure.
     * @param measurementStdDevs  The standard deviation confidence vector [x, y, heading] for this specific frame.
     */
    public GeneralPoseEstimate(Pose2d pose2d, double timestampSeconds, Matrix<N3, N1> measurementStdDevs) {
        this.pose2d = pose2d;
        this.timestampSeconds = timestampSeconds;
        this.measurementStdDevs = measurementStdDevs;
    }
}