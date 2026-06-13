package com.alumiboti5590.eyeofprovidence.robot.hardware.motors.notifications;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.alumiboti5590.eyeofprovidence.network.Elastic;
import com.ctre.phoenix6.hardware.core.CoreTalonFX;
import com.revrobotics.REVLibError;
import com.revrobotics.spark.SparkBase;

public class MotorConnectionCheckTest {

    // Holds the scoped static mock for the Elastic class across each individual test execution
    private MockedStatic<Elastic> mockedElastic;

    @BeforeEach
    public void setUp() {
        // Redirects all static calls to Elastic.class into a Mockito monitoring bucket
        mockedElastic = mockStatic(Elastic.class);
    }

    @AfterEach
    public void tearDown() {
        // CRITICAL: Closes the static mock block to prevent memory leaks and test cross-contamination
        mockedElastic.close();
    }

    @Test
    public void testSingleSparkBaseDisconnectionTriggersNotification() {
        // Arrange
        SparkBase mockSpark = mock(SparkBase.class);
        when(mockSpark.getDeviceId()).thenReturn(1);
        when(mockSpark.getLastError()).thenReturn(REVLibError.kCANDisconnected); // Force error state

        // Act
        MotorConnectionCheck.checkMotors("Drivetrain", mockSpark);

        // Assert
        // Verifies Elastic.sendNotification() was called exactly 1 time with any Notification object
        mockedElastic.verify(
            () -> Elastic.sendNotification(any(Elastic.Notification.class)), 
            times(1)
        );
    }

    @Test
    public void testMultipleSparkBaseDisconnectionsTriggerNotifications() {
        // Arrange
        SparkBase mockSpark1 = mock(SparkBase.class);
        when(mockSpark1.getDeviceId()).thenReturn(1);
        when(mockSpark1.getLastError()).thenReturn(REVLibError.kCANDisconnected);

        SparkBase mockSpark2 = mock(SparkBase.class);
        when(mockSpark2.getDeviceId()).thenReturn(2);
        when(mockSpark2.getLastError()).thenReturn(REVLibError.kCantFindFirmware); // Another variant of an error state

        // Act
        MotorConnectionCheck.checkMotors("Climber", mockSpark1, mockSpark2);

        // Assert
        // Both motors are broken, so we expect exactly 2 distinct dashboard notifications
        mockedElastic.verify(
            () -> Elastic.sendNotification(any(Elastic.Notification.class)), 
            times(2)
        );
    }

    @Test
    public void testSingleTalonFXDisconnectionTriggersNotification() {
        // Arrange
        CoreTalonFX mockTalon = mock(CoreTalonFX.class);
        when(mockTalon.getDeviceID()).thenReturn(10);
        when(mockTalon.isConnected()).thenReturn(false); // Force CTRE disconnected state

        // Act
        MotorConnectionCheck.checkMotors("Intake", mockTalon);

        // Assert
        mockedElastic.verify(
            () -> Elastic.sendNotification(any(Elastic.Notification.class)), 
            times(1)
        );
    }

    @Test
    public void testMultipleTalonFXDisconnectionsTriggerNotifications() {
        // Arrange
        CoreTalonFX mockTalon1 = mock(CoreTalonFX.class);
        when(mockTalon1.getDeviceID()).thenReturn(11);
        when(mockTalon1.isConnected()).thenReturn(false);

        CoreTalonFX mockTalon2 = mock(CoreTalonFX.class);
        when(mockTalon2.getDeviceID()).thenReturn(12);
        when(mockTalon2.isConnected()).thenReturn(false);

        // Act
        MotorConnectionCheck.checkMotors("Shooter", mockTalon1, mockTalon2);

        // Assert
        mockedElastic.verify(
            () -> Elastic.sendNotification(any(Elastic.Notification.class)), 
            times(2)
        );
    }

    @Test
    public void testSingleSparkBaseConnectedDoesNotTriggerNotification() {
        // Arrange
        SparkBase mockSpark = mock(SparkBase.class);
        // kOk means the motor is perfectly healthy and reachable
        when(mockSpark.getLastError()).thenReturn(REVLibError.kOk); 

        // Act
        MotorConnectionCheck.checkMotors("Drivetrain", mockSpark);

        // Assert
        // Verifies that Elastic.sendNotification was NEVER invoked with any notification
        mockedElastic.verify(
            () -> Elastic.sendNotification(any(Elastic.Notification.class)), 
            never()
        );
    }

    @Test
    public void testSingleTalonFXConnectedDoesNotTriggerNotification() {
        // Arrange
        CoreTalonFX mockTalon = mock(CoreTalonFX.class);
        // true means the Falcon/Talon FX is actively communicating on the CAN bus
        when(mockTalon.isConnected()).thenReturn(true); 

        // Act
        MotorConnectionCheck.checkMotors("Intake", mockTalon);

        // Assert
        // Verifies that the dashboard remains clear of error notifications
        mockedElastic.verify(
            () -> Elastic.sendNotification(any(Elastic.Notification.class)), 
            never()
        );
    }
}