/* 2023 Written by Alumiboti FRC 5590 */
package org.alumiboti.eop.pid;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PIDGainsTest {
    @Test
    @DisplayName("Simple PIDGains Constructor")
    public void simplePIDGainsConstructor() {
        double p = .1, i = .005, d = 100;
        PIDGains gains = new PIDGains(p, i, d);
        assertEquals(p, gains.kP);
        assertEquals(i, gains.kI);
        assertEquals(d, gains.kD);
        assertEquals(0, gains.kFF);
        assertEquals(0, gains.kIzone);
        assertEquals(-PIDGains.DEFAULT_PEAK_OUTPUT, gains.kPeakMinOutput);
        assertEquals(PIDGains.DEFAULT_PEAK_OUTPUT, gains.kPeakMaxOutput);
    }

    @Test
    @DisplayName("PIDGains Constructor with Same Peak Output")
    public void samePeakPIDGainsConstructor() {
        double p = .1, i = .005, d = 100;
        PIDGains gains = new PIDGains(p, i, d, 0, 0, .5);
        assertEquals(p, gains.kP);
        assertEquals(i, gains.kI);
        assertEquals(d, gains.kD);
        assertEquals(0, gains.kFF);
        assertEquals(0, gains.kIzone);
        assertEquals(-.5, gains.kPeakMinOutput);
        assertEquals(.5, gains.kPeakMaxOutput);
    }

    @Test
    @DisplayName("PIDGains Constructor with Everything Defined")
    public void kitchenSinkPIDGainsConstructor() {
        double p = .1, i = .005, d = 100, ff = .05, izone = .2, minPeak = -.1, maxPeak = .3;
        PIDGains gains = new PIDGains(p, i, d, ff, izone, minPeak, maxPeak);
        assertEquals(p, gains.kP);
        assertEquals(i, gains.kI);
        assertEquals(d, gains.kD);
        assertEquals(ff, gains.kFF);
        assertEquals(izone, gains.kIzone);
        assertEquals(minPeak, gains.kPeakMinOutput);
        assertEquals(maxPeak, gains.kPeakMaxOutput);
    }
}
