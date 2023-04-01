/* 2023 Written by Alumiboti FRC 5590 */
package org.alumiboti5590.eop.subsystems.drivetrain.tank;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.Encoder;
import org.alumiboti5590.eop.pid.PIDGains;
import org.alumiboti5590.eop.subsystems.util.CANMotorConfig;

/**
 * A TankDrive implementation using Talon SRX motor controllers to control <strong>brushed</strong> motors.
 * Do not use this under any circumstances with brushless motors; this is designed for CIM, 775, Redline and other
 * brushed motors <i>only</i>.
 */
public class TalonSRXBrushedTankDrive implements TankDrive {

    // ~~~~~~~~~~~~~~~~~~~~
    // CONSTANTS & DEFAULTS
    // ~~~~~~~~~~~~~~~~~~~~

    /**
     * The default ramp rate, meaning the motors will take this long (in seconds) to
     * accelerate to full speed to prevent damage to the motor or subsystems. In drivetrains,
     * this also helps to prevent flip-overs from sudden acceleration.
     */
    public static final double DEFAULT_RAMP_RATE = .3;

    /**
     * Limit the motors to the following AMPS to prevent brownouts, trips, or too much
     * power being drawn when it really isn't needed. This is a nice safety feature.
     */
    public static final int DEFAULT_CURRENT_LIMIT = 30;

    // ~~~~~~~~~~~~~~~~~~
    // INSTANCE VARIABLES
    // ~~~~~~~~~~~~~~~~~~

    private WPI_TalonSRX leftLeader, rightLeader;
    private WPI_TalonSRX leftFollower, rightFollower;
    private Encoder leftLeaderEncoder, rightLeaderEncoder;
    PIDController leftPIDController, rightPIDController;

    private TankDriveHelper tankDriveHelper;

    /**
     * Creates a new drivetrain from the provided motor and optionally provided encoders
     * @param leftLeaderConfig configuration for the left leader motor controller
     * @param rightLeaderConfig configuration for the right leader motor controller
     * @param leftFollowerConfig configuration for the left follower motor controller
     * @param rightFollowerConfig configuration for the right follower motor controller
     * @param leftEncoder the encoder to be associated with the left side; must be in-phase where forward is positive
     * @param rightEncoder the encoder to be associated with the right side; must be in-phase where forward is positive
     */
    public TalonSRXBrushedTankDrive(
            CANMotorConfig leftLeaderConfig,
            CANMotorConfig rightLeaderConfig,
            CANMotorConfig leftFollowerConfig,
            CANMotorConfig rightFollowerConfig,
            Encoder leftEncoder,
            Encoder rightEncoder) {
        this.leftLeader = leftLeaderConfig.toWPITalonSRX();
        this.rightLeader = rightLeaderConfig.toWPITalonSRX();
        this.leftFollower = leftFollowerConfig.toWPITalonSRX();
        this.rightFollower = rightFollowerConfig.toWPITalonSRX();

        // Configure the followers to follow their correct leader
        this.leftFollower.follow(this.leftLeader);
        this.rightFollower.follow(this.rightLeader);

        this.setAccelerationRampRate(DEFAULT_RAMP_RATE);
        this.setCurrentLimit(DEFAULT_CURRENT_LIMIT);

        // Configure encoders
        if (leftEncoder != null) {
            this.leftLeaderEncoder = leftEncoder;
        }
        if (rightEncoder != null) {
            this.rightLeaderEncoder = rightEncoder;
        }

        this.tankDriveHelper = new TankDriveHelper(leftLeader, rightLeader);
    }

    // ~~~~~~~~
    // MOVEMENT
    // ~~~~~~~~

    @Override
    public void curvatureDrive(double xSpeed, double zRotation, boolean allowTurnInPlace, boolean squareInputs) {
        this.tankDriveHelper.curvatureDrive(xSpeed, zRotation, allowTurnInPlace, squareInputs);
    }

    @Override
    public void tankDrive(double leftSpeed, double rightSpeed, boolean squareInputs) {
        this.tankDriveHelper.tankDrive(leftSpeed, rightSpeed, squareInputs);
    }

    @Override
    public void stop() {
        this.tankDriveHelper.stop();
    }

    // ~~~~~~~~~~~~~
    // CONFIGURATION
    // ~~~~~~~~~~~~~

    public void setAccelerationRampRate(double maxAccRate) {
        this.leftLeader.configOpenloopRamp(maxAccRate);
        this.rightLeader.configOpenloopRamp(maxAccRate);
        this.leftFollower.configOpenloopRamp(maxAccRate);
        this.rightFollower.configOpenloopRamp(maxAccRate);
    }

    public void setCurrentLimit(int currentInAmps) {
        this.leftLeader.configPeakCurrentLimit(currentInAmps);
        this.rightLeader.configPeakCurrentLimit(currentInAmps);
        this.leftFollower.configPeakCurrentLimit(currentInAmps);
        this.rightFollower.configPeakCurrentLimit(currentInAmps);
    }

    @Override
    public void setSidePIDGains(PIDGains gains) {
        this.leftPIDController = new PIDController(gains.kP, gains.kI, gains.kD);
        this.rightPIDController = new PIDController(gains.kP, gains.kI, gains.kD);
    }

    // ~~~~~~~~
    // ODOMETRY
    // ~~~~~~~~

    @Override
    public void resetOdometry() {
        this.resetEncoders();
    }

    private void resetEncoders() {
        this.leftLeaderEncoder.reset();
        this.rightLeaderEncoder.reset();
    }
}
