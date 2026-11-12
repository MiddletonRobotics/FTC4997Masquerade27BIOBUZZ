package org.firstinspires.ftc.teamcode.pedro;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.algorithm.Foresight;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Repeatable test runner for hand-tuning Foresight values.
 *
 * Workflow:
 *   1. Put the robot at the origin, facing the +X direction, with room ahead (and to the left).
 *   2. DPAD UP / DOWN: pick a test.   A: start.
 *   3. Watch the robot. Press A again to stop it.
 *   4. Edit values in Panels (or FTC Dashboard) under "Constants".
 *   5. Put the robot back at the start and press A. Values are re-read on every start.
 */
@Configurable
@TeleOp(name = "Foresight Live Tuner", group = "Tuning")
public class ForesightLiveTuner extends LinearOpMode {
    public static double DISTANCE_INCHES = 48;
    public static double DWELL_SECONDS = 1.0;      // pause at the end of each path before the next leg
    public static double TURN_HOLD_SECONDS = 2.0;  // time spent on each heading in the turn test

    enum Test { LINE_FORWARD, LINE_STRAFE, CURVE, TURN_90, HOLD }

    private PinpointLocalizer localizer;
    private Mecanum drivetrain;
    private Follower follower;

    private Test test = Test.LINE_FORWARD;
    private boolean running = false;
    private boolean legA = true;
    private boolean atEnd = false;
    private Path pathA, pathB;
    private final ElapsedTime timer = new ElapsedTime();

    @Override
    public void runOpMode() throws InterruptedException {
        localizer = new PinpointLocalizer(hardwareMap, Constants.localizerConfig);
        drivetrain = new Mecanum(hardwareMap, Constants.drivetrainConfig);
        localizer.setPose(Pose.zero());
        Thread.sleep(1000);

        boolean prevA = false, prevUp = false, prevDown = false;

        while (!isStarted() && !isStopRequested()) {
            telemetry.addLine("Ready. Press START, then use DPAD to pick a test and A to run it.");
            telemetry.update();
        }
        waitForStart();

        while (opModeIsActive()) {
            boolean a = gamepad1.a, up = gamepad1.dpad_up, down = gamepad1.dpad_down;

            if (!running) {
                Test[] all = Test.values();
                if (up && !prevUp) test = all[(test.ordinal() + 1) % all.length];
                if (down && !prevDown) test = all[(test.ordinal() + all.length - 1) % all.length];
            }
            if (a && !prevA) {
                if (running) stopTest(); else startTest();
            }
            prevA = a; prevUp = up; prevDown = down;

            if (running) tick();

            Pose p = localizer.pose();
            telemetry.addData("Test", test);
            telemetry.addData("State", running ? "RUNNING (A = stop)" : "STOPPED (A = start, DPAD = change test)");
            telemetry.addData("x (in)", p.x());
            telemetry.addData("y (in)", p.y());
            telemetry.addData("heading (deg)", Math.toDegrees(p.heading()));
            telemetry.addLine("Edit Constants in Panels, reposition robot, press A.");
            telemetry.update();
        }

        drivetrain.stop();
    }

    private void startTest() {
        // New Foresight each run, so edited Constants take effect. Localizer/drivetrain are reused.
        follower = new Follower(localizer, drivetrain, new Foresight(Constants.buildForesightConfig()));
        follower.setPose(Pose.zero());
        follower.update();

        double d = DISTANCE_INCHES;
        switch (test) {
            case LINE_FORWARD:
                pathA = line(Pose.zero(), new Pose(d, 0, 0)).constant(0);
                pathB = line(new Pose(d, 0, 0), Pose.zero()).constant(0);
                break;
            case LINE_STRAFE:
                pathA = line(Pose.zero(), new Pose(0, d, 0)).constant(0);
                pathB = line(new Pose(0, d, 0), Pose.zero()).constant(0);
                break;
            case CURVE:
                pathA = curve(Pose.zero(), new Pose(d, 0), new Pose(d, d)).tangent();
                pathB = curve(new Pose(d, d), new Pose(d, 0), Pose.zero()).tangent();
                break;
            default:
                break;
        }

        legA = true;
        running = true;
        beginLeg();
    }

    private void stopTest() {
        running = false;
        drivetrain.stop();
    }

    private void beginLeg() {
        timer.reset();
        atEnd = false;
        switch (test) {
            case LINE_FORWARD:
            case LINE_STRAFE:
            case CURVE:
                follower.follow(legA ? pathA : pathB);
                break;
            case TURN_90:
                follower.hold(legA ? new Pose(0, 0, Math.PI / 2) : Pose.zero());
                break;
            case HOLD:
                follower.hold(Pose.zero());
                break;
        }
    }

    private void tick() {
        follower.update();
        switch (test) {
            case LINE_FORWARD:
            case LINE_STRAFE:
            case CURVE:
                if (!atEnd && follower.atParametricEnd()) {
                    atEnd = true;
                    timer.reset();
                }
                if (atEnd && timer.seconds() > DWELL_SECONDS) {
                    legA = !legA;
                    beginLeg();
                }
                break;
            case TURN_90:
                if (timer.seconds() > TURN_HOLD_SECONDS) {
                    legA = !legA;
                    beginLeg();
                }
                break;
            case HOLD:
                break;
        }
    }
}