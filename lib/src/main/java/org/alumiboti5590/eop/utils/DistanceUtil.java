/* 2023 Written by Alumiboti FRC 5590 */
package org.alumiboti5590.eop.utils;

/**
 * DistanceUtil packages a lot of useful distance conversion methods
 */
public class DistanceUtil {

    public static final double INCH_TO_CM_RATIO = 2.54;

    /**
     * Converts a measurement from inches to centimeters
     * @param inches the distance in inches
     * @return the distance in centimeters
     */
    public static double inchesToCentimeters(double inches) {
        return inches * INCH_TO_CM_RATIO;
    }

    /**
     * Converts a measurement from centimeters to inches
     * @param centimeters the distance in centimeters
     * @return the distance in inches
     */
    public static double centimetersToInches(double centimeters) {
        return centimeters / INCH_TO_CM_RATIO;
    }

    /**
     * Converts a measurement from centimeters to meters
     * @param centimeters the distance in centimeters
     * @return the distance in meters
     */
    public static double centimetersToMeters(double centimeters) {
        return centimeters / 100;
    }

    /**
     * Converts a measurement from meters to centimeters
     * @param meters the distance in meters
     * @return the distance in centimeters
     */
    public static double metersToCentimeters(double meters) {
        return meters * 100;
    }
}
