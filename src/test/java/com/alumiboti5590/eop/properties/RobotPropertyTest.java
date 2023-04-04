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

    // Used to test fetching value from enumeration
    private enum Properties {
        a_string,
        a_positive_integer,
        a_positive_double,
        a_true_boolean;
    }

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
    @DisplayName("getString works with existing key")
    public void getStringExistingKey() {
        String expected = "Hello World!";
        String actual = RobotProperty.getString("a_string");

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    @Test
    @DisplayName("getString raises an exception on a missing key")
    public void getStringMissingKey() {
        assertThrows(InvalidParameterException.class, () -> RobotProperty.getString("another_string"));
    }

    @Test
    @DisplayName("getString works returns real value if default is given")
    public void getStringReturnsRealIfDefaultGiven() {
        String expected = "Hello World!";
        String actual = RobotProperty.getString("a_string", "Goodbye world!");

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    @Test
    @DisplayName("getString works returns default value if missing property")
    public void getStringReturnsDefaultIfMissing() {
        String expected = "Hello World!";
        String actual = RobotProperty.getString("another_string", expected);

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    @Test
    @DisplayName("getString works given an enum value")
    public void getStringExistingKeyViaEnumeration() {
        String expected = "Hello World!";
        String actual = RobotProperty.getString(Properties.a_string);

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    // ~~~~~~~~~~~~~
    // Integer Tests
    // ~~~~~~~~~~~~~

    @Test
    @DisplayName("getInt works with existing key")
    public void getIntExistingKey() {
        int expected = 0;
        int actual = RobotProperty.getInt("a_zero_integer");

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    @Test
    @DisplayName("getInt works with a negative existing key")
    public void getIntNegativeExistingKey() {
        int expected = -12;
        int actual = RobotProperty.getInt("a_negative_integer");

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    @Test
    @DisplayName("getInt raises an exception on a missing key")
    public void getIntMissingKey() {
        assertThrows(InvalidParameterException.class, () -> RobotProperty.getInt("another_string"));
    }

    @Test
    @DisplayName("getInt works returns real value if default is given")
    public void getIntReturnsRealIfDefaultGiven() {
        int expected = 15;
        int actual = RobotProperty.getInt("a_positive_integer", 10);

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    @Test
    @DisplayName("getInt works returns default value if missing property")
    public void getIntReturnsDefaultIfMissing() {
        int expected = 6;
        int actual = RobotProperty.getInt("another_integer", expected);

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    @Test
    @DisplayName("getInt works given an enum value")
    public void getIntExistingKeyViaEnumeration() {
        int expected = 15;
        int actual = RobotProperty.getInt(Properties.a_positive_integer);

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    // ~~~~~~~~~~~~~
    // Double Tests
    // ~~~~~~~~~~~~~

    @Test
    @DisplayName("getDouble works with existing key")
    public void getDoubleExistingKey() {
        double expected = 0;
        double actual = RobotProperty.getDouble("a_zero_double");

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    @Test
    @DisplayName("getDouble works with a negative existing key")
    public void getDoubleNegativeExistingKey() {
        double expected = -12.2;
        double actual = RobotProperty.getDouble("a_negative_double");

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    @Test
    @DisplayName("getDouble raises an exception on a missing key")
    public void getDoubleMissingKey() {
        assertThrows(InvalidParameterException.class, () -> RobotProperty.getDouble("another_string"));
    }

    @Test
    @DisplayName("getDouble works returns real value if default is given")
    public void getDoubleReturnsRealIfDefaultGiven() {
        double expected = 15.456;
        double actual = RobotProperty.getDouble("a_positive_double", 10);

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    @Test
    @DisplayName("getDouble works returns default value if missing property")
    public void getDoubleReturnsDefaultIfMissing() {
        double expected = 6;
        double actual = RobotProperty.getDouble("another_double", expected);

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    @Test
    @DisplayName("getDouble works given an enum value")
    public void getDoubleExistingKeyViaEnumeration() {
        double expected = 15.456;
        double actual = RobotProperty.getDouble(Properties.a_positive_double);

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    // ~~~~~~~~~~~~~
    // Boolean Tests
    // ~~~~~~~~~~~~~

    @Test
    @DisplayName("getBoolean works with existing key")
    public void getBooleanTrueExistingKey() {
        boolean expected = true;
        boolean actual = RobotProperty.getBoolean("a_true_boolean");

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    @Test
    @DisplayName("getBoolean works with a negative existing key")
    public void getBooleanFalseExistingKey() {
        boolean expected = false;
        boolean actual = RobotProperty.getBoolean("a_false_boolean");

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    @Test
    @DisplayName("getBoolean raises an exception on a missing key")
    public void getBooleanMissingKey() {
        assertThrows(InvalidParameterException.class, () -> RobotProperty.getBoolean("another_string"));
    }

    @Test
    @DisplayName("getBoolean works returns real value if default is given")
    public void getBooleanReturnsRealIfDefaultGiven() {
        boolean expected = true;
        boolean actual = RobotProperty.getBoolean("a_true_boolean", false);

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    @Test
    @DisplayName("getBoolean works returns default value if missing property")
    public void getBooleanReturnsDefaultIfMissing() {
        boolean expected = false;
        boolean actual = RobotProperty.getBoolean("another_boolean", expected);

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }

    @Test
    @DisplayName("getBoolean works given an enum value")
    public void getBooleanExistingKeyViaEnumeration() {
        boolean expected = true;
        boolean actual = RobotProperty.getBoolean(Properties.a_true_boolean);

        assertEquals(expected, actual, MessageFormat.format("Expected {0} to equal {1}", expected, actual));
    }
}
