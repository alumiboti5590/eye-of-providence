/* 2023 Written by Alumiboti FRC 5590 */
package com.alumiboti5590.eop.components;

import edu.wpi.first.wpilibj.DigitalInput;

/**
 * Shortcut wrapper around the WPI {@code DigitalInput} class for a Digital
 * I/O-configured Limit Switch.
 */
public class LimitSwitch extends DigitalInput {

    /**
     * Create an instance of a LimitSwitch class. Creates a digital input given a channel.
     *
     * @param channel the DIO channel for the digital input
     */
    public LimitSwitch(int channel) {
        super(channel);
    }

    /**
     * Same as {@code get()}, but it's a bit more clearer on the physical result
     * of the limit switch being pressed and engaged, or not.
     * @return If the switch is engaged or not.
     */
    public boolean isEngaged() {
        return this.get();
    }
}
