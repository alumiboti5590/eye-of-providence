/* 2023 Written by Alumiboti FRC 5590 */
package org.alumiboti5590.eop.subsystems.drivetrain.tank;

import org.alumiboti5590.eop.pid.PIDGains;

/**
 * TankDriveBase abstracts out the control methods needed for a Tank Drive chassis subsystem, where
 * each side of the robot acts as it's own system, similar to a tank - hence the name.
 *
 * For the most part, this class just wraps the DifferentialDrive and a few other pieces nicely.
 */
public interface TankDrive {

    // ~~~~~~~~~~~~~
    // CONFIGURATION
    // ~~~~~~~~~~~~~

    /**
     * Sets the ramp rate for open loop control modes.
     *
     * <p>This is the maximum rate at which the motor controller's output is allowed to change.
     *
     * @param maxAccRate Time in seconds to go from 0 to full throttle.
     */
    public void setAccelerationRampRate(double maxAccRate);

    /**
     * Sets the current limit in Amps.
     *
     * <p>The motor controller will reduce the controller voltage output to avoid surpassing this
     * limit. This limit is enabled by default and used for brushless only. This limit is highly
     * recommended when brushless motors, but it also helps to avoid brownouts in any situation.
     * @param currentInAmps The current limit in Amps.
     */
    public void setCurrentLimit(int currentInAmps);

    /**
     * Set the PID controller gains for both sides of the tank drive. Note that the actual effect
     * will be determined by the motors used (Neos + Spark Max's have a lot more control because of
     * their built-in encoder) but it tries to be fairly equal enough for a drivetrain to work.
     * @param gains desired PID gains
     */
    public void setSidePIDGains(PIDGains gains);

    // ~~~~~~~~
    // MOVEMENT
    // ~~~~~~~~

    /**
     * Given a movement speed and a rotational speed, move the robot. The rotation argument controls
     * the curvature of the robot's path rather than its rate of heading change. This makes the
     * robot more controllable at high speeds.
     *
     * @param xSpeed The robot's speed along the X axis [-1.0..1.0]. Forward is positive.
     * @param zRotation The normalized curvature [-1.0..1.0]. Counterclockwise is positive.
     * @param allowTurnInPlace If set, overrides constant-curvature turning for turn-in-place
     *     maneuvers. zRotation will control turning rate instead of curvature.
     * @param squareInputs If set, decreases the input sensitivity at low speeds.
     */
    public void curvatureDrive(double xSpeed, double zRotation, boolean allowTurnInPlace, boolean squareInputs);

    /**
     * Given a speed for the left and right side, respectively, set the motor speeds as given. This is
     * the more difficult control mode for a drivetrain.
     *
     * @param leftSpeed The robot left side's speed along the X axis [-1.0..1.0]. Forward is positive.
     * @param rightSpeed The robot right side's speed along the X axis [-1.0..1.0]. Forward is
     *     positive.
     * @param squareInputs If set, decreases the input sensitivity at low speeds.
     */
    public void tankDrive(double leftSpeed, double rightSpeed, boolean squareInputs);

    /**
     * Stops both sides of the tank drive when called.
     */
    public void stop();

    // ~~~~~~~~
    // ODOMETRY
    // ~~~~~~~~

    /**
     * Reset the encoders and gyroscope readings.
     */
    public void resetOdometry();
}
