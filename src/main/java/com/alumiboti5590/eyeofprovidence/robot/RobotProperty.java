package com.alumiboti5590.eyeofprovidence.robot;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.Properties;

import com.alumiboti5590.eyeofprovidence.network.Elastic;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * <p><b>RobotProperty</b> provides a unified, statically accessed configuration system 
 * for FRC robots. It maps keys from a deployed properties file to basic Java types 
 * (String, Integer, Double, Boolean) dynamically at runtime.</p>
 * * <p>Rather than hardcoding constants for multiple physical robots (e.g., competition bot 
 * vs. practice bot), this utility reads the robot's identity from the file {@code /etc/robot} 
 * on the RoboRIO file system, then matches it to a corresponding file in the project's 
 * deploy directory (e.g., {@code deploy/competition.properties}).</p>
 * * <h3>Usage Examples:</h3>
 * <pre>{@code
 * // 1. Fetching a REQUIRED property (Throws an exception if missing or unparseable)
 * int motorId = RobotProperty.get("arm_motor_id", int.class);
 * double pGain = RobotProperty.get(DriveConstants.PID_P, double.class);
 * * // 2. Fetching an OPTIONAL property (Returns the default value on failure; type inferred automatically)
 * boolean isInverted = RobotProperty.get("is_inverted", false);
 * String robotOwner  = RobotProperty.get("owner", "Alumiboti 5590");
 * }</pre>
 */
public class RobotProperty {

    /** The path on the RoboRIO filesystem containing the unique text identifier for this specific robot. */
    private static final String kRobotNameFile = "/etc/robot";

    /** * Eagerly initialized thread-safe singleton instance. 
     * This guarantees properties are loaded sequentially exactly once when the class is first accessed.
     */
    private static final RobotProperty INSTANCE = new RobotProperty();

    /** The backing Java utility that stores the raw key-value string mappings loaded from disk. */
    private final Properties properties = new Properties();

    /** Cached absolute path to the active `.properties` file for telemetry and debugging purposes. */
    private final String propertyPath;

    /**
     * Private constructor to prevent direct instantiation from external classes.
     * Triggers the full identification and loading routine on startup.
     */
    private RobotProperty() {
        this.propertyPath = determinePropertyFilePath();
        this.loadDeployFileContents();
    }

    // ~~~~~~~~~
    // PUBLIC GENERIC API
    // ~~~~~~~~~

    /**
     * Retrieves a mandatory property from the configuration file and parses it into the requested type.
     * * @param <T>     The expected type to be returned.
     * @param keyObj  The lookup key. Can be a {@link String} or any Object with a meaningful {@code toString()} (e.g., Enums).
     * @param type    The class token representing the desired output type (e.g., {@code int.class}, {@code Boolean.class}).
     * @return The strongly-typed value extracted from the properties file.
     * @throws InvalidParameterException If the key is missing or the string value cannot be parsed into the target type.
     * @throws IllegalArgumentException  If an unsupported class type token is requested.
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Object keyObj, Class<T> type) {
        String key = keyObj.toString();
        String val = INSTANCE.properties.getProperty(key);

        // Fail early if the property was never defined
        if (val == null) {
            String msg = "Could not load expected property: " + key;
            // Push an alert out to the Elastic dashboard notify system
            Elastic.sendNotification(new Elastic.Notification(Elastic.NotificationLevel.ERROR, "Unknown Property", msg, 5000));
            throw new InvalidParameterException(msg);
        }

        try {
            val = val.trim(); // Strip accidental leading/trailing spaces from file edit typos

            // Route string parsing based on the class token provided
            if (type == String.class) return (T) val;
            if (type == Integer.class || type == int.class) return (T) Integer.valueOf(val);
            if (type == Double.class || type == double.class) return (T) Double.valueOf(val);
            if (type == Boolean.class || type == boolean.class) return (T) Boolean.valueOf(val);
            
        } catch (NumberFormatException e) {
            // Intercept parsing errors so structural bad data issues throw meaningful configuration errors
            throw new InvalidParameterException("Property '" + key + "' with value '" + val + "' cannot be parsed as " + type.getSimpleName());
        }

        // Safeguard against unsupported data types passed into the method
        throw new IllegalArgumentException("Unsupported property type conversion target: " + type.getName());
    }

    /**
     * Retrieves an optional property from the configuration file. If the property does not exist 
     * or fails to be parsed, this method falls back gracefully to the provided default value.
     * * <p>The return type is implicitly inferred by Java based on the type of the default value passed.</p>
     * * @param <T>          The inferred type returned by the method.
     * @param keyObj       The lookup key. Can be a {@link String} or any Object with a meaningful {@code toString()}.
     * @param defaultValue The backup data returned if the configuration key is missing or corrupted.
     * @return The typed value from the properties file if valid; otherwise, the {@code defaultValue}.
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Object keyObj, T defaultValue) {
        String key = keyObj.toString();
        String val = INSTANCE.properties.getProperty(key);

        // Fallback immediately if the key is entirely missing
        if (val == null) {
            return defaultValue;
        }

        try {
            val = val.trim(); // Cleanup string whitespace

            // Evaluate type mappings relative to the provided defaultValue instance
            if (defaultValue instanceof String) return (T) val;
            if (defaultValue instanceof Integer) return (T) Integer.valueOf(val);
            if (defaultValue instanceof Double) return (T) Double.valueOf(val);
            if (defaultValue instanceof Boolean) return (T) Boolean.valueOf(val);
            
        } catch (NumberFormatException e) {
            // Warn the drive team via standard DriverStation logs, but do not crash the code execution paths
            DriverStation.reportWarning("Failed parsing property '" + key + "' with value '" + val + "'. Falling back to default.", false);
        }

        return defaultValue;
    }

    /**
     * Retrieves the absolute system path of the configuration file currently in use.
     * Useful for diagnostics, telemetry layouts, or troubleshooting file mismatches.
     * * @return The full path String pointing to the active active properties file.
     */
    public static String getCurrentPropertyPath() {
        return INSTANCE.propertyPath;
    }

    // ~~~~~~~~~
    // INTERNALS
    // ~~~~~~~~~

    /**
     * Reads the host RoboRIO file system to locate the explicit target robot identity string.
     * * @return The cleaned robot name read from files (e.g., "practice"), or "competition" as a default backup.
     */
    private static String getRobotNameFromFilesystem() {
        try {
            // Attempt to pull the system name file managed via target RIO configurations
            String path = Files.readString(Paths.get(kRobotNameFile), StandardCharsets.UTF_8).trim();
            SmartDashboard.putBoolean("Found Robot Name", true);
            return path;
        } catch (IOException ex) {
            // File wasn't configured or we're running in a simulation environment. Fall back to standard baseline.
            SmartDashboard.putBoolean("Found Robot Name", false);
            return "competition";
        }
    }

    /**
     * Resolves the target absolute file system location where the properties file should live inside 
     * the compiled WPILib deployment bundle.
     * * @return A string representations of the properties target path.
     */
    private static String determinePropertyFilePath() {
        String robotName = getRobotNameFromFilesystem();
        String deployDirPath = Filesystem.getDeployDirectory().getAbsolutePath();
        
        // Combine the deploy directory path with the resolved robot name (e.g., /home/lvuser/deploy/practice.properties)
        Path propertiesFilePath = Paths.get(deployDirPath, robotName + ".properties");
        
        SmartDashboard.putString("Robot Name", robotName);
        return propertiesFilePath.toString();
    }

    /**
     * Opens the calculated properties file, reads the key-value attributes into memory, 
     * and ensures file resources are properly handled and released.
     */
    private void loadDeployFileContents() {
        // Utilizing a Try-With-Resources statement to ensure the FileInputStream closes automatically
        try (InputStream input = new FileInputStream(this.propertyPath)) {
            this.properties.clear(); // Wipe out any preexisting cache trace lines if re-called
            this.properties.load(input);
        } catch (IOException ex) {
            // Log full stack-trace to standard error outputs if the file completely fails to load
            ex.printStackTrace();
        }
    }
}