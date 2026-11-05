package org.firstinspires.ftc.teamcode.OpModes;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImpl;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.util.Range;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.button.Trigger;
import com.seattlesolvers.solverslib.hardware.motors.Motor;

@TeleOp(name = "MotorTestOp")
public class MotorTestOp extends OpMode {
    private Motor rf, rr, lf, lr, intake, transfer;
    private DcMotorEx shooter_left, shooter_right;
    private Servo depollonator;
    private Servo shootblocker;
    private double rpm = 100;
    private double ticks_per_rev = 28;
    private double tps;
    private boolean depollonator_toggle = false;



    public void init(){
        rf = new Motor(hardwareMap, "rf", Motor.GoBILDA.RPM_435);
        rr = new Motor(hardwareMap, "rr", Motor.GoBILDA.RPM_435);
        lf = new Motor(hardwareMap, "lf", Motor.GoBILDA.RPM_435);
        lr = new Motor(hardwareMap, "lr", Motor.GoBILDA.RPM_435);
        intake = new Motor(hardwareMap, "intake", Motor.GoBILDA.RPM_435);
        transfer = new Motor(hardwareMap, "transfer", Motor.GoBILDA.RPM_435);
        shooter_left = hardwareMap.get(DcMotorEx.class, "shooterOne");
        shooter_right = hardwareMap.get(DcMotorEx.class, "shooterTwo");

        shooter_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shooter_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        shooter_left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooter_right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        shooter_left.setDirection(DcMotorSimple.Direction.REVERSE);
        depollonator = hardwareMap.get(Servo.class, "depollonator");
        //depollonator.setPwmRange(new PwmControl.PwmRange(500, 2500));
        shootblocker = hardwareMap.get(Servo.class, "shootblocker");
        //shootblocker.setPwmRange(new PwmControl.PwmRange(500, 2500));

        rf.setRunMode(Motor.RunMode.VelocityControl);
        rr.setRunMode(Motor.RunMode.VelocityControl);
        lf.setRunMode(Motor.RunMode.VelocityControl);
        lr.setRunMode(Motor.RunMode.VelocityControl);

        rf.setInverted(true);
        rr.setInverted(true);
        lf.setInverted(true);
        lr.setInverted(true);

    }
    public void loop() {
        final double drivetrain_velocity = 1000;
        final double intake_velocity = 2000;
        final double transfer_velocity = 2000;
        final double shooter_velocity = 2796;
        double currentVelocity;

        double direction = gamepad1.x ? -1.0 : 1.0;

        rf.set(0);
        rr.set(0);
        lf.set(0);
        lr.set(0);
        intake.set(0);
        transfer.set(0);
        shooter_left.setVelocity(0);
        shooter_right.setVelocity(0);
        tps = (rpm * ticks_per_rev) / 60;


        if (gamepad1.dpad_up && gamepad1.dpad_right) rf.set(direction * drivetrain_velocity);
        if (gamepad1.dpad_down && gamepad1.dpad_right) rr.set(direction * drivetrain_velocity);
        if (gamepad1.dpad_up && gamepad1.dpad_left) lf.set(direction * drivetrain_velocity);
        if (gamepad1.dpad_down && gamepad1.dpad_left) lr.set(direction * drivetrain_velocity);

        if (gamepad1.left_bumper) intake.set(intake_velocity * direction);
        if (gamepad1.right_bumper) transfer.set(transfer_velocity * direction);
        if (gamepad1.right_trigger>0.5){
            currentVelocity = (shooter_right.getVelocity() * 60) / ticks_per_rev;
            tps = (rpm * ticks_per_rev) / 60;
            shooter_right.setVelocity(tps);
            shooter_left.setVelocity(tps);
            telemetry.addData("current rpm", currentVelocity);
            telemetry.addData("target tps", tps);
            telemetry.addData("actual tps", shooter_right.getVelocity());
            telemetry.addData("power", shooter_right.getPower());
        } else {
            shooter_right.setVelocity(0);
            shooter_left.setVelocity(0);
        }
        if (gamepad1.triangle){
            depollonator_toggle = !depollonator_toggle;
        }
        if (depollonator_toggle){
            depollonator.setPosition(0);
        } else {
            depollonator.setPosition(1);
        }
        telemetry.update();
    }
}
