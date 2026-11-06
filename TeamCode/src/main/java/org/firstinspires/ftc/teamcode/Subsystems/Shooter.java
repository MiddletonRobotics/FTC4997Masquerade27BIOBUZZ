package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Shooter extends SubsystemBase {
    private final MotorEx shooter_left;
    private final MotorEx shooter_right;

    private final Telemetry telemetry;

    // Default starting constants for velocity control (tune later as needed)
    private static final double DEFAULT_KP = 0.0;
    private static final double DEFAULT_KI = 0.0;
    private static final double DEFAULT_KD = 0.0;
    private static final double DEFAULT_KS = 0.0;
    private static final double DEFAULT_KV = 0.001;

    public Shooter(final HardwareMap hMap, final String name, Telemetry telemetry) {
        shooter_left = new MotorEx(hMap, "shooterOne", Motor.GoBILDA.BARE);
        shooter_right = new MotorEx(hMap, "shooterTwo", Motor.GoBILDA.BARE);

        shooter_left.setInverted(true);

        shooter_left.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        shooter_right.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);



  //      setVeloCoefficients(DEFAULT_KP, DEFAULT_KI, DEFAULT_KD);
    //    setFeedforwardCoefficients(DEFAULT_KS, DEFAULT_KV);

        this.telemetry = telemetry;
    }

    public void periodic() {
        double ticksPerSecond = shooter_right.getCorrectedVelocity();
        double currentrpm = (ticksPerSecond / shooter_right.getCPR()) * 60;
        telemetry.addData("Shooter RPM", currentrpm);

    }

    public void setVelocity(double rpm) {
        shooter_left.setRunMode(Motor.RunMode.VelocityControl);
        shooter_right.setRunMode(Motor.RunMode.VelocityControl);

        double ticksPerSec = (rpm * shooter_right.getCPR()) / 60.0;
        shooter_left.setVelocity(ticksPerSec);
        shooter_right.setVelocity(ticksPerSec);

    }

    public void stop() {
        shooter_left.setRunMode(Motor.RunMode.RawPower);
        shooter_right.setRunMode(Motor.RunMode.RawPower);

        shooter_left.set(0);
        shooter_right.set(0);
    }
/*
    public void setVeloCoefficients(double kp, double ki, double kd) {
        shooter_left.setVeloCoefficients(kp, ki, kd);
        shooter_right.setVeloCoefficients(kp, ki, kd);
    }

    public void setFeedforwardCoefficients(double ks, double kv) {
        shooter_left.setFeedforwardCoefficients(ks, kv);
        shooter_right.setFeedforwardCoefficients(ks, kv);
    }*/
}