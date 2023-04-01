/* 2023 Written by Alumiboti FRC 5590 */
package org.alumiboti5590.eop.subsystems.util;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.revrobotics.CANSparkMax.IdleMode;

/**
 * A single interface for controlling motor idles that allows for spanning RevRobotics or CTRE libraries.
 */
public enum MotorIdleMode {

    /** The motor will not 'fight' any external forces against it when the speed is set to zero */
    kCoast(false),
    /** The motor will attempt to 'soft brake' itself when the speed is set to zero */
    kBrake(true);

    private final boolean kIsBrakeEnabled;

    private MotorIdleMode(boolean isBrakeEnabled) {
        this.kIsBrakeEnabled = isBrakeEnabled;
    }

    /**
     * Returns the corresponding value of RevRobotics' IdleMode
     * @return the corresponding IdleMode
     */
    public IdleMode toIdleMode() {
        return this.kIsBrakeEnabled ? IdleMode.kBrake : IdleMode.kCoast;
    }

    /**
     * Returns the corresponding value of CTRE's NeutralMode
     * @return the corresponding NeutralMode
     */
    public NeutralMode toNeutralMode() {
        return this.kIsBrakeEnabled ? NeutralMode.Brake : NeutralMode.Coast;
    }
}
