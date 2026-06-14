package com.alumiboti5590.eyeofprovidence.robot;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.Properties;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import com.alumiboti5590.eyeofprovidence.network.Elastic;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Unit tests for the {@link RobotProperty} configuration utility.
 * Utilizes Mockito static mocking to simulate the WPILib/RoboRIO environment.
 */
public class RobotPropertyTest {

    // Retain static mock references to close them after all tests finish
    private static MockedStatic<Filesystem> mockedFilesystem;
    private static MockedStatic<SmartDashboard> mockedSmartDashboard;
    private static MockedStatic<Elastic> mockedElastic;
    private static MockedStatic<DriverStation> mockedDriverStation;

    // The underlying properties table inside the singleton we will manipulate
    private Properties internalProperties;

    /**
     * An example enumeration to test object-based lookup keys.
     */
    private enum TestKeys {
        DEVICE_ID, IS_ENABLED
    }

    @BeforeAll
    static void classSetup() throws Exception {
        // 1. Initialize static mocks BEFORE RobotProperty is ever referenced or loaded by the JVM
        mockedFilesystem = mockStatic(Filesystem.class);
        mockedSmartDashboard = mockStatic(SmartDashboard.class);
        mockedElastic = mockStatic(Elastic.class);
        mockedDriverStation = mockStatic(DriverStation.class);

        // 2. Mock WPILib filesystem calls to safely bypass native RoboRIO directory lookups
        File mockDeployDir = new File(".");
        mockedFilesystem.when(Filesystem::getDeployDirectory).thenReturn(mockDeployDir);

        // 3. Force RobotProperty class loading while statics are actively mocked
        Field instanceField = RobotProperty.class.getDeclaredField("INSTANCE");
        instanceField.setAccessible(true);
        instanceField.get(null);
    }

    @AfterAll
    static void classTeardown() {
        // Deregister static mocks to prevent memory leaks and thread pollution
        mockedFilesystem.close();
        mockedSmartDashboard.close();
        mockedElastic.close();
        mockedDriverStation.close();
    }

    @BeforeEach
    void testSetup() throws Exception {
        mockedSmartDashboard.clearInvocations();

        // Use reflection to extract the live private properties table from the singleton instance
        Field instanceField = RobotProperty.class.getDeclaredField("INSTANCE");
        instanceField.setAccessible(true);
        Object singletonInstance = instanceField.get(null);

        Field propertiesField = RobotProperty.class.getDeclaredField("properties");
        propertiesField.setAccessible(true);

        internalProperties = (Properties) propertiesField.get(singletonInstance);

        // Wipe the data clean before running every individual test block
        internalProperties.clear();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // STRING TYPE TESTS
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testGetString_Required_Success() {
        internalProperties.put("string.key", "Alumiboti");
        String result = RobotProperty.get("string.key", String.class);
        assertEquals("Alumiboti", result);
    }

    @Test
    void testGetString_Required_MissingThrowsException() {
        assertThrows(InvalidParameterException.class, () -> {
            RobotProperty.get("missing.key", String.class);
        });
    }

    @Test
    void testGetString_Optional_Present() {
        internalProperties.put("string.key", "CustomValue");
        String result = RobotProperty.get("string.key", "BackupValue");
        assertEquals("CustomValue", result);
    }

    @Test
    void testGetString_Optional_MissingReturnsDefault() {
        String result = RobotProperty.get("missing.key", "BackupValue");
        assertEquals("BackupValue", result);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // INTEGER TYPE TESTS
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testGetInt_Required_Success() {
        internalProperties.put("int.key", " 5590 "); // Includes whitespace to verify trim
        int result = RobotProperty.get("int.key", int.class);
        assertEquals(5590, result);
    }

    @Test
    void testGetInt_Required_MalformedThrowsException() {
        internalProperties.put("int.key", "five-five-nine-zero");
        assertThrows(InvalidParameterException.class, () -> {
            RobotProperty.get("int.key", int.class);
        });
    }

    @Test
    void testGetInt_Optional_Present() {
        internalProperties.put("int.key", "42");
        int result = RobotProperty.get("int.key", 100);
        assertEquals(42, result);
    }

    @Test
    void testGetInt_Optional_MissingReturnsDefault() {
        int result = RobotProperty.get("missing.key", 100);
        assertEquals(100, result);
    }

    @Test
    void testGetInt_Optional_MalformedReturnsDefaultAndWarns() {
        internalProperties.put("int.key", "bad_number");
        int result = RobotProperty.get("int.key", 100);

        assertEquals(100, result);
        // Verify a driver station alert was emitted due to bad layout data
        mockedDriverStation.verify(() -> DriverStation.reportWarning(anyString(), anyBoolean()), times(1));
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // DOUBLE TYPE TESTS
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testGetDouble_Required_Success() {
        internalProperties.put("double.key", "0.05");
        double result = RobotProperty.get("double.key", double.class);
        assertEquals(0.05, result, 0.0001);
    }

    @Test
    void testGetDouble_Required_MalformedThrowsException() {
        internalProperties.put("double.key", "0.05_with_text");
        assertThrows(InvalidParameterException.class, () -> {
            RobotProperty.get("double.key", double.class);
        });
    }

    @Test
    void testGetDouble_Optional_Present() {
        internalProperties.put("double.key", "3.1415");
        double result = RobotProperty.get("double.key", 1.0);
        assertEquals(3.1415, result, 0.0001);
    }

    @Test
    void testGetDouble_Optional_MissingReturnsDefault() {
        double result = RobotProperty.get("missing.key", 1.0);
        assertEquals(1.0, result, 0.0001);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // BOOLEAN TYPE TESTS
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testGetBoolean_Required_Success() {
        internalProperties.put("bool.key", "true");
        boolean result = RobotProperty.get("bool.key", boolean.class);
        assertTrue(result);
    }

    @Test
    void testGetBoolean_Optional_Present() {
        internalProperties.put("bool.key", "false");
        boolean result = RobotProperty.get("bool.key", true);
        assertFalse(result);
    }

    @Test
    void testGetBoolean_Optional_MissingReturnsDefault() {
        boolean result = RobotProperty.get("missing.key", true);
        assertTrue(result);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // EDGE CASES & DESIGN VERIFICATIONS
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testObjectKeyResolution_UsingEnum() {
        internalProperties.put("DEVICE_ID", "11");
        internalProperties.put("IS_ENABLED", "true");

        int id = RobotProperty.get(TestKeys.DEVICE_ID, int.class);
        boolean enabled = RobotProperty.get(TestKeys.IS_ENABLED, false);

        assertEquals(11, id);
        assertTrue(enabled);
    }

    @Test
    void testUnsupportedClassTypeTokenThrowsException() {
        internalProperties.put("unsupported.key", "12345");
        assertThrows(IllegalArgumentException.class, () -> {
            // Long.class is explicitly not defined inside the supported parser paths
            RobotProperty.get("unsupported.key", Long.class);
        });
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // INTERNAL / PRIVATE METHOD TESTS
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    void testGetRobotNameFromFilesystem_FileExists() throws Exception {
        // Intercept Java's native Files utility specifically for this test block
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.readString(eq(Paths.get("/etc/robot")), eq(StandardCharsets.UTF_8)))
                    .thenReturn("  practice_bot \n"); // Include weird spacing to test .trim()

            // Access the private static method via Reflection
            Method method = RobotProperty.class.getDeclaredMethod("getRobotNameFromFilesystem");
            method.setAccessible(true);
            String robotName = (String) method.invoke(null);

            assertEquals("practice_bot", robotName);
            mockedSmartDashboard.verify(() -> SmartDashboard.putBoolean("Found Robot Name", true), times(1));
        }
    }

    @Test
    void testGetRobotNameFromFilesystem_FileNotFound_FallsBack() throws Exception {
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            // Force an IOException to simulate a missing or corrupt file on the RoboRIO
            mockedFiles.when(() -> Files.readString(any(Path.class), any()))
                    .thenThrow(new IOException("Simulated file missing"));

            Method method = RobotProperty.class.getDeclaredMethod("getRobotNameFromFilesystem");
            method.setAccessible(true);
            String robotName = (String) method.invoke(null);

            // Verify safe recovery behavior
            assertEquals("competition", robotName);
            mockedSmartDashboard.verify(() -> SmartDashboard.putBoolean("Found Robot Name", false), times(1));
        }
    }

    @Test
    void testDeterminePropertyFilePath() throws Exception {
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.readString(any(Path.class), any()))
                    .thenReturn("beta_trike");

            Method method = RobotProperty.class.getDeclaredMethod("determinePropertyFilePath");
            method.setAccessible(true);
            String calculatedPath = (String) method.invoke(null);

            // Assert path structure ends with the proper file name regardless of the host OS root directory
            assertTrue(calculatedPath.endsWith("beta_trike.properties"));
            mockedSmartDashboard.verify(() -> SmartDashboard.putString("Robot Name", "beta_trike"), times(1));
        }
    }

    @Test
    void testLoadDeployFileContents_LoadsValidData(@TempDir Path tempDir) throws Exception {
        // 1. Write an actual, isolated dummy properties file to an ephemeral test directory
        Path tempPropertiesFile = tempDir.resolve("test-robot.properties");
        String fileContent = "can.id.int=22\npid.p.double=0.45\nfeature.bool=true";
        Files.writeString(tempPropertiesFile, fileContent);

        // 2. Fetch the live singleton instance via reflection
        Field instanceField = RobotProperty.class.getDeclaredField("INSTANCE");
        instanceField.setAccessible(true);
        Object singletonInstance = instanceField.get(null);

        // 3. Force-inject our temp file path into the private field `propertyPath`
        Field pathField = RobotProperty.class.getDeclaredField("propertyPath");
        pathField.setAccessible(true);
        pathField.set(singletonInstance, tempPropertiesFile.toAbsolutePath().toString());

        // 4. Manually trigger the private file reader method via reflection
        Method loadMethod = RobotProperty.class.getDeclaredMethod("loadDeployFileContents");
        loadMethod.setAccessible(true);
        loadMethod.invoke(singletonInstance);

        // 5. Assert that the internal table parsed the real file content perfectly
        assertEquals(22, RobotProperty.get("can.id.int", int.class));
        assertEquals(0.45, RobotProperty.get("pid.p.double", double.class), 0.001);
        assertTrue(RobotProperty.get("feature.bool", boolean.class));
    }
}