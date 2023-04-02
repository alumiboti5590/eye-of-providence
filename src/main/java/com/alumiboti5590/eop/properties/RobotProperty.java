/* 2023 Written by Alumiboti FRC 5590 */
package com.alumiboti5590.eop.properties;

import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * RobotProperty is an extension of normal Java Properties, but can be accessed in
 * a static manner. The properties file loaded is found by ammending the Robot name
 * found under {@code /etc/robot} on the RoboRio to the {@code Filesystem.getDeployDirectory()}
 * provided by WPILib.
 *
 * <p>Access to properties should be done via the {@code getAs...} static methods, such as
 * {@code getAsString()}, {@code getAsInt()}, {@code getAsDouble()}, and {@code getAsBoolean()}.
 * Each also accepts a second parameter representing the default value if the property
 * key is not defined.
 * @see <a href="https://docs.wpilib.org/en/stable/docs/software/advanced-gradlerio/deploy-git-data.html#using-deployed-files">WPILib Deployed Files</a>
 */
public class RobotProperty extends Properties {

    // ~~~~~~~~~
    // ACCESSORS
    // ~~~~~~~~~

    /**
     * Returns the property as a string. This is the same as {@code `getProperty()`}, but matches
     * the type-generation of the other static methods.
     *
     * An {@code InvalidParameterException} will be raised if a property is loaded that does not exist.
     * @param key The property key to fetch.
     * @return The String value of the property.
     */
    public static String getAsString(String key) {
        return getSingleton().getProperty(key);
    }

    /**
     * Same as {@link getAsString(String)} but takes a default value if the property is not defined,
     * instead of raising an exception.
     * @param key The property key to fetch.
     * @param defaultValue The default value if the property does not exist.
     * @return The String value of the property.
     */
    public static String getAsString(String key, String defaultValue) {
        try {
            return getSingleton().getProperty(key);
        } catch (InvalidParameterException e) {
            return defaultValue;
        }
    }

    /**
     * Returns the property as an integer.
     *
     * An {@code InvalidParameterException} will be raised if a property is loaded that does not exist.
     * @param key The property key to fetch.
     * @return The integer value of the property.
     */
    public static int getAsInt(String key) {
        return Integer.parseInt(getAsString(key));
    }

    /**
     * Same as {@link getAsInt(String)} but takes a default value if the property is not defined,
     * instead of raising an exception.
     * @param key The property key to fetch.
     * @param defaultValue The default value if the property does not exist.
     * @return The integer value of the property.
     */
    public static int getAsInt(String key, int defaultValue) {
        try {
            return getAsInt(key);
        } catch (InvalidParameterException e) {
            return defaultValue;
        }
    }

    /**
     * Returns the property as a boolean.
     *
     * An {@code InvalidParameterException} will be raised if a property is loaded that does not exist.
     * @param key The property key to fetch.
     * @return The boolean value of the property.
     */
    public static boolean getAsBoolean(String key) {
        return Boolean.parseBoolean(getAsString(key));
    }

    /**
     * Same as {@link getAsBoolean(String)} but takes a default value if the property is not defined,
     * instead of raising an exception.
     * @param key The property key to fetch.
     * @param defaultValue The default value if the property does not exist.
     * @return The boolean value of the property.
     */
    public static boolean getAsBoolean(String key, boolean defaultValue) {
        try {
            return getAsBoolean(key);
        } catch (InvalidParameterException e) {
            return defaultValue;
        }
    }

    /**
     * Returns the property as a double.
     *
     * An {@code InvalidParameterException} will be raised if a property is loaded that does not exist.
     * @param key The property key to fetch.
     * @return The double value of the property.
     */
    public static double getAsDouble(String key) {
        return Double.parseDouble(getAsString(key));
    }

    /**
     * Same as {@link getAsDouble(String)} but takes a default value if the property is not defined,
     * instead of raising an exception.
     * @param key The property key to fetch.
     * @param defaultValue The default value if the property does not exist.
     * @return The double value of the property.
     */
    public static double getAsDouble(String key, double defaultValue) {
        try {
            return getAsDouble(key);
        } catch (InvalidParameterException e) {
            return defaultValue;
        }
    }

    /**
     * Returns the currently loaded property file path for debugging purposes
     * @return the full path to the current properties file
     */
    public String getCurrentPropertyPath() {
        return this.propertyPath;
    }

    // ~~~~~~~~~
    // INTERNALS
    // ~~~~~~~~~

    private static final String ROBOT_NAME_FILE = "/etc/robot";

    private static RobotProperty singleton;
    private String propertyPath;

    private RobotProperty() {
        this(determinePropertyFilePath());
    }

    private RobotProperty(String propertiesFilePath) {
        this.propertyPath = propertiesFilePath;
        this.loadDeployFileContents();
    }

    private static RobotProperty getSingleton() {
        if (singleton == null) {
            singleton = new RobotProperty();
        }
        return singleton;
    }

    // Fetches the robot name from the `ROBOT_NAME_FILE`
    private static String getRobotNameFromFilesystem() {
        try {
            return Files.readString(Paths.get(ROBOT_NAME_FILE), StandardCharsets.UTF_8)
                    .trim();
        } catch (IOException ex) {
            ex.printStackTrace();
            return "";
        }
    }

    private static String determinePropertyFilePath() {
        String robotName = getRobotNameFromFilesystem();
        String deployDirPath = Filesystem.getDeployDirectory().getAbsolutePath();
        Path propertiesFilePath = Paths.get(deployDirPath, robotName + ".properties");
        SmartDashboard.putString("Robot Name", robotName);
        return propertiesFilePath.toString();
    }

    /**
     * Load in the robot properties into the RobotProperties instance.
     */
    private void loadDeployFileContents() {
        try {
            InputStream input = new FileInputStream(this.propertyPath);
            this.clear(); // Ensure no previous keys exist
            this.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * An override of the Java {@code Properties.getProperty(String)} method, but use {@code getAsString(String)} instead.
     */
    @Override
    public String getProperty(String key) {
        String property = super.getProperty(key);
        if (property == null) {
            throw new InvalidParameterException(MessageFormat.format("Missing property value for key '{0}'", key));
        }
        return property;
    }

    /**
     * Used only for testing, so <strong>do not call this in code</strong>.
     * @param propertyPath The property path that determines the file properties are loaded from.
     */
    public static void UNSAFE_setSingleton(String propertyPath) {
        singleton = new RobotProperty(propertyPath);
    }
}
