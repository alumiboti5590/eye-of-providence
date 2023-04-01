/* 2023 Written by Alumiboti FRC 5590 */
package org.alumiboti5590.eop.subsystems.drivetrain.tank;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import org.alumiboti5590.eop.pid.PIDGains;

/**
 * TankDrive abstracts out the control methods needed for a Tank Drive chassis subsystem, where
 * each side of the robot acts as it's own system, similar to a tank - hence the name.
 *
 * For the most part, this class just wraps the DifferentialDrive and a few other pieces nicely.
 * You will have to use one of the more concrete implementations to configure the motors
 * and other components correctly.
 */
public abstract class TankDrive extends SubsystemBase {

    /**
     * TeleopDriveType is an enumeration that allows for selecting the drive type during
     * the tele-operated period for a tank drive chassis.
     */
    public enum TeleopDriveType {
        kCurvature("Curvature Drive"),
        kTank("Tank Drive");

        private final String kChooserName;

        private TeleopDriveType(String chooserName) {
            this.kChooserName = chooserName;
        }

        public String getName() {
            return this.kChooserName;
        }
    }

    /** The default drive type to use if one is not selected in the SmartDashboard */
    public static final TeleopDriveType DEFAULT_TELEOP_DRIVE_TYPE = TeleopDriveType.kCurvature;

    // ~~~~~~~~~~~~~~~~
    // Teleop Suppliers
    // ~~~~~~~~~~~~~~~~

    DoubleSupplier curvatureSpeedSupplier;
    DoubleSupplier curvatureRotationSupplier;
    BooleanSupplier curvatureQuickTurnSupplier;

    DoubleSupplier tankDriveLeftSpeedSupplier;
    DoubleSupplier tankDriveRightSpeedSupplier;

    // ~~~~~~~~~~~~~~~~~~
    // INSTANCE VARIABLES
    // ~~~~~~~~~~~~~~~~~~

    /**
     * The DifferentialDrive class comes with many of the default drive methods to avoid the team having
     * to calculate output speeds of the motors, such as tank, arcade, and curvature modes.
     */
    private DifferentialDrive differentialDrive;

    /**
     * The drive type used during teleop periods.
     */
    private TeleopDriveType teleopDriveType = DEFAULT_TELEOP_DRIVE_TYPE;

    /** Used for determining the drive type when entering teleop mode */
    private static SendableChooser<TeleopDriveType> teleopDriveTypeChooser;

    static {
        teleopDriveTypeChooser = new SendableChooser<TeleopDriveType>();
        teleopDriveTypeChooser.setDefaultOption(DEFAULT_TELEOP_DRIVE_TYPE.getName(), DEFAULT_TELEOP_DRIVE_TYPE);
        for (TeleopDriveType driveType : TeleopDriveType.values()) {
            teleopDriveTypeChooser.addOption(driveType.getName(), driveType);
        }
    }

    private boolean squareTeleopInputs = true;
    private boolean invertCurvatureSteering = false;

    // ~~~~~~~~~~~~~~~~
    // TELEOP SUPPLIERS
    // ~~~~~~~~~~~~~~~~

    /**
     * Set the speed supplier for curvature drive mode
     * @param speedProvider a supplier that is used to control the speed in curvature mode
     * @return The TankDrive instance to be a part of a call chain
     */
    public TankDrive setCurvatureSpeedSupplier(DoubleSupplier speedProvider) {
        this.curvatureSpeedSupplier = speedProvider;
        return this;
    }

    /**
     * Set the rotation supplier for curvature drive mode
     * @param steeringProvider a supplier that is used to control the rotation in curvature mode
     * @return The TankDrive instance to be a part of a call chain
     */
    public TankDrive setCurvatureSteeringSupplier(DoubleSupplier steeringProvider) {
        this.curvatureRotationSupplier = steeringProvider;
        return this;
    }

    /**
     * Set if the robot should be allowed to 'quick turn' in curvature mode
     * @param quickTurnSupplier a supplier that is used to control 'quick turn' toggling in curvature mode
     * @return The TankDrive instance to be a part of a call chain
     */
    public TankDrive setCurvatureQuickTurnSupplier(BooleanSupplier quickTurnSupplier) {
        this.curvatureQuickTurnSupplier = quickTurnSupplier;
        return this;
    }

    /**
     * Set the speed supplier for the left-side of the tank drive control
     * @param speedProvider a supplier that is used for driving the left-side in tank drive
     * @return The TankDrive instance to be a part of a call chain
     */
    public TankDrive setTankDriveLeftSpeedSupplier(DoubleSupplier speedProvider) {
        this.tankDriveLeftSpeedSupplier = speedProvider;
        return this;
    }

    /**
     * Set the speed supplier for the right-side of the tank drive control
     * @param speedProvider a supplier that is used for driving the right-side in tank drive
     * @return The TankDrive instance to be a part of a call chain
     */
    public TankDrive setTankDriveRightSpeedSupplier(DoubleSupplier speedProvider) {
        this.tankDriveRightSpeedSupplier = speedProvider;
        return this;
    }

    // ~~~~~~~~~~~~~~~~~~~
    // BASIC CONFIGURATION
    // ~~~~~~~~~~~~~~~~~~~

    /**
     * Sets if the curvature steering input should be inverted
     * @param invertSteeringInput If the curvature steering input should be inverted
     * @return The TankDrive instance to be a part of a call chain
     */
    public TankDrive setInvertCurvatureSteering(boolean invertSteeringInput) {
        this.invertCurvatureSteering = invertSteeringInput;
        return this;
    }

    /**
     * If configured - which it is by default - then the speed inputs will be squared
     * meaning less sensitivity at lower levels.
     * @param squareTeleopInputs If the speed inputs should be squared
     * @return The TankDrive instance to be a part of a call chain
     */
    public TankDrive setSquareTeleopInputs(boolean squareTeleopInputs) {
        this.squareTeleopInputs = squareTeleopInputs;
        return this;
    }

    /**
     * Set the drive type to use in Tele-operated mode by the driver
     * @param driveType The drive type to use
     * @return The TankDrive instance to be a part of a call chain
     */
    public TankDrive setTeleopDriveType(TeleopDriveType driveType) {
        this.teleopDriveType = driveType;
        return this;
    }

    /**
     * Returns the SendableChooser needed to pick the desired drive type from the SmartDashboard / Shuffleboard
     * @return The SendableChooser that can be sent to the SmartDashboard
     */
    public SendableChooser<TeleopDriveType> getTeleopDriveTypeSendableChooser() {
        return teleopDriveTypeChooser;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~
    // ADVANCED CONFIGURATION
    // ~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Sets the ramp rate for open loop control modes.
     *
     * <p>This is the maximum rate at which the motor controller's output is allowed to change.
     *
     * @param maxAccRate Time in seconds to go from 0 to full throttle.
     */
    public abstract void setAccelerationRampRate(double maxAccRate);

    /**
     * Sets the current limit in Amps.
     *
     * <p>The motor controller will reduce the controller voltage output to avoid surpassing this
     * limit. This limit is enabled by default and used for brushless only. This limit is highly
     * recommended when brushless motors, but it also helps to avoid brownouts in any situation.
     * @param currentInAmps The current limit in Amps.
     */
    public abstract void setCurrentLimit(int currentInAmps);

    /**
     * Set the PID controller gains for both sides of the tank drive. Note that the actual effect
     * will be determined by the motors used (Neos + Spark Max's have a lot more control because of
     * their built-in encoder) but it tries to be fairly equal enough for a drivetrain to work.
     * @param gains desired PID gains
     */
    public abstract void setSidePIDGains(PIDGains gains);

    // ~~~~~~~~
    // MOVEMENT
    // ~~~~~~~~

    /**
     * The main method to control the drivetrain during teleop. It controls the chassis based
     * on the current teleopDriveType and it requires the correct suppliers to be defined to
     * fetch controller values.
     */
    public void teleopOpenDrive() {
        switch (this.teleopDriveType) {
            case kCurvature:
                double forwardSpeed = this.curvatureSpeedSupplier.getAsDouble();
                double steeringInput = this.curvatureRotationSupplier.getAsDouble();
                steeringInput = this.invertCurvatureSteering ? -steeringInput : steeringInput;
                boolean allowQuickTurn = this.curvatureQuickTurnSupplier.getAsBoolean();
                this.curvatureDrive(forwardSpeed, steeringInput, allowQuickTurn, squareTeleopInputs);
                break;
            case kTank:
                double leftTankTreadSpeed = this.tankDriveLeftSpeedSupplier.getAsDouble();
                double rightTankTreadSpeed = this.tankDriveRightSpeedSupplier.getAsDouble();
                this.tankDrive(leftTankTreadSpeed, rightTankTreadSpeed, squareTeleopInputs);
                break;
        }
    }

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
     * Given a speed for the left and right side, respectively, set the motor speeds as given. This is
     * the more difficult control mode for a drivetrain.
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
     * Stops both sides of the tank drive when called.
     */
    public void stop() {
        this.differentialDrive.stopMotor();
    }

    // ~~~~~~~~
    // ODOMETRY
    // ~~~~~~~~

    /**
     * Reset the encoders and gyroscope readings.
     */
    public abstract void resetOdometry();

    // ~~~~~~~~~
    // INTERNALS
    // ~~~~~~~~~

    protected void configureDifferentialDrive(MotorController leftLeader, MotorController rightLeader) {
        this.differentialDrive = new DifferentialDrive(leftLeader, rightLeader);
        this.differentialDrive.setSafetyEnabled(false);
    }
}
