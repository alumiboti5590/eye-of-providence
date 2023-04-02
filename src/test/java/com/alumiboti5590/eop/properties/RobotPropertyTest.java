/* 2023 Written by Alumiboti FRC 5590 */
package com.alumiboti5590.eop.properties;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.text.MessageFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RobotPropertyTest {

    private Path workingDirectory;

    @BeforeEach
    public void init() {
        this.workingDirectory = Path.of("", "src/test/resources");
        Path file = this.workingDirectory.resolve("robot.properties");
        RobotProperty.UNSAFE_setSingleton(file.toAbsolutePath().toString());
    }

    // ~~~~~~~~~~~~
    // String Tests
    // ~~~~~~~~~~~~

    @Test
    @DisplayName("getAsString works with existing key")
    public void getAsStringExistingKey() {
        String expected = "Hello World!";
        String actual = RobotProperty.getAsString("a_string");

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    @Test
    @DisplayName("getAsString raises an exception on a missing key")
    public void getAsStringMissingKey() {
        assertThrows(InvalidParameterException.class, () -> RobotProperty.getAsString("another_string"));
    }

    @Test
    @DisplayName("getAsString works returns real value if default is given")
    public void getAsStringReturnsRealIfDefaultGiven() {
        String expected = "Hello World!";
        String actual = RobotProperty.getAsString("a_string", "Goodbye world!");

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    @Test
    @DisplayName("getAsString works returns default value if missing property")
    public void getAsStringReturnsDefaultIfMissing() {
        String expected = "Hello World!";
        String actual = RobotProperty.getAsString("another_string", expected);

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    // ~~~~~~~~~~~~~
    // Integer Tests
    // ~~~~~~~~~~~~~

    @Test
    @DisplayName("getAsInt works with existing key")
    public void getAsIntExistingKey() {
        int expected = 0;
        int actual = RobotProperty.getAsInt("a_zero_integer");

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    @Test
    @DisplayName("getAsInt works with a negative existing key")
    public void getAsIntNegativeExistingKey() {
        int expected = -12;
        int actual = RobotProperty.getAsInt("a_negative_integer");

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    @Test
    @DisplayName("getAsInt raises an exception on a missing key")
    public void getAsIntMissingKey() {
        assertThrows(InvalidParameterException.class, () -> RobotProperty.getAsInt("another_string"));
    }

    @Test
    @DisplayName("getAsInt works returns real value if default is given")
    public void getAsIntReturnsRealIfDefaultGiven() {
        int expected = 15;
        int actual = RobotProperty.getAsInt("a_positive_integer", 10);

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    @Test
    @DisplayName("getAsInt works returns default value if missing property")
    public void getAsIntReturnsDefaultIfMissing() {
        int expected = 6;
        int actual = RobotProperty.getAsInt("another_integer", expected);

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    // ~~~~~~~~~~~~~
    // Double Tests
    // ~~~~~~~~~~~~~

    @Test
    @DisplayName("getAsDouble works with existing key")
    public void getAsDoubleExistingKey() {
        double expected = 0;
        double actual = RobotProperty.getAsDouble("a_zero_double");

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    @Test
    @DisplayName("getAsDouble works with a negative existing key")
    public void getAsDoubleNegativeExistingKey() {
        double expected = -12.2;
        double actual = RobotProperty.getAsDouble("a_negative_double");

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    @Test
    @DisplayName("getAsDouble raises an exception on a missing key")
    public void getAsDoubleMissingKey() {
        assertThrows(InvalidParameterException.class, () -> RobotProperty.getAsDouble("another_string"));
    }

    @Test
    @DisplayName("getAsDouble works returns real value if default is given")
    public void getAsDoubleReturnsRealIfDefaultGiven() {
        double expected = 15.456;
        double actual = RobotProperty.getAsDouble("a_positive_double", 10);

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    @Test
    @DisplayName("getAsDouble works returns default value if missing property")
    public void getAsDoubleReturnsDefaultIfMissing() {
        double expected = 6;
        double actual = RobotProperty.getAsDouble("another_double", expected);

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    // ~~~~~~~~~~~~~
    // Boolean Tests
    // ~~~~~~~~~~~~~

    @Test
    @DisplayName("getAsBoolean works with existing key")
    public void getAsBooleanTrueExistingKey() {
        boolean expected = true;
        boolean actual = RobotProperty.getAsBoolean("a_true_boolean");

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    @Test
    @DisplayName("getAsBoolean works with a negative existing key")
    public void getAsBooleanFalseExistingKey() {
        boolean expected = false;
        boolean actual = RobotProperty.getAsBoolean("a_false_boolean");

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    @Test
    @DisplayName("getAsBoolean raises an exception on a missing key")
    public void getAsBooleanMissingKey() {
        assertThrows(InvalidParameterException.class, () -> RobotProperty.getAsBoolean("another_string"));
    }

    @Test
    @DisplayName("getAsBoolean works returns real value if default is given")
    public void getAsBooleanReturnsRealIfDefaultGiven() {
        boolean expected = true;
        boolean actual = RobotProperty.getAsBoolean("a_true_boolean", false);

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    @Test
    @DisplayName("getAsBoolean works returns default value if missing property")
    public void getAsBooleanReturnsDefaultIfMissing() {
        boolean expected = false;
        boolean actual = RobotProperty.getAsBoolean("another_boolean", expected);

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }
}
