package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.drivetrain.Drivetrain;
import com.pedropathing.localization.Localizer;
import com.pedropathing.math.Pose;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.DoubleSupplier;

/**
 * Driver-Station version of Pedro 3's Foresight AutoTune procedures (ForesightTuner.java).
 * Same measurements and same formulas, but you start each test with a gamepad button and
 * the results show up in telemetry (and in Logcat under the tag "ForesightManual").
 *
 * Uses YOUR Constants.drivetrainConfig and Constants.localizerConfig, so fix motor names/
 * directions and pod directions/offsets first, and make sure the localizer reads correctly.
 *
 * Controls:  dpad up/down = pick test | A = run | B = abort (hold) | X = results view | Y = clear all
 *
 * Run order (same as Pedro's tuner): 1, 2, 3, 4, 5, 6, 7, 8, 9, 10.
 * Tests 7 and 8 use the heading results from 5 and 6 to hold the robot straight.
 * Run each test 3+ times; the results view shows the average of all runs.
 */
@TeleOp(name = "Foresight Manual Tuner")
public class ForesightManualTuner extends LinearOpMode {

    // ---- settings ----
    static final double VELOCITY_RUN_DISTANCE = 48.0;  // in, for the max-velocity tests
    static final double DECEL_TARGET_VELOCITY = 30.0;  // in/s, speed to reach before cutting power
    static final double BRAKE_TEST_DISTANCE = 36.0;    // in, must be at least 15

    // ---- Pedro's own constants for the gain formulas ----
    static final double ALPHA_HEADING = 18.25;
    static final double ALPHA_LARGE = 10.2;
    static final double ALPHA_SMALL = 6.2;
    static final double BRAKE_KV_SCALE = 0.85;
    static final double BRAKING_POWER = 0.001; // tiny reverse power used during "braking" tests

    static final String TAG = "ForesightManual";

    static final String[] MENU = {
            "1  Max forward velocity",
            "2  Max strafe velocity",
            "3  Forward deceleration",
            "4  Strafe deceleration",
            "5  Heading braking (spins in place)",
            "6  Heading kP (spins in place)",
            "7  Forward braking (needs 5 and 6)",
            "8  Strafe braking (needs 5 and 6)",
            "9  Forward translational",
            "10 Strafe translational"
    };

    static final String[] KEYS = {
            "maxAchievableForwardVelocity",
            "maxAchievableStrafeVelocity",
            "naturalForwardDeceleration",
            "naturalStrafeDeceleration",
            "headingBrakeCoefficients.linear",
            "headingBrakeCoefficients.quadratic",
            "headingFeedback kP",
            "linearBrakeCoefficients.forward",
            "quadraticBrakeCoefficients.forward",
            "linearBrakeCoefficients.strafe",
            "quadraticBrakeCoefficients.strafe",
            "forwardTranslational PRIMARY kP (error > 2.5in)",
            "forwardTranslational SECONDARY kP (error < 2.5in)",
            "strafeTranslational PRIMARY kP (error > 2.5in)",
            "strafeTranslational SECONDARY kP (error < 2.5in)",
            "coast feedforward kV",
            "brake feedforward kV"
    };

    private static final String K_MAX_F = KEYS[0], K_MAX_S = KEYS[1], K_DEC_F = KEYS[2], K_DEC_S = KEYS[3];
    private static final String K_H_LIN = KEYS[4], K_H_QUAD = KEYS[5], K_H_KP = KEYS[6];
    private static final String K_FB_LIN = KEYS[7], K_FB_QUAD = KEYS[8], K_SB_LIN = KEYS[9], K_SB_QUAD = KEYS[10];
    private static final String K_FT_P = KEYS[11], K_FT_S = KEYS[12], K_ST_P = KEYS[13], K_ST_S = KEYS[14];
    private static final String K_COAST = KEYS[15], K_BRAKE = KEYS[16];

    private Localizer localizer;
    private Drivetrain drivetrain;
    private final LinkedHashMap<String, ArrayList<Double>> results = new LinkedHashMap<>();
    private String status = "Ready. Pick a test and press A.";

    @Override
    public void runOpMode() throws InterruptedException {
        localizer = new PinpointLocalizer(hardwareMap, Constants.localizerConfig);
        drivetrain = new Mecanum(hardwareMap, Constants.drivetrainConfig);
        localizer.setPose(Pose.zero());
        localizer.update();
        sleep(1000); // let the Pinpoint finish calibrating, like Pedro's tuners do

        telemetry.addLine("Foresight Manual Tuner ready. Press Start.");
        telemetry.update();
        waitForStart();

        int sel = 0;
        boolean showValues = false;
        boolean pUp = false, pDown = false, pA = false, pX = false, pY = false;

        while (opModeIsActive()) {
            if (gamepad1.dpad_down && !pDown) sel = (sel + 1) % MENU.length;
            if (gamepad1.dpad_up && !pUp) sel = (sel + MENU.length - 1) % MENU.length;
            if (gamepad1.x && !pX) showValues = !showValues;
            if (gamepad1.y && !pY) { results.clear(); status = "Cleared all results."; }
            if (gamepad1.a && !pA) runTest(sel);

            pUp = gamepad1.dpad_up; pDown = gamepad1.dpad_down;
            pA = gamepad1.a; pX = gamepad1.x; pY = gamepad1.y;

            telemetry.addLine("UP/DOWN pick | A run | B abort | X results | Y clear");
            if (!showValues) {
                for (int i = 0; i < MENU.length; i++) {
                    telemetry.addLine((i == sel ? "> " : "   ") + MENU[i]);
                }
                telemetry.addLine(status);
            } else {
                for (String k : KEYS) telemetry.addLine(valueLine(k));
            }
            telemetry.update();
        }
        drivetrain.stop();
    }

    private void runTest(int sel) {
        try {
            switch (sel) {
                case 0: maxVelocity(true); break;
                case 1: maxVelocity(false); break;
                case 2: deceleration(true); break;
                case 3: deceleration(false); break;
                case 4: headingBraking(); break;
                case 5: headingKp(); break;
                case 6: braking(true); break;
                case 7: braking(false); break;
                case 8: translational(true); break;
                case 9: translational(false); break;
                default: break;
            }
        } finally {
            drivetrain.stop();
        }
    }

    // ------------------------------------------------------------------ tests

    /** Mirrors Pedro's ForwardVelocity / StrafeVelocity: full power until distance, average the last 10 speeds. */
    private void maxVelocity(boolean forward) {
        prepare(forward ? "Max forward velocity: drives forward " + (int) VELOCITY_RUN_DISTANCE + " in at full power"
                        : "Max strafe velocity: strafes left " + (int) VELOCITY_RUN_DISTANCE + " in at full power");
        ArrayDeque<Double> speeds = new ArrayDeque<>();
        for (int i = 0; i < 10; i++) speeds.add(0.0);
        DrivePowers power = forward ? new DrivePowers(1, 0, 0) : new DrivePowers(0, 1, 0);
        ElapsedTime t = new ElapsedTime();
        boolean finished = false;

        while (!abort() && t.seconds() < 4) {
            localizer.update();
            double d = Math.abs(forward ? localizer.pose().x() : localizer.pose().y());
            if (d > VELOCITY_RUN_DISTANCE) { finished = true; break; }
            drivetrain.drive(power, forward); // flag copied from Pedro's tuner (true forward, false strafe)
            double cur = Math.abs(forward ? localizer.twist().toVector2D().x() : localizer.twist().toVector2D().y());
            speeds.addLast(cur);
            speeds.removeFirst();
        }
        drivetrain.stop();

        if (!finished) { status = "Max velocity: did not finish (aborted, timed out, or pods not reading)."; return; }
        double sum = 0;
        for (double s : speeds) sum += s;
        double avg = sum / speeds.size();
        record(forward ? K_MAX_F : K_MAX_S, avg);
        coastToStop();
        status = String.format(Locale.US, "Max %s velocity = %.2f in/s", forward ? "forward" : "strafe", avg);
    }

    /** Mirrors Pedro's ForwardDeceleration / StrafeDeceleration: average dv/dt after cutting power at ~30 in/s. */
    private void deceleration(boolean forward) {
        prepare(forward ? "Forward deceleration: drives forward until " + (int) DECEL_TARGET_VELOCITY + " in/s then coasts"
                        : "Strafe deceleration: strafes left until " + (int) DECEL_TARGET_VELOCITY + " in/s then coasts");
        DrivePowers power = forward ? new DrivePowers(1, 0, 0) : new DrivePowers(0, 1, 0);
        ElapsedTime t = new ElapsedTime();
        boolean reached = false;
        double prevV = 0;
        long prevT = 0;

        while (!abort() && t.seconds() < 3) {
            localizer.update();
            double cur = forward ? localizer.twist().toVector2D().x() : localizer.twist().toVector2D().y();
            if (Math.abs(cur) > DECEL_TARGET_VELOCITY) {
                prevV = cur;
                prevT = System.nanoTime();
                reached = true;
                drivetrain.stop(false);
                break;
            }
            drivetrain.drive(power, false);
        }
        if (!reached) { drivetrain.stop(); status = "Deceleration: never reached the target speed."; return; }

        ArrayList<Double> accelerations = new ArrayList<>();
        t.reset();
        while (!abort() && t.seconds() < 6) {
            drivetrain.stop(false);
            localizer.update();
            double cur = forward ? localizer.twist().toVector2D().x() : localizer.twist().toVector2D().y();
            long now = System.nanoTime();
            double dt = (now - prevT) / 1e9;
            if (dt > 0) accelerations.add((cur - prevV) / dt);
            prevV = cur;
            prevT = now;
            if (Math.abs(cur) <= 1) break;
        }
        drivetrain.stop();

        if (accelerations.isEmpty()) { status = "Deceleration: no samples."; return; }
        double sum = 0;
        for (double a : accelerations) sum += a;
        double decel = Math.abs(sum / accelerations.size());
        record(forward ? K_DEC_F : K_DEC_S, decel);
        status = String.format(Locale.US, "%s deceleration = %.2f in/s^2", forward ? "Forward" : "Strafe", decel);
    }

    /** Mirrors Pedro's HeadingBraking: spin back and forth at 12 powers, fit stop angle vs spin rate. */
    private void headingBraking() {
        prepare("Heading braking: spins in place back and forth (~1 min). Clear the area.");
        final int trials = 12;
        double[] powers = biasedGradient(trials, 1.0, 0.2, 1.5);
        ArrayList<double[]> data = new ArrayList<>();
        ElapsedTime timer = new ElapsedTime();
        ElapsedTime total = new ElapsedTime();
        int iteration = 0;
        int state = 0; // 0 = spin up, 1 = brake
        double totalHeading = 0, prevHeading = 0, startHeading = 0, measuredVelocity = 0;
        timer.reset();

        while (!abort() && iteration < trials && total.seconds() < 120) {
            localizer.update();
            double ch = localizer.pose().heading();
            totalHeading += norm(ch - prevHeading);
            prevHeading = ch;

            int direction = (iteration % 2 == 0) ? 1 : -1;
            double power = powers[iteration];

            if (state == 0) {
                if (timer.seconds() > 2) {
                    startHeading = totalHeading;
                    measuredVelocity = Math.abs(localizer.velocity().omega);
                    drivetrain.drive(new DrivePowers(0, 0, -BRAKING_POWER * direction), false);
                    state = 1;
                    timer.reset();
                } else {
                    drivetrain.drive(new DrivePowers(0, 0, power * direction), false);
                }
            } else {
                if (Math.abs(localizer.velocity().omega) > 0.001 && timer.seconds() < 3) {
                    drivetrain.drive(new DrivePowers(0, 0, -BRAKING_POWER * direction), false);
                } else {
                    data.add(new double[]{measuredVelocity, Math.abs(totalHeading - startHeading)});
                    iteration++;
                    timer.reset();
                    state = 0;
                }
            }
        }
        drivetrain.stop();

        if (iteration < trials) { status = "Heading braking: did not finish."; return; }
        double[] c = fitThroughOrigin(data);
        if (c == null) { status = "Heading braking: fit failed."; return; }
        record(K_H_LIN, c[0]);
        record(K_H_QUAD, c[1]);
        status = String.format(Locale.US, "Heading braking linear=%.5f quadratic=%.5f", c[0], c[1]);
    }

    /** Mirrors Pedro's HeadingTuner: step response at power 0.4, kP = tau * alpha^2 / K. */
    private void headingKp() {
        prepare("Heading kP: spins in place for ~1.2 s");
        double[] id = stepResponse(new DrivePowers(0, 0, 0.4), 0.4, () -> Math.abs(localizer.velocity().omega));
        if (id == null) return;
        double kP = id[0] * ALPHA_HEADING * ALPHA_HEADING / id[1];
        record(K_H_KP, kP);
        status = String.format(Locale.US, "Heading kP = %.4f (tau %.4f, K %.4f)", kP, id[0], id[1]);
    }

    /** Mirrors Pedro's ForwardBraking / StrafeBraking: drive out and back at 5 powers with 0.001 reverse power. */
    private void braking(boolean forward) {
        double hLin = avg(K_H_LIN), hQuad = avg(K_H_QUAD), hKp = avg(K_H_KP);
        String note = "";
        if (Double.isNaN(hLin) || Double.isNaN(hQuad) || Double.isNaN(hKp)) {
            hLin = 0; hQuad = 0; hKp = 0;
            note = " (heading hold OFF: run tests 5 and 6 first for best results)";
        }
        double distance = Math.max(BRAKE_TEST_DISTANCE, 15.0);
        prepare((forward ? "Forward" : "Strafe") + " braking: drives out " + (int) distance
                + " in and back at several speeds. Clear space." + note);

        double maxPower = forward ? 0.7 : 1.0;
        double[] powers = biasedGradient(5, maxPower, 0.3, 1.5);
        ArrayList<double[]> data = new ArrayList<>();
        ElapsedTime timer = new ElapsedTime();
        ElapsedTime total = new ElapsedTime();
        int iteration = 0;
        int state = 0; // 0 drive, 1 brake, 2 wait
        double startX = 0, startY = 0, measuredV = 0;
        timer.reset();

        while (!abort() && iteration < powers.length && total.seconds() < 90) {
            localizer.update();
            int direction = (iteration % 2 == 0) ? 1 : -1;
            double power = powers[iteration];
            double pos = Math.abs(forward ? localizer.pose().x() : localizer.pose().y());

            if (state == 0) {
                boolean atEnd = forward
                        ? ((direction > 0 && pos >= distance) || (direction < 0 && pos <= 12))
                        : ((direction > 0 && pos > distance) || (direction < 0 && pos <= 6));
                if (atEnd) {
                    startX = localizer.pose().x();
                    startY = localizer.pose().y();
                    measuredV = localizer.velocity().toVector2D().magnitude();
                    brakeStep(forward, direction, hLin, hQuad, hKp);
                    state = 1;
                    timer.reset();
                } else {
                    double hp = headingPower(hLin, hQuad, hKp);
                    drivetrain.drive(forward ? new DrivePowers(power * direction, 0, hp)
                                             : new DrivePowers(0, power * direction, hp), false);
                }
            } else if (state == 1) {
                if (localizer.velocity().toVector2D().magnitude() > 0.25 && timer.seconds() < 7) {
                    brakeStep(forward, direction, hLin, hQuad, hKp);
                } else {
                    double dist = Math.hypot(localizer.pose().x() - startX, localizer.pose().y() - startY);
                    data.add(new double[]{measuredV, dist});
                    iteration++;
                    state = 2;
                    timer.reset();
                }
            } else {
                drivetrain.stop();
                if (timer.seconds() > 1) state = 0;
            }
        }
        drivetrain.stop();

        if (iteration < powers.length) { status = "Braking: did not finish."; return; }
        double[] c = fitThroughOrigin(data);
        if (c == null) { status = "Braking: fit failed."; return; }
        record(forward ? K_FB_LIN : K_SB_LIN, c[0]);
        record(forward ? K_FB_QUAD : K_SB_QUAD, c[1]);
        status = String.format(Locale.US, "%s braking linear=%.5f quadratic=%.6f", forward ? "Forward" : "Strafe", c[0], c[1]);
    }

    /** Mirrors Pedro's ForwardTranslational / StrafeTranslational. */
    private void translational(boolean forward) {
        prepare(forward ? "Forward translational: moves forward ~1.2 s (about 12-24 in)"
                        : "Strafe translational: moves left ~1.2 s (about 12-24 in)");
        double[] id = forward
                ? stepResponse(new DrivePowers(0.4, 0, 0), 0.4, () -> Math.abs(localizer.twist().toVector2D().x()))
                : stepResponse(new DrivePowers(0, 0.4, 0), 0.4, () -> Math.abs(localizer.twist().toVector2D().y()));
        if (id == null) return;
        double tau = id[0], k = id[1];
        double primary = tau * ALPHA_LARGE * ALPHA_LARGE / k;
        double secondary = tau * ALPHA_SMALL * ALPHA_SMALL / k;
        record(forward ? K_FT_P : K_ST_P, primary);
        record(forward ? K_FT_S : K_ST_S, secondary);
        if (forward) { // Pedro takes the feedforwards from the forward test only
            double kv = 1.0 / k;
            record(K_COAST, kv);
            record(K_BRAKE, kv * BRAKE_KV_SCALE);
        }
        status = String.format(Locale.US, "%s translational primary=%.4f secondary=%.4f",
                forward ? "Forward" : "Strafe", primary, secondary);
    }

    // ---------------------------------------------------------------- helpers

    /** Applies power for 1.2 s, then fits the speed curve. Returns {tau, K}, or null on failure. */
    private double[] stepResponse(DrivePowers p, double power, DoubleSupplier speed) {
        ArrayList<Double> times = new ArrayList<>();
        ArrayList<Double> speeds = new ArrayList<>();
        ElapsedTime timer = new ElapsedTime();
        timer.reset();
        drivetrain.drive(p, false);
        while (!abort()) {
            localizer.update();
            times.add(timer.seconds());
            speeds.add(speed.getAsDouble());
            if (timer.seconds() >= 1.2) break;
            drivetrain.drive(p, false);
        }
        drivetrain.stop();
        if (abort()) { status = "Aborted."; return null; }

        int n = times.size();
        if (n < 4) { status = "Step response: too few samples."; return null; }
        int start = Math.max(0, n - 15);
        double sum = 0;
        for (int i = start; i < n; i++) sum += speeds.get(i);
        double k = (sum / (n - start)) / power;
        if (!(k > 0)) { status = "Step response: no motion detected. Check localizer and motors."; return null; }

        ArrayList<Double> xs = new ArrayList<>();
        ArrayList<Double> ys = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            double v = speeds.get(i) / power;
            if (v > 0.8 * k) continue;
            if (v < 0.1 * k) continue;
            ys.add(Math.log(k - v));
            xs.add(times.get(i));
        }
        if (xs.size() < 2) { status = "Step response: not enough usable samples."; return null; }
        double slope = slope(xs, ys);
        double tau = -1.0 / slope;
        if (Double.isNaN(tau) || Double.isInfinite(tau) || tau <= 0) {
            status = "Step response: fit failed. Try again.";
            return null;
        }
        return new double[]{tau, k};
    }

    private double headingPower(double hLin, double hQuad, double hKp) {
        double w = localizer.velocity().omega;
        double brakeDist = hLin * w + hQuad * w * w * Math.signum(w);
        double headingError = norm(-localizer.pose().heading());
        double error = headingError - brakeDist;
        return Math.max(-0.3, Math.min(1.0, hKp * error)) / 2;
    }

    private void brakeStep(boolean forward, int direction, double hLin, double hQuad, double hKp) {
        double hp = headingPower(hLin, hQuad, hKp);
        double brake = -BRAKING_POWER * direction;
        double minBrake = Math.abs(hp) + 0.001;
        if (direction > 0) brake = Math.min(brake, -minBrake);
        else brake = Math.max(brake, minBrake);
        drivetrain.drive(forward ? new DrivePowers(brake, 0, hp) : new DrivePowers(0, brake, hp), false);
    }

    private void prepare(String message) {
        drivetrain.stop();
        telemetry.addLine(message);
        telemetry.addLine("Starting in 2 s. Hold B to abort.");
        telemetry.update();
        sleep(2000);
        localizer.setPose(Pose.zero());
        localizer.update();
    }

    private void coastToStop() {
        ElapsedTime t = new ElapsedTime();
        while (!abort() && t.seconds() < 3) {
            localizer.update();
            if (localizer.velocity().toVector2D().magnitude() < 1) break;
        }
    }

    private boolean abort() {
        return gamepad1.b || !opModeIsActive();
    }

    private void record(String key, double value) {
        results.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        RobotLog.ii(TAG, key + " = " + value);
    }

    private double avg(String key) {
        ArrayList<Double> l = results.get(key);
        if (l == null || l.isEmpty()) return Double.NaN;
        double s = 0;
        for (double v : l) s += v;
        return s / l.size();
    }

    private String valueLine(String key) {
        ArrayList<Double> l = results.get(key);
        if (l == null || l.isEmpty()) return key + " = --";
        return key + " = " + String.format(Locale.US, "%.6f", avg(key)) + "  (n=" + l.size() + ")";
    }

    private static double norm(double a) {
        while (a > Math.PI) a -= 2 * Math.PI;
        while (a < -Math.PI) a += 2 * Math.PI;
        return a;
    }

    /** Least-squares fit of distance = a*v + b*v^2 (no constant term), which is how Pedro uses the coefficients. */
    private static double[] fitThroughOrigin(List<double[]> data) {
        double s2 = 0, s3 = 0, s4 = 0, r1 = 0, r2 = 0;
        for (double[] p : data) {
            double v = p[0], y = p[1];
            s2 += v * v;
            s3 += v * v * v;
            s4 += v * v * v * v;
            r1 += v * y;
            r2 += v * v * y;
        }
        double det = s2 * s4 - s3 * s3;
        if (Math.abs(det) < 1e-12) return null;
        return new double[]{(r1 * s4 - r2 * s3) / det, (s2 * r2 - s3 * r1) / det};
    }

    private static double slope(List<Double> x, List<Double> y) {
        int n = x.size();
        double mx = 0, my = 0;
        for (int i = 0; i < n; i++) { mx += x.get(i); my += y.get(i); }
        mx /= n;
        my /= n;
        double num = 0, den = 0;
        for (int i = 0; i < n; i++) {
            num += (x.get(i) - mx) * (y.get(i) - my);
            den += (x.get(i) - mx) * (x.get(i) - mx);
        }
        return den == 0 ? 0 : num / den;
    }

    private static double[] biasedGradient(int count, double max, double min, double bias) {
        if (count < 2) return new double[]{max};
        double[] values = new double[count];
        for (int i = 0; i < count; i++) {
            double t = (double) i / (count - 1);
            values[i] = min + (1 - Math.pow(t, bias)) * (max - min);
        }
        return values;
    }
}
