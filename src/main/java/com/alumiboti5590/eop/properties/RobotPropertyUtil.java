/* 2023 Written by Alumiboti FRC 5590 */
package com.alumiboti5590.eop.properties;

import com.alumiboti5590.eop.components.LimitSwitch;
import com.alumiboti5590.eop.components.motors.util.CANMotorConfig;
import com.alumiboti5590.eop.components.motors.util.MotorIdleMode;
import edu.wpi.first.wpilibj.Encoder;

/**
 * RobotPropertyUtil is used to shortcut many component creations using common
 * naming conventions such as motors, encoders, and more.
 */
public class RobotPropertyUtil {

    /**
     * Shortcut to return a {@code CANMotorConfig} using the {@code RobotProperty} class and a motor name.
     * It expects a few properties to be defined prefixed with whatever is passed as
     * {@code motorName}.
     * 
     * <ul>
     *   <li>{@code <motorName>_id} (integer): The CAN ID of the motor.</li>
     *   <li>{@code <motorName>_inverted} (boolean): If the motor should be inverted. Defaults to {@code false}.</li>
     *   <li>{@code <motorName>_brake_mode_enabled} (boolean): If the motor should be inverted. Defaults to {@code false}.</li>
     * </ul>
     * 
     * @param motorName The motor name to fetch the appropriate values for.
     * @return The CANMotorConfig generated from the dynamic property values.
     */
    public static CANMotorConfig getCANMotorConfig(String motorName) {
        int canBusID = RobotProperty.getInt(String.format("%s_id", motorName));
        boolean isInverted = RobotProperty.getBoolean(String.format("%s_inverted", motorName), false);
        boolean brakeMode = RobotProperty.getBoolean(String.format("%s_brake_mode_enabled", motorName), false);
        return new CANMotorConfig(canBusID, isInverted, brakeMode ? MotorIdleMode.kBrake : MotorIdleMode.kCoast);
    }

    /**
     * Shortcut to return a {@code CANMotorConfig} using the {@code RobotProperty} class and an encoder name.
     * It expects a few properties to be defined prefixed with whatever is passed as
     * {@code encoderName}.
     * 
     * <ul>
     *   <li>{@code <encoderName>_chan_a_id} (integer): The DIO ID of channel A of the encoder.</li>
     *   <li>{@code <encoderName>_chan_b_id} (integer): The DIO ID of channel B of the encoder.</li>
     * </ul>
     * @param encoderName The encoder name to fetch the appropriate values for.
     * 
     * @return The Encoder generated from the dynamic property values.
     */
    public static Encoder getEncoder(String encoderName) {
        int chanADioID = RobotProperty.getInt(String.format("%s_chan_a_id", encoderName));
        int chanBDioID = RobotProperty.getInt(String.format("%s_chan_b_id", encoderName));
        return new Encoder(chanADioID, chanBDioID);
    }

    /**
     * Shortcut to return a {@code LimitSwitch} using the {@code RobotProperty} class and a motor name.
     * 
     * <p>It expects an integer {@code <switchName>_limit_switch_dio_id} to be defined prefixed with whatever is passed as
     * {@code switchName}.
     * 
     * @param switchName The limit switch name to fetch the appropriate values for.
     * @return The LimitSwitch generated from the dynamic property values.
     */
    public static LimitSwitch getLimitSwitch(String switchName) {
        int dioChanID = RobotProperty.getInt(String.format("%s_limit_switch_dio_id", switchName));
        return new LimitSwitch(dioChanID);
    }
}
