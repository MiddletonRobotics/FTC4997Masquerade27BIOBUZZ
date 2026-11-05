package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.hardware.motors.Motor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Shooter extends SubsystemBase {
    private final DcMotorEx shooter_left;
    private final DcMotorEx shooter_right;
    private double tps;
    private double ticks_per_rev = 28;
    private double rpm = 100;
    private Telemetry telemetry;



    public Shooter(final HardwareMap hardwareMap, final String name, Telemetry telemetry) {
        this.telemetry = telemetry;
        shooter_left = hardwareMap.get(DcMotorEx.class, "shooterOne");
        shooter_right = hardwareMap.get(DcMotorEx.class, "shooterTwo");

        shooter_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shooter_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        shooter_left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooter_right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        shooter_left.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void shoot() {
        double currentVelocity = (shooter_left.getVelocity()*60)/ticks_per_rev;
        tps = (rpm * ticks_per_rev) /60;
        shooter_left.setVelocity(tps);
        shooter_right.setVelocity(tps);
        telemetry.addData("current rpm", currentVelocity );
        telemetry.addData("target tps", tps);
        telemetry.addData("actual tps", shooter_left.getVelocity());
        telemetry.addData("power", shooter_left.getPower());
        telemetry.update();
    }
    public void stop() {
        shooter_left.setVelocity(0);
        shooter_right.setVelocity(0);
    }

}