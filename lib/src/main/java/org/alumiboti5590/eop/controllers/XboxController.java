/* 2023 Written by Alumiboti FRC 5590 */
package org.alumiboti5590.eop.controllers;

import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * A subclass of the WPILib XboxController containing useful additions such as deadzone management
 * and Triggers for Command-based programming. It aims to be a mix between XboxController and
 * CommandXboxController which are provided by WPILib. This class works for both Xbox 360 and Xbox One controllers.
 * @see <a href="https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/wpilibj/XboxController.html">WPILib XboxController</a>
 * @see <a href="https://docs.wpilib.org/en/stable/docs/software/basic-programming/joystick.html">Joysticks on WPILib</a>
 */
public class XboxController extends edu.wpi.first.wpilibj.XboxController {

    /** The minimum value needed by the left and right triggers to be considered "pressed" like a button */
    private double triggerAsTriggerDeadzones = .2;

    public XboxController(int port) {
        super(port);
    }

    public XboxController(int port, double triggerAsTriggerDeadzones) {
        super(port);
        this.triggerAsTriggerDeadzones = triggerAsTriggerDeadzones;
    }

    /**
     * Returns the A button press as a Trigger to be used in Command-based programming.
     * @see <a href="https://docs.wpilib.org/en/stable/docs/software/commandbased/binding-commands-to-triggers.html#trigger-bindings">Binding Commands to Triggers</a>
     * @return the button as a Trigger
     */
    public Trigger getAButtonTrigger() {
        return new Trigger(this::getAButton);
    }

    /**
     * Returns the Back button press as a Trigger to be used in Command-based programming.
     * @see <a href="https://docs.wpilib.org/en/stable/docs/software/commandbased/binding-commands-to-triggers.html#trigger-bindings">Binding Commands to Triggers</a>
     * @return the button as a Trigger
     */
    public Trigger getBackButtonTrigger() {
        return new Trigger(this::getBackButton);
    }

    /**
     * Returns the B button press as a Trigger to be used in Command-based programming.
     * @see <a href="https://docs.wpilib.org/en/stable/docs/software/commandbased/binding-commands-to-triggers.html#trigger-bindings">Binding Commands to Triggers</a>
     * @return the button as a Trigger
     */
    public Trigger getBButtonTrigger() {
        return new Trigger(this::getBButton);
    }

    /**
     * Returns the Left Bumper button press as a Trigger to be used in Command-based programming.
     * @see <a href="https://docs.wpilib.org/en/stable/docs/software/commandbased/binding-commands-to-triggers.html#trigger-bindings">Binding Commands to Triggers</a>
     * @return the button as a Trigger
     */
    public Trigger getLeftBumperTrigger() {
        return new Trigger(this::getLeftBumper);
    }

    /**
     * Returns the Left Stick button press as a Trigger to be used in Command-based programming.
     * @see <a href="https://docs.wpilib.org/en/stable/docs/software/commandbased/binding-commands-to-triggers.html#trigger-bindings">Binding Commands to Triggers</a>
     * @return the button as a Trigger
     */
    public Trigger getLeftStickButtonTrigger() {
        return new Trigger(this::getLeftStickButton);
    }

    /**
     * Returns if the Left Trigger axis is pushed in enough to be considered "pressed", using {@link #triggerAsTriggerDeadzones}
     * as the minimum value required to be considered "pressed".
     * @return if the trigger is pressed or not
     */
    public boolean getLeftTriggerPressed() {
        return this.getLeftTriggerAxis() > triggerAsTriggerDeadzones;
    }

    /**
     * Returns the Left Trigger pressed as a Trigger to be used in Command-based programming.
     * @see <a href="https://docs.wpilib.org/en/stable/docs/software/commandbased/binding-commands-to-triggers.html#trigger-bindings">Binding Commands to Triggers</a>
     * @return the button as a Trigger
     */
    public Trigger getLeftTrigger() {
        return new Trigger(this::getLeftTriggerPressed);
    }

    /**
     * Returns the Right Bumper button press as a Trigger to be used in Command-based programming.
     * @see <a href="https://docs.wpilib.org/en/stable/docs/software/commandbased/binding-commands-to-triggers.html#trigger-bindings">Binding Commands to Triggers</a>
     * @return the button as a Trigger
     */
    public Trigger getRightBumperTrigger() {
        return new Trigger(this::getRightBumper);
    }

    /**
     * Returns the Right Stick button press as a Trigger to be used in Command-based programming.
     * @see <a href="https://docs.wpilib.org/en/stable/docs/software/commandbased/binding-commands-to-triggers.html#trigger-bindings">Binding Commands to Triggers</a>
     * @return the button as a Trigger
     */
    public Trigger getRightStickButtonTrigger() {
        return new Trigger(this::getRightStickButton);
    }

    /**
     * Returns if the Right Trigger axis is pushed in enough to be considered "pressed", using {@link #triggerAsTriggerDeadzones}
     * as the minimum value required to be considered "pressed".
     * @return if the trigger is pressed or not
     */
    public boolean getRightTriggerPressed() {
        return this.getRightTriggerAxis() > triggerAsTriggerDeadzones;
    }

    /**
     * Returns the Right Trigger pressed as a Trigger to be used in Command-based programming.
     * @see <a href="https://docs.wpilib.org/en/stable/docs/software/commandbased/binding-commands-to-triggers.html#trigger-bindings">Binding Commands to Triggers</a>
     * @return the button as a Trigger
     */
    public Trigger getRightTrigger() {
        return new Trigger(this::getLeftTriggerPressed);
    }

    /**
     * Returns the Start button press as a Trigger to be used in Command-based programming.
     * @see <a href="https://docs.wpilib.org/en/stable/docs/software/commandbased/binding-commands-to-triggers.html#trigger-bindings">Binding Commands to Triggers</a>
     * @return the button as a Trigger
     */
    public Trigger getStartButtonTrigger() {
        return new Trigger(this::getStartButton);
    }

    /**
     * Returns the X button press as a Trigger to be used in Command-based programming.
     * @see <a href="https://docs.wpilib.org/en/stable/docs/software/commandbased/binding-commands-to-triggers.html#trigger-bindings">Binding Commands to Triggers</a>
     * @return the button as a Trigger
     */
    public Trigger getXButtonTrigger() {
        return new Trigger(this::getXButton);
    }

    /**
     * Returns the Y button press as a Trigger to be used in Command-based programming.
     * @see <a href="https://docs.wpilib.org/en/stable/docs/software/commandbased/binding-commands-to-triggers.html#trigger-bindings">Binding Commands to Triggers</a>
     * @return the button as a Trigger
     */
    public Trigger getYButtonTrigger() {
        return new Trigger(this::getYButton);
    }
}
