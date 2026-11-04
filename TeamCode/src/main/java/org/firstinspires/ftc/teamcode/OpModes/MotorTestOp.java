package org.firstinspires.ftc.teamcode.OpModes;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImpl;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.util.Range;
import com.seattlesolvers.solverslib.command.button.Trigger;
import com.seattlesolvers.solverslib.hardware.motors.Motor;

@TeleOp(name = "MotorTestOp")
public class MotorTestOp extends OpMode {
    private Motor rf, rr, lf, lr, intake, transfer, shooter_left, shooter_right;
    private Servo depollonator;
    private Servo shootblocker;

    double positiona;
    double positionb;


    public void init(){
        rf = new Motor(hardwareMap, "rf", Motor.GoBILDA.RPM_435);
        rr = new Motor(hardwareMap, "rr", Motor.GoBILDA.RPM_435);
        lf = new Motor(hardwareMap, "lf", Motor.GoBILDA.RPM_435);
        lr = new Motor(hardwareMap, "lr", Motor.GoBILDA.RPM_435);
        intake = new Motor(hardwareMap, "intake", Motor.GoBILDA.RPM_435);
        transfer = new Motor(hardwareMap, "transfer", Motor.GoBILDA.RPM_435);
        shooter_left = new Motor(hardwareMap, "shooterOne", Motor.GoBILDA.RPM_435);
        shooter_right = new Motor(hardwareMap, "shooterTwo", Motor.GoBILDA.RPM_435);
        depollonator = hardwareMap.get(Servo.class, "depollonator");
        //depollonator.setPwmRange(new PwmControl.PwmRange(500, 2500));
        positiona = 0;
        shootblocker = hardwareMap.get(Servo.class, "shootblocker");
        //shootblocker.setPwmRange(new PwmControl.PwmRange(500, 2500));
        positionb = 0;

        rf.setRunMode(Motor.RunMode.VelocityControl);
        rr.setRunMode(Motor.RunMode.VelocityControl);
        lf.setRunMode(Motor.RunMode.VelocityControl);
        lr.setRunMode(Motor.RunMode.VelocityControl);

        rf.setInverted(true);
        rr.setInverted(true);
        lf.setInverted(true);
        lr.setInverted(true);

    }
    public void loop(){
        final double drivetrain_velocity   = 1000;
        final double intake_velocity  = 2000;
        final double transfer_velocity = 2000;
        final double shooter_velocity = 2796;

        double direction = gamepad1.x ? -1.0 : 1.0;

        rf.set(0);
        rr.set(0);
        lf.set(0);
        lr.set(0);
        intake.set(0);
        transfer.set(0);
        shooter_left.set(0);
        shooter_right.set(0);

        if (gamepad1.dpad_up && gamepad1.dpad_right)   rf.set(direction * drivetrain_velocity);
        if (gamepad1.dpad_down && gamepad1.dpad_right) rr.set(direction * drivetrain_velocity);
        if (gamepad1.dpad_up && gamepad1.dpad_left)    lf.set(direction * drivetrain_velocity);
        if (gamepad1.dpad_down && gamepad1.dpad_left)  lr.set(direction * drivetrain_velocity);

        if (gamepad1.left_bumper)  intake.set(intake_velocity * direction);
        if (gamepad1.right_bumper) transfer.set(transfer_velocity * direction);

        if (gamepad1.left_trigger > 0.5)  shooter_left.set(direction * shooter_velocity);
        if (gamepad1.right_trigger > 0.5) shooter_right.set(direction * shooter_velocity);
        if (gamepad1.triangle){
        depollonator.setPosition(0);}
        telemetry.addData("Position depollen", positiona);
        telemetry.addData("Position block", positionb);
        telemetry.update();
    }
}
