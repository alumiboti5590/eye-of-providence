/* 2023 Written by Alumiboti FRC 5590 */
package org.alumiboti5590.eop.subsystems.drivetrain.tank;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import org.alumiboti5590.eop.components.motors.util.CANMotorConfig;
import org.alumiboti5590.eop.components.motors.util.CANSparkMaxUtil;
import org.alumiboti5590.eop.components.motors.util.CANSparkMaxUtil.Usage;
import org.alumiboti5590.eop.pid.PIDGains;

/**
 * A TankDrive implementation using Spark Max motor controllers to control <strong>brushless</strong> motors.
 * Do not use this under any circumstances with brushed motors; this is designed for Neos, Neo 550s, and other
 * brushless motors <i>only</i>.
 */
public class SparkMaxBrushlessTankDrive extends TankDrive {

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
    public static final int DEFAULT_CURRENT_LIMIT = 40;

    // ~~~~~~~~~~~~~~~~~~
    // INSTANCE VARIABLES
    // ~~~~~~~~~~~~~~~~~~

    private CANSparkMax leftLeader, rightLeader;
    private CANSparkMax leftFollower, rightFollower;
    private RelativeEncoder leftLeaderEncoder, rightLeaderEncoder;
    private SparkMaxPIDController leftPIDController, rightPIDController;

    /**
     * Creates a new drivetrain from the provided motor and optionally provided encoders
     * @param leftLeaderConfig configuration for the left leader motor controller
     * @param rightLeaderConfig configuration for the right leader motor controller
     * @param leftFollowerConfig configuration for the left follower motor controller
     * @param rightFollowerConfig configuration for the right follower motor controller
     */
    public SparkMaxBrushlessTankDrive(
            CANMotorConfig leftLeaderConfig,
            CANMotorConfig rightLeaderConfig,
            CANMotorConfig leftFollowerConfig,
            CANMotorConfig rightFollowerConfig) {
        this.leftLeader = leftLeaderConfig.toSparkMaxBrushless();
        this.rightLeader = rightLeaderConfig.toSparkMaxBrushless();
        this.leftFollower = leftFollowerConfig.toSparkMaxBrushless();
        this.rightFollower = rightFollowerConfig.toSparkMaxBrushless();
        this.configureDifferentialDrive(leftLeader, rightLeader);

        this.setAccelerationRampRate(DEFAULT_RAMP_RATE);
        this.setCurrentLimit(DEFAULT_CURRENT_LIMIT);
        this.setDrivetrainUsage(Usage.kMinimal);

        // Configure the followers to follow their correct leader
        this.leftFollower.follow(this.leftLeader);
        this.rightFollower.follow(this.rightLeader);

        // Configure encoders
        this.leftLeaderEncoder = this.leftLeader.getEncoder();
        this.rightLeaderEncoder = this.rightLeader.getEncoder();
    }

    // ~~~~~~~~~~~~~
    // CONFIGURATION
    // ~~~~~~~~~~~~~

    @Override
    public void setAccelerationRampRate(double maxAccRate) {
        this.leftLeader.setOpenLoopRampRate(maxAccRate);
        this.leftFollower.setOpenLoopRampRate(maxAccRate);
        this.rightLeader.setOpenLoopRampRate(maxAccRate);
        this.rightFollower.setOpenLoopRampRate(maxAccRate);
    }

    @Override
    public void setCurrentLimit(int currentInAmps) {
        this.leftLeader.setSmartCurrentLimit(currentInAmps);
        this.leftFollower.setSmartCurrentLimit(currentInAmps);
        this.rightLeader.setSmartCurrentLimit(currentInAmps);
        this.rightFollower.setSmartCurrentLimit(currentInAmps);
    }

    @Override
    public void setSidePIDGains(PIDGains gains) {
        if (this.leftPIDController == null) {
            this.leftPIDController = this.leftLeader.getPIDController();
        }
        if (this.rightPIDController == null) {
            this.rightPIDController = this.rightLeader.getPIDController();
        }

        SparkMaxPIDController[] controllers = new SparkMaxPIDController[] {leftPIDController, rightPIDController};
        for (SparkMaxPIDController pidController : controllers) {
            pidController.setP(gains.kP);
            pidController.setI(gains.kI);
            pidController.setD(gains.kD);
            pidController.setFF(gains.kFF);
            pidController.setIZone(gains.kIzone);
            pidController.setOutputRange(gains.kPeakMinOutput, gains.kPeakMaxOutput);
        }
    }

    /**
     * The following allows us to save precious time in our 20ms robot loop by not
     * evaluating the Spark Max values any more than we really need to. It's recommended
     * to set this (although the code might do it automatically) before switching up the
     * drive mode between position-based, velocity-based, or open.
     * @param desiredUsage the short-term desired usage for the motor
     */
    public void setDrivetrainUsage(Usage desiredUsage) {
        CANSparkMaxUtil.setCANSparkMaxBusUsage(leftLeader, desiredUsage, true);
        CANSparkMaxUtil.setCANSparkMaxBusUsage(rightLeader, desiredUsage, true);
        CANSparkMaxUtil.setCANSparkMaxBusUsage(leftLeader, Usage.kMinimal);
        CANSparkMaxUtil.setCANSparkMaxBusUsage(rightLeader, Usage.kMinimal);
    }

    // ~~~~~~~~
    // ODOMETRY
    // ~~~~~~~~

    @Override
    public void resetOdometry() {
        this.resetEncoders();
    }

    private void resetEncoders() {
        this.leftLeaderEncoder.setPosition(0);
        this.rightLeaderEncoder.setPosition(0);
    }
}
