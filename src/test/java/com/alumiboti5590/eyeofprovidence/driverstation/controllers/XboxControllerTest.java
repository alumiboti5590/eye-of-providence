package com.alumiboti5590.eyeofprovidence.driverstation.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.simulation.DriverStationSim;
import edu.wpi.first.wpilibj.simulation.XboxControllerSim;

public class XboxControllerTest {

    private static final int CONTROLLER_PORT = 1;
    private static final double EPSILON = 1e-6; // Tolerance for floating-point comparisons

    private XboxController m_controller;
    private XboxControllerSim m_controllerSim;

    @BeforeAll
    public static void initHardwareLayer() {
        // Initializes the simulated WPILib Hardware Abstraction Layer
        assert HAL.initialize(500, 0);
    }

    @BeforeEach
    public void setUp() {
        DriverStationSim.resetData();
        
        // Testing the default constructor which applies the new 2% (0.02) deadband
        m_controller = new XboxController(CONTROLLER_PORT);
        m_controllerSim = new XboxControllerSim(CONTROLLER_PORT);
    }

    @Test
    public void testLeftJoystickDeadband() {
        // --- TEST LEFT X AXIS ---
        // 1. Value inside the 2% deadband (Expect absolute 0.0)
        m_controllerSim.setLeftX(0.01);
        DriverStationSim.notifyNewData();
        assertEquals(0.0, m_controller.getLeftX(), EPSILON);

        // 2. Value inside the negative 2% deadband (Expect absolute 0.0)
        m_controllerSim.setLeftX(-0.01);
        DriverStationSim.notifyNewData();
        assertEquals(0.0, m_controller.getLeftX(), EPSILON);

        // 3. Value outside deadband: 0.51 rescales linearly to exactly 0.50
        m_controllerSim.setLeftX(0.51);
        DriverStationSim.notifyNewData();
        assertEquals(0.50, m_controller.getLeftX(), EPSILON);

        // --- TEST LEFT Y AXIS ---
        m_controllerSim.setLeftY(0.01);
        DriverStationSim.notifyNewData();
        assertEquals(0.0, m_controller.getLeftY(), EPSILON);

        // Negative value outside deadband: -0.51 rescales to exactly -0.50
        m_controllerSim.setLeftY(-0.51);
        DriverStationSim.notifyNewData();
        assertEquals(-0.50, m_controller.getLeftY(), EPSILON);
    }

    @Test
    public void testRightJoystickDeadband() {
        // --- TEST RIGHT X AXIS ---
        m_controllerSim.setRightX(-0.01);
        DriverStationSim.notifyNewData();
        assertEquals(0.0, m_controller.getRightX(), EPSILON);

        m_controllerSim.setRightX(0.51);
        DriverStationSim.notifyNewData();
        assertEquals(0.50, m_controller.getRightX(), EPSILON);

        // --- TEST RIGHT Y AXIS ---
        m_controllerSim.setRightY(0.01);
        DriverStationSim.notifyNewData();
        assertEquals(0.0, m_controller.getRightY(), EPSILON);

        m_controllerSim.setRightY(0.51);
        DriverStationSim.notifyNewData();
        assertEquals(0.50, m_controller.getRightY(), EPSILON);
    }

    @Test
    public void testFullAxisThrow() {
        // Verify that pushing the stick to maximum deflection (1.0) still yields full power (1.0)
        m_controllerSim.setLeftX(1.0);
        DriverStationSim.notifyNewData();
        assertEquals(1.0, m_controller.getLeftX(), EPSILON);
        
        m_controllerSim.setLeftX(-1.0);
        DriverStationSim.notifyNewData();
        assertEquals(-1.0, m_controller.getLeftX(), EPSILON);
    }
}