package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import com.pedropathing.api.Paths;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous(name = "Auto Park Left", group = "Autonomous")
public class Auto_Park_Left extends OpMode {

    private static final double STRAFE_POWER = 0.25;   // max power
    private static final double STRAFE_TIME_SEC = 2.0;

    private DcMotorEx leftFront, rightFront, leftBack, rightBack;
    private final ElapsedTime timer = new ElapsedTime();
    private boolean done = false;

    @Override
    public void init() {
        // Change these names to match your robot config
        leftFront = hardwareMap.get(DcMotorEx.class, "lf");
        rightFront = hardwareMap.get(DcMotorEx.class, "rf");
        leftBack = hardwareMap.get(DcMotorEx.class, "lr");
        rightBack = hardwareMap.get(DcMotorEx.class, "rr");

        // Standard mecanum: reverse the left side
        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightBack.setDirection(DcMotorSimple.Direction.FORWARD);

        for (DcMotorEx m : new DcMotorEx[]{leftFront, rightFront, leftBack, rightBack}) {
            m.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
    }

    @Override
    public void start() {
        timer.reset();
    }

    @Override
    public void loop() {
        if (!done && timer.seconds() < STRAFE_TIME_SEC) {
            // Strafe left: LF back, RF forward, LB forward, RB back
            leftFront.setPower(-STRAFE_POWER);
            rightFront.setPower(STRAFE_POWER);
            leftBack.setPower(STRAFE_POWER);
            rightBack.setPower(-STRAFE_POWER);

        } else {
            done = true;
            stopDrive();
        }

        telemetry.addData("Time", "%.2f / %.1f s", timer.seconds(), STRAFE_TIME_SEC);
        telemetry.addData("Done", done);
        telemetry.update();
    }

    @Override
    public void stop() {
        stopDrive();
    }

    private void stopDrive() {
        leftFront.setPower(0);
        rightFront.setPower(0);
        leftBack.setPower(0);
        rightBack.setPower(0);
    }
}