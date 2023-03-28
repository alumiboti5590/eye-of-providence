/* 2023 Written by Alumiboti FRC 5590 */
package org.alumiboti5590.eop.pid;

/**
 * PIDGains allows for easily storing PID values in an object that can be pulled
 * out and used against PID controllers. There are a slew of constructors but the
 * values given are final; they cannot be changed.
 */
public class PIDGains {

    public static final double DEFAULT_PEAK_OUTPUT = 1;

    public double kP, kI, kD, kFF, kIzone, kPeakMinOutput, kPeakMaxOutput;

    /**
     * PIDGains constructor with only the P, I, and D values provided
     */
    public PIDGains(double p, double i, double d) {
        this(p, i, d, 0, 0, DEFAULT_PEAK_OUTPUT);
    }

    /**
     * PIDGains constructor with all values but a mirrored peak value. Pass a number
     * 'x' and the peak outputs will be [-x, x]
     */
    public PIDGains(double p, double i, double d, double feedForward, double izone, double peakOutput) {
        this(p, i, d, feedForward, izone, peakOutput == 0 ? 0 : -peakOutput, peakOutput);
    }

    /**
     * PIDGains constructor with all values potentially defined, including
     * non-symmetrical values for the peak minimum and maximum values.
     */
    public PIDGains(
            double p,
            double i,
            double d,
            double feedForward,
            double izone,
            double peakMinOutput,
            double peakMaxOutput) {
        this.kP = p;
        this.kI = i;
        this.kD = d;
        this.kFF = feedForward;
        this.kIzone = izone;
        this.kPeakMinOutput = peakMinOutput;
        this.kPeakMaxOutput = peakMaxOutput;
    }
}
