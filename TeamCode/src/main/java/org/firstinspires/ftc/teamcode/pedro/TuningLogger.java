package org.firstinspires.ftc.teamcode.pedro;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/**
 * Measures max velocity and stopping behavior using only the Pinpoint driver
 * (no Pedro classes), so it works on any Pedro version.
 *
 * Controls:
 *   dpad up/down  : choose axis (FORWARD / STRAFE)
 *   dpad left     : toggle motor zero-power mode (FLOAT / BRAKE)
 *   hold A        : drive at full power along the chosen axis
 *   release A     : cut power, then log start speed + stopping distance
 *   B             : reset max speed and results
 *
 * Use plenty of open floor (about 8 ft+). Run each test several times and average.
 */
@TeleOp(name = "Tuning Logger")
public class TuningLogger extends LinearOpMode {

    // MEASURE THESE ON YOUR ROBOT (inches, from the robot's center / tracking point).
    // Per the goBILDA Pinpoint guide:
    //   X_POD_OFFSET = how far SIDEWAYS the forward-facing (X) pod is. LEFT = positive, RIGHT = negative.
    //   Y_POD_OFFSET = how far FORWARD the strafe-facing (Y) pod is. FORWARD = positive, BACK = negative.
    static final double X_POD_OFFSET = 0.0;  // TODO enter yours
    static final double Y_POD_OFFSET = 0.0;  // TODO enter yours

    DcMotor lf, lr, rf, rr;
    GoBildaPinpointDriver pinpoint;

    boolean forwardAxis = true;
    boolean brakeMode = false;
    boolean wasDriving = false;
    boolean measuring = false;

    double maxSpeed = 0;
    double startSpeed = 0, startPos = 0;
    double lastStopDistance = 0, lastStartSpeed = 0, lastDecel = 0;

    boolean prevUp, prevDown, prevLeft, prevB, prevRB, prevLB;
    double testPower = 1.0; // right bumper +0.1, left bumper -0.1 (only while stopped)

    @Override
    public void runOpMode() {
        lf = hardwareMap.get(DcMotor.class, "lf");
        lr = hardwareMap.get(DcMotor.class, "lr");
        rf = hardwareMap.get(DcMotor.class, "rf");
        rr = hardwareMap.get(DcMotor.class, "rr");

        lf.setDirection(DcMotorSimple.Direction.REVERSE);
        lr.setDirection(DcMotorSimple.Direction.REVERSE);
        rf.setDirection(DcMotorSimple.Direction.FORWARD);
        rr.setDirection(DcMotorSimple.Direction.FORWARD);

        for (DcMotor m : new DcMotor[]{lf, lr, rf, rr}) {
            m.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        applyZeroPower();

        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        pinpoint.setOffsets(X_POD_OFFSET, Y_POD_OFFSET, DistanceUnit.INCH);
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD); // change if different pods
        pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD,
                                      GoBildaPinpointDriver.EncoderDirection.FORWARD);
        pinpoint.resetPosAndIMU();

        telemetry.addLine("Ready. Place robot with lots of open space ahead.");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            pinpoint.update();

            // Pinpoint: X = forward, Y = left
            double vel = Math.abs(forwardAxis
                    ? pinpoint.getVelX(DistanceUnit.INCH)
                    : pinpoint.getVelY(DistanceUnit.INCH));
            double pos = forwardAxis
                    ? pinpoint.getPosX(DistanceUnit.INCH)
                    : pinpoint.getPosY(DistanceUnit.INCH);

            // Settings (only change while stopped)
            if (!wasDriving && !measuring) {
                if (gamepad1.dpad_up && !prevUp) forwardAxis = true;
                if (gamepad1.dpad_down && !prevDown) forwardAxis = false;
                if (gamepad1.dpad_left && !prevLeft) { brakeMode = !brakeMode; applyZeroPower(); }
                if (gamepad1.right_bumper && !prevRB) testPower = Math.min(1.0, testPower + 0.1);
                if (gamepad1.left_bumper && !prevLB) testPower = Math.max(0.2, testPower - 0.1);
            }
            prevRB = gamepad1.right_bumper; prevLB = gamepad1.left_bumper;
            if (gamepad1.b && !prevB) {
                maxSpeed = 0; lastStopDistance = 0; lastStartSpeed = 0; lastDecel = 0;
            }
            prevUp = gamepad1.dpad_up; prevDown = gamepad1.dpad_down;
            prevLeft = gamepad1.dpad_left; prevB = gamepad1.b;

            boolean driving = gamepad1.a;
            if (driving) {
                maxSpeed = Math.max(maxSpeed, vel);
                setPower(testPower);
                measuring = false;
            } else {
                setPower(0.0);
                if (wasDriving) { // A just released
                    startSpeed = vel;
                    startPos = pos;
                    measuring = true;
                }
                if (measuring && vel < 1.0) { // stopped (under 1 in/s)
                    lastStartSpeed = startSpeed;
                    lastStopDistance = Math.abs(pos - startPos);
                    // rough constant-deceleration estimate: v^2 / (2d)
                    lastDecel = lastStopDistance > 0.1
                            ? (startSpeed * startSpeed) / (2 * lastStopDistance) : 0;
                    measuring = false;
                }
            }
            wasDriving = driving;

            telemetry.addData("Axis (dpad up/down)", forwardAxis ? "FORWARD" : "STRAFE");
            telemetry.addData("Zero power (dpad left)", brakeMode ? "BRAKE" : "FLOAT");
            telemetry.addData("Test power (bumpers)", "%.1f", testPower);
            telemetry.addLine("Hold A to drive, release to test stop");
            // Pod direction check: push the robot BY HAND (motors off). Forward => X must increase, left => Y must increase.
            telemetry.addData("Pinpoint X (fwd +)", "%.2f", pinpoint.getPosX(DistanceUnit.INCH));
            telemetry.addData("Pinpoint Y (left +)", "%.2f", pinpoint.getPosY(DistanceUnit.INCH));
            telemetry.addData("Current speed (in/s)", "%.2f", vel);
            telemetry.addData("MAX speed (in/s)", "%.2f", maxSpeed);
            telemetry.addData("Measuring stop", measuring);
            telemetry.addData("Last start speed (in/s)", "%.2f", lastStartSpeed);
            telemetry.addData("Last stop distance (in)", "%.2f", lastStopDistance);
            telemetry.addData("Est. decel (in/s^2)", "%.2f", lastDecel);
            telemetry.update();
        }
        setPower(0);
    }

    void applyZeroPower() {
        DcMotor.ZeroPowerBehavior b = brakeMode
                ? DcMotor.ZeroPowerBehavior.BRAKE : DcMotor.ZeroPowerBehavior.FLOAT;
        lf.setZeroPowerBehavior(b); lr.setZeroPowerBehavior(b);
        rf.setZeroPowerBehavior(b); rr.setZeroPowerBehavior(b);
    }

    void setPower(double p) {
        if (forwardAxis) {
            lf.setPower(p); lr.setPower(p); rf.setPower(p); rr.setPower(p);
        } else { // strafe right
            lf.setPower(p); lr.setPower(-p); rf.setPower(-p); rr.setPower(p);
        }
    }
}
