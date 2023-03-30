/* 2023 Written by Alumiboti FRC 5590 */
package org.alumiboti5590.eop.pid;

/**
 * PIDGains allows for easily storing PID values in an object that can be pulled
 * out and used against PID controllers. There are a slew of constructors but the
 * values given are final; they cannot be changed.
 * @see <a href="https://v5.docs.ctr-electronics.com/en/stable/ch16_ClosedLoop.html">CTRE Closed Loop Tutorial</a>
 * @see <a href="https://www.chiefdelphi.com/t/how-do-you-tune-the-pid-values/133667/4">Chief Delphi 'Tuning a PID Loop'</a>
 */
public class PIDGains {

    /** By default, let motors spin up to full speed. This should be adjusted using peak output parameters */
    public static final double DEFAULT_PEAK_OUTPUT = 1;

    public double kP, kI, kD, kFF, kIzone, kPeakMinOutput, kPeakMaxOutput;

    /**
     * PIDGains constructor with only the P, I, and D values provided
     * @param p proportional control value
     * @param i integral control value
     * @param d derivative control value
     */
    public PIDGains(double p, double i, double d) {
        this(p, i, d, 0, 0, DEFAULT_PEAK_OUTPUT);
    }

    /**
     * PIDGains constructor with all values but a mirrored peak value. Pass a number
     * 'x' and the peak outputs will be [-x, x]
     * @param p proportional control value
     * @param i integral control value
     * @param d derivative control value
     * @param feedForward the feed forward that should be applied by default. This is added to the end result
     * @param izone the integral zone that allows for larger error tolerances
     * @param peakOutput a max value for the motors that will also be negated for the min peak output
     */
    public PIDGains(double p, double i, double d, double feedForward, double izone, double peakOutput) {
        this(p, i, d, feedForward, izone, peakOutput == 0 ? 0 : -peakOutput, peakOutput);
    }

    /**
     * PIDGains constructor with all values potentially defined, including
     * non-symmetrical values for the peak minimum and maximum values.
     * @param p proportional control value
     * @param i integral control value
     * @param d derivative control value
     * @param feedForward the feed forward that should be applied by default. This is added to the end result
     * @param izone the integral zone that allows for larger error tolerances
     * @param peakMinOutput the minimum peak output allowed
     * @param peakMaxOutput the maximum peak output allowed
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
