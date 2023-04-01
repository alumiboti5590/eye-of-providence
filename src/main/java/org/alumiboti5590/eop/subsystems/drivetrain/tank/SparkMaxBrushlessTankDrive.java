/* 2023 Written by Alumiboti FRC 5590 */
package org.alumiboti5590.eop.subsystems.drivetrain.tank;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import org.alumiboti5590.eop.pid.PIDGains;
import org.alumiboti5590.eop.subsystems.util.CANMotorConfig;

/**
 * A TankDrive implementation using Spark Max motor controllers to control <strong>brushless</strong> motors.
 * Do not use this under any circumstances with brushed motors; this is designed for Neos, Neo 550s, and other
 * brushless motors <i>only</i>.
 */
public class SparkMaxBrushlessTankDrive implements TankDrive {

    private CANSparkMax leftLeader, rightLeader;
    private CANSparkMax leftFollower, rightFollower;
    private RelativeEncoder leftLeaderEncoder, rightLeaderEncoder;
    private SparkMaxPIDController leftPIDController, rightPIDController;

    private TankDriveHelper tankDriveHelper;

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

        // Configure the followers to follow their correct leader
        this.leftFollower.follow(this.leftLeader);
        this.rightFollower.follow(this.rightLeader);

        // Configure encoders
        this.leftLeaderEncoder = this.leftLeader.getEncoder();
        this.rightLeaderEncoder = this.rightLeader.getEncoder();

        this.tankDriveHelper = new TankDriveHelper(leftLeader, rightLeader);
    }

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

    @Override
    public void resetOdometry() {
        this.resetEncoders();
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

    private void resetEncoders() {
        this.leftLeaderEncoder.setPosition(0);
        this.rightLeaderEncoder.setPosition(0);
    }
}
