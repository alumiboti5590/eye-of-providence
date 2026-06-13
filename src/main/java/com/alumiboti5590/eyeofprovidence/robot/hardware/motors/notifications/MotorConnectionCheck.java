package com.alumiboti5590.eyeofprovidence.robot.hardware.motors.notifications;

import java.util.Arrays;

import com.alumiboti5590.eyeofprovidence.network.Elastic;
import com.ctre.phoenix6.hardware.core.CoreTalonFX;
import com.revrobotics.REVLibError;
import com.revrobotics.spark.SparkBase;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * <h3>MotorConnectionCheck</h3>
 * * Provides static diagnostic utilities to verify the physical connection status 
 * of various FRC motor controllers over the CAN bus network. 
 * <p>
 * This class serves as a safety firewall during robot initialization (such as in 
 * `robotInit` or subsystem constructors). If a device is missing or unresponsive, 
 * it captures the hardware fault and pushes an alert directly to the pit crew via 
 * the Elastic dashboard notification ecosystem.
 * </p>
 * * <p>Supported ecosystems:</p>
 * <ul>
 * <li><b>REV Robotics:</b> Evaluates connection stability using {@link SparkBase#getLastError()}</li>
 * <li><b>CTRE Phoenix 6:</b> Evaluates line status using {@link CoreTalonFX#isConnected()}</li>
 * </ul>
 * * @author Team 5590 (The Alumiboti)
 * @since 2026
 */
public class MotorConnectionCheck {

    /**
     * Checks an array of REV Spark motor controllers for hardware connection faults, 
     * automatically resolving the associated subsystem's name for diagnostic logging.
     * * @param subsystem        The parent {@link SubsystemBase} managing these motors (e.g., Drivetrain)
     * @param motorControllers A variable-length list or array of {@link SparkBase} objects to verify
     * * @see #checkMotors(String, SparkBase...)
     */
    public static void checkMotors(SubsystemBase subsystem, SparkBase... motorControllers) {
        checkMotors(subsystem.getName(), motorControllers);
    }

    /**
     * Checks an array of REV Spark motor controllers for hardware connection faults.
     * <p>
     * Iterates through the provided controllers and pulls their latest status register. 
     * If the flag registers anything other than {@link REVLibError#kOk}, a high-priority 
     * dashboard alert is generated.
     * </p>
     * * @param subsystemName    The name of the subsystem to group these controllers under in the logs
     * @param motorControllers A variable-length list or array of {@link SparkBase} objects to verify
     */
    public static void checkMotors(String subsystemName, SparkBase... motorControllers) {
        Arrays.stream(motorControllers).forEach((SparkBase spark) -> {
            REVLibError error = spark.getLastError();
            if (error != REVLibError.kOk) {
                createMotorDisconnection(subsystemName, spark.getClass().getSimpleName(), spark.getDeviceId());
            }
        });
    }

    /**
     * Checks an array of CTRE Falcon 500 / Talon FX motor controllers for hardware connection faults, 
     * automatically resolving the associated subsystem's name for diagnostic logging.
     * * @param subsystem        The parent {@link SubsystemBase} managing these motors
     * @param motorControllers A variable-length list or array of {@link CoreTalonFX} objects to verify
     * * @see #checkMotors(String, CoreTalonFX...)
     */
    public static void checkMotors(SubsystemBase subsystem, CoreTalonFX... motorControllers) {
        checkMotors(subsystem.getName(), motorControllers);
    }

    /**
     * Checks an array of CTRE Falcon 500 / Talon FX motor controllers for hardware connection faults.
     * <p>
     * Uses Phoenix 6 telemetry tracking to check if the controller is actively reporting frames 
     * across the CAN bus. If the connection check returns false, a high-priority dashboard alert is generated.
     * </p>
     * * @param subsystemName    The name of the subsystem to group these controllers under in the logs
     * @param motorControllers A variable-length list or array of {@link CoreTalonFX} objects to verify
     */
    public static void checkMotors(String subsystemName, CoreTalonFX... motorControllers) {
        Arrays.stream(motorControllers).forEach((CoreTalonFX talonFX) -> {
            if (!talonFX.isConnected()) {
                createMotorDisconnection(subsystemName, talonFX.getClass().getSimpleName(), talonFX.getDeviceID());
            }
        });
    }

    /**
     * Internal factory method responsible for formatting hardware errors and dispatching 
     * a temporary 5-second alert bundle to the driver station telemetry network.
     * * @param subsystemName The name of the subsystem containing the broken hardware
     * @param deviceName    The class name/type of the faulty controller (e.g., SparkMax, TalonFX)
     * @param deviceID      The assigned CAN ID integer of the failing device
     */
    private static void createMotorDisconnection(String subsystemName, String deviceName, int deviceID) {
        String msg = String.format("%s motor controller %s (%d) is not connected", 
                subsystemName,
                deviceName,
                deviceID);
                
        String title = String.format("[%s] %s@%d Not Connected", subsystemName, deviceName, deviceID);
        
        // Generates an ERROR-level alert scheduled to persist on the UI dashboard for 5000ms
        Elastic.Notification note = new Elastic.Notification(Elastic.NotificationLevel.ERROR, title, msg, 5000);
        Elastic.sendNotification(note);
    }
}