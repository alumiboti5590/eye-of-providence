/* 2023 Written by Alumiboti FRC 5590 */
package org.alumiboti5590.eop.subsystems.drivetrain.tank;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;

/**
 * TankDriveBase abstracts out the control methods needed for a Tank Drive chassis subsystem, where
 * each side of the robot acts as it's own system, similar to a tank - hence the name.
 *
 * For the most part, this class just wraps the DifferentialDrive and a few other pieces nicely.
 */
class TankDriveHelper {

    /**
     * The DifferentialDrive class comes with many of the default drive methods to avoid the team having
     * to calculate output speeds of the motors, such as tank, arcade, and curvature modes.
     */
    private DifferentialDrive differentialDrive;

    /**
     * The maximum output percentage allowed from the drivetrain, defaulting to 1.0, aka full speed
     */
    private double maxOutputPercentage = 1.0;

    /**
     * The default TankDrive configuration, with motor safety disabled, meaning it does not expect a
     * value update every iteration.
     * @param leftLeader The MotorController representing the left leader, assuming a 4-motor configuration
     * @param rightLeader The MotorController representing the right leader, assuming a 4-motor configuration
     */
    public TankDriveHelper(MotorController leftLeader, MotorController rightLeader) {
        this(leftLeader, rightLeader, false);
    }

    /**
     * The default TankDrive configuration with an option for disabling motor safety, meaning whether or not
     * it expects a value update every iteration or else the motors will stop. This could prevents the robot
     * from speeding endlessly into a wall, when enabled.
     * @param leftLeader The MotorController representing the left leader, assuming a 4-motor configuration
     * @param rightLeader The MotorController representing the right leader, assuming a 4-motor configuration
     * @param safetyEnabled If the motor safety on the DifferentialDrive should be enabled or not
     */
    public TankDriveHelper(MotorController leftLeader, MotorController rightLeader, boolean safetyEnabled) {
        this.differentialDrive = new DifferentialDrive(leftLeader, rightLeader);
        differentialDrive.setSafetyEnabled(safetyEnabled);
    }

    public void setMaxOutput(double maxOutputPercentage) {
        this.maxOutputPercentage = maxOutputPercentage;
        this.differentialDrive.setMaxOutput(maxOutputPercentage);
    }

    public double getMaxOutput() {
        return this.maxOutputPercentage;
    }

    /**
     * Curvature drive method for differential drive platform.
     *
     * The rotation argument controls the curvature of the robot's path rather than its rate of
     * heading change. This makes the robot more controllable at high speeds.
     *
     * @param xSpeed The robot's speed along the X axis [-1.0..1.0]. Forward is positive.
     * @param zRotation The normalized curvature [-1.0..1.0]. Counterclockwise is positive.
     * @param allowTurnInPlace If set, overrides constant-curvature turning for turn-in-place
     *     maneuvers. zRotation will control turning rate instead of curvature.
     * @param squareInputs If set, decreases the input sensitivity at low speeds.
     */
    public void curvatureDrive(double xSpeed, double zRotation, boolean allowTurnInPlace, boolean squareInputs) {
        // Curvature drive does _not_ allow for squaring the speed input,
        // interestingly enough (?), so we have to do that bit ourselves.
        if (squareInputs) {
            xSpeed = Math.copySign(xSpeed * xSpeed, xSpeed);
            zRotation = Math.copySign(zRotation * zRotation, zRotation);
        }
        this.differentialDrive.curvatureDrive(xSpeed, zRotation, allowTurnInPlace);
    }

    /**
     * Tank drive method for differential drive platform.
     *
     * @param leftSpeed The robot left side's speed along the X axis [-1.0..1.0]. Forward is positive.
     * @param rightSpeed The robot right side's speed along the X axis [-1.0..1.0]. Forward is
     *     positive.
     * @param squareInputs If set, decreases the input sensitivity at low speeds.
     */
    public void tankDrive(double leftSpeed, double rightSpeed, boolean squareInputs) {
        this.differentialDrive.tankDrive(leftSpeed, rightSpeed, squareInputs);
    }

    /**
     * Stops the motors.
     */
    public void stop() {
        this.differentialDrive.stopMotor();
    }
}
