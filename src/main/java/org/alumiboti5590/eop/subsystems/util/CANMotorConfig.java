/* 2023 Written by Alumiboti FRC 5590 */
package org.alumiboti5590.eop.subsystems.util;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

/**
 * CANMotorConfig packages together the CAN IDs of motors and if they should
 * be inverted or not. Before, these used to be stray values that would be
 * managed independently of each other, but keeping them grouped makes it
 * a bit easier to reason about.
 * @see <a href="https://docs.revrobotics.com/sparkmax/operating-modes/control-interfaces#can-interface">Spark Max CAN Documentation</a>
 * @see <a href="https://v5.docs.ctr-electronics.com/en/stable/ch08_BringUpCAN.html#set-device-ids">CTRE CAN Documentation</a>
 */
public class CANMotorConfig {

    /** The CAN bus ID of the motor */
    public final int kCANBusID;

    /** If the motor power output should be inverted or not */
    public final boolean kIsInverted;

    /** Determines the default idle mode of the motor; if it should be braking or coasting */
    public final MotorIdleMode kIdleMode;

    /**
     * Create a new CAN bus motor configuration with brake mode enabled by default
     * @param canBusID the CAN bus ID number to use
     * @param isInverted if the motor output should be inverted or not
     */
    public CANMotorConfig(int canBusID, boolean isInverted) {
        this(canBusID, isInverted, MotorIdleMode.kBrake);
    }

    /**
     * Create a new CAN bus motor configuration with brake mode enabled by default
     * @param canBusID the CAN bus ID number to use
     * @param isInverted if the motor output should be inverted or not
     * @param idleMode the desired idle mode of the motor; either brake or coasting
     */
    public CANMotorConfig(int canBusID, boolean isInverted, MotorIdleMode idleMode) {
        this.kCANBusID = canBusID;
        this.kIsInverted = isInverted;
        this.kIdleMode = idleMode;
    }

    /**
     * Converts the CAN motor config into a Spark Max controlling a brushless motor, such as a Neo or Neo 550
     * @return a Spark Max abstracted motor controller
     */
    public CANSparkMax toSparkMaxBrushless() {
        CANSparkMax motorController = new CANSparkMax(kCANBusID, MotorType.kBrushless);
        motorController.restoreFactoryDefaults();
        motorController.setInverted(kIsInverted);
        motorController.setIdleMode(this.kIdleMode.toIdleMode());
        return motorController;
    }

    /**
     * Converts the CAN motor config into a Spark Max controlling a brushed motor, such as a CIM,
     * 775, or Redline. This is very uncommon as we would usually use a Talon SRX for those motors
     * @return a Spark Max abstracted motor controller
     */
    public CANSparkMax UNSAFE_toSparkMaxBrushed() {
        CANSparkMax motorController = new CANSparkMax(kCANBusID, MotorType.kBrushed);
        motorController.restoreFactoryDefaults();
        motorController.setInverted(kIsInverted);
        motorController.setIdleMode(this.kIdleMode.toIdleMode());
        return motorController;
    }

    /**
     * Converts the CAN motor config into a Talon SRX controlling a brushed motor,
     * such as a CIM, 775, or Redline.
     * @return a Talon SRX abstracted motor controller
     */
    public TalonSRX toTalonSRX() {
        TalonSRX motorController = new TalonSRX(kCANBusID);
        motorController.configFactoryDefault();
        motorController.setInverted(kIsInverted);
        motorController.setNeutralMode(this.kIdleMode.toNeutralMode());
        return motorController;
    }

    /**
     * Converts the CAN motor config into a Talon SRX controlling a brushed motor,
     * such as a CIM, 775, or Redline.
     * @return a Talon SRX abstracted motor controller
     */
    public WPI_TalonSRX toWPITalonSRX() {
        WPI_TalonSRX motorController = new WPI_TalonSRX(kCANBusID);
        motorController.configFactoryDefault();
        motorController.setInverted(kIsInverted);
        motorController.setNeutralMode(this.kIdleMode.toNeutralMode());
        return motorController;
    }
}
