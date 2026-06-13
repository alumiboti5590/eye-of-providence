package com.alumiboti5590.eyeofprovidence.driverstation.controllers;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * <h3>XboxController</h3>
 * * An upgraded wrapper around WPILib's native {@link CommandXboxController} that
 * implements centralized, configurable deadzone filtering across all analog inputs.
 * <p>
 * Physical gamepad joysticks and triggers suffer from mechanical tolerances, wear,
 * and structural "slop" that prevent them from returning perfectly to 0.0 when released.
 * Without a deadband filter, this minor electrical jitter manifests as uncommanded
 * robot drift on the field.
 * </p>
 * *
 * <p>
 * This class  overrides the native axis and trigger methods, filtering out inputs
 * below the configured thresholds using {@link MathUtil#applyDeadband(double, double)}.
 * WPILib's algorithm flattens inputs inside the deadzone to 0.0, and linearly rescales the
 * remaining active area from 0.0 to 1.0 to preserve smooth, full-range driver resolution.
 * </p>
 * * @author Team 5590 (The Alumiboti)
 * 
 * @since 2026
 */
public class XboxController extends CommandXboxController {

    /**
     * The default deadzone boundary (7.5%) applied to axes and triggers
     * if no explicit threshold override is specified.
     */
    private static final double DEFAULT_DEADZONE_THRESHOLD = .02;

    /** The filtering boundary below which joystick inputs are discarded and flattened to 0.0. */
    private double m_stickDeadzoneThreshold;

    /** The filtering boundary below which analog trigger inputs are discarded and flattened to 0.0. */
    private double m_triggerDeadzoneThreshold;

    /**
     * Constructs a new XboxController on the specified Driver Station USB port using
     * the default 7.5% deadzone thresholds for both sticks and triggers.
     * * @param port The USB slot ID index assigned to this controller within the FRC Driver Station (0-5).
     */
    public XboxController(int port) {
        this(port, DEFAULT_DEADZONE_THRESHOLD, DEFAULT_DEADZONE_THRESHOLD);
    }

    /**
     * Constructs a new XboxController on the specified Driver Station USB port with
     * fully customizable stick and trigger deadzone boundaries.
     * * @param port The USB slot ID index assigned to this controller within the FRC Driver Station (0-5).
     * 
     * @param stickDeadzone
     *            The minimum absolute value required to register joystick motion (0.0 to 1.0).
     * @param triggerDeadzone
     *            The minimum absolute value required to register trigger pull activity (0.0 to 1.0).
     */
    public XboxController(int port, double stickDeadzone, double triggerDeadzone) {
        super(port);
        this.m_stickDeadzoneThreshold = stickDeadzone;
        this.m_triggerDeadzoneThreshold = triggerDeadzone;
    }

    /**
     * Returns the deadband-filtered horizontal (X) value of the left joystick.
     * * @return The filtered axis position between -1.0 (left) and 1.0 (right). Returns 0.0 if within the deadband.
     */
    @Override
    public double getLeftX() {
        return MathUtil.applyDeadband(super.getLeftX(), this.m_stickDeadzoneThreshold);
    }

    /**
     * Returns the deadband-filtered vertical (Y) value of the left joystick.
     * <p>
     * Note: In compliance with standard FRC/WPILib conventions, pulling the stick
     * backward yields a positive value, while pushing it forward yields a negative value.
     * </p>
     * * @return The filtered axis position between -1.0 (forward) and 1.0 (backward). Returns 0.0 if within the
     * deadband.
     */
    @Override
    public double getLeftY() {
        return MathUtil.applyDeadband(super.getLeftY(), this.m_stickDeadzoneThreshold);
    }

    /**
     * Returns the deadband-filtered horizontal (X) value of the right joystick.
     * * @return The filtered axis position between -1.0 (left) and 1.0 (right). Returns 0.0 if within the deadband.
     */
    @Override
    public double getRightX() {
        return MathUtil.applyDeadband(super.getRightX(), this.m_stickDeadzoneThreshold);
    }

    /**
     * Returns the deadband-filtered vertical (Y) value of the right joystick.
     * <p>
     * Note: In compliance with standard FRC/WPILib conventions, pulling the stick
     * backward yields a positive value, while pushing it forward yields a negative value.
     * </p>
     * * @return The filtered axis position between -1.0 (forward) and 1.0 (backward). Returns 0.0 if within the
     * deadband.
     */
    @Override
    public double getRightY() {
        return MathUtil.applyDeadband(super.getRightY(), this.m_stickDeadzoneThreshold);
    }

    /**
     * Returns a command-bindable {@link Trigger} instance representing the state of the left analog trigger.
     * <p>
     * The trigger is considered "active" (true) once its physical depression value
     * moves past the controller's designated trigger deadzone threshold.
     * </p>
     * * @return A {@link Trigger} object tracking the active/inactive boolean threshold state of the left trigger.
     */
    @Override
    public Trigger leftTrigger() {
        return leftTrigger(this.m_triggerDeadzoneThreshold);
    }

    /**
     * Returns a command-bindable {@link Trigger} instance representing the state of the right analog trigger.
     * <p>
     * The trigger is considered "active" (true) once its physical depression value
     * moves past the controller's designated trigger deadzone threshold.
     * </p>
     * * @return A {@link Trigger} object tracking the active/inactive boolean threshold state of the right trigger.
     */
    @Override
    public Trigger rightTrigger() {
        return rightTrigger(this.m_triggerDeadzoneThreshold);
    }
}