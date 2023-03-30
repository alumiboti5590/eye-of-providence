/* 2023 Written by Alumiboti FRC 5590 */
package org.alumiboti5590.eop.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DistanceUtilTest {

    @Test
    @DisplayName("DistanceUtil Inches to Centimeters")
    void validInchesToCentimeters() {
        double inches = 4;
        double expected = 10.16;
        double actual = DistanceUtil.inchesToCentimeters(inches);

        assertEquals(expected, actual, .01);
    }

    @Test
    @DisplayName("DistanceUtil Centimeters to Inches")
    void validCentimetersToInches() {
        double centimeters = 10;
        double expected = 3.94;
        double actual = DistanceUtil.centimetersToInches(centimeters);

        assertEquals(expected, actual, .01);
    }

    @Test
    @DisplayName("DistanceUtil Centimeters to Meters")
    void validCentimetersToMeters() {
        double centimeters = 230;
        double expected = 2.30;
        double actual = DistanceUtil.centimetersToMeters(centimeters);

        assertEquals(expected, actual, .001);
    }

    @Test
    @DisplayName("DistanceUtil Meters to Centimeters")
    void validMeterstoCentimeters() {
        double meters = 2.30;
        double expected = 230;
        double actual = DistanceUtil.metersToCentimeters(meters);
        assertEquals(expected, actual, .001);
    }
}
