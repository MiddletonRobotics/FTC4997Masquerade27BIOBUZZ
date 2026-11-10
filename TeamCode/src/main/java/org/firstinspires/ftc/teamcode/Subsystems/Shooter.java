package org.firstinspires.ftc.teamcode.Subsystems;


import com.acmerobotics.dashboard.config.Config;
import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import org.firstinspires.ftc.robotcore.external.Telemetry;
@Config
@Configurable
public class Shooter extends SubsystemBase {
    private final MotorEx shooter_left;
    private final MotorEx shooter_right;
    private final Telemetry telemetry;


    public Shooter(final HardwareMap hMap, final String name, Telemetry telemetry) {
        shooter_left = new MotorEx(hMap, "shooterOne", Motor.GoBILDA.BARE);
        shooter_right = new MotorEx(hMap, "shooterTwo", Motor.GoBILDA.BARE);

        shooter_left.setInverted(true);

        shooter_left.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        shooter_right.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);

        this.telemetry = telemetry;
    }

    private static class Profile {
        final double rpm, kPL, kSL, kVL, kPR, kSR, kVR;
        Profile(double rpm, double kPL, double kSL, double kVL, double kPR, double kSR, double kVR) {
            this.rpm = rpm; this.kPL = kPL; this.kSL = kSL; this.kVL = kVL; this.kPR = kPR; this.kSR = kSR; this.kVR = kVR;
        }
    }

    public static Profile[] tuningValues = {
            new Profile(1150,  0.5, 0, 2, 0.5, 0, 2),
            new Profile(1250,  0.5, 0, 2, 0.5, 0, 2),
            new Profile(1350,  0.5, 0, 2, 0.5, 0, 2),

            new Profile(1400,  0.5, 0, 0.1, 0.5, 0, 0.1),
            new Profile(1500,  0.5, 0, 0.1, 0.5, 0, 0.1),
            new Profile(1600,  0.5, 0, 0.1, 0.5, 0, 0.1),
    };

    private double targetRpm = 0;


    public void shooterspeedup () {

    }
    public void setVelocity(double rpm) {
        Profile shootercoeff = null;
        for (Profile eachprofile : tuningValues) if (eachprofile.rpm == rpm) shootercoeff = eachprofile;

        shooter_left.setRunMode(Motor.RunMode.VelocityControl);
        shooter_right.setRunMode(Motor.RunMode.VelocityControl);
        shooter_left.setVeloCoefficients(shootercoeff.kPL, 0, 0);
        shooter_right.setVeloCoefficients(shootercoeff.kPR, 0, 0);
        shooter_left.setFeedforwardCoefficients(shootercoeff.kSL, shootercoeff.kVL);
        shooter_right.setFeedforwardCoefficients(shootercoeff.kSR, shootercoeff.kVR);
        targetRpm = rpm;
    }

    public void periodic() {
        if (targetRpm != 0) {
            double tps = targetRpm * shooter_right.getCPR() / 60.0;
            double currentrightrpm = shooter_right.getCorrectedVelocity()*60/shooter_right.getCPR();
            double currentleftrpm = shooter_left.getCorrectedVelocity()*60/shooter_left.getCPR();
            double currentpowerleft = shooter_left.get();
            double currentpowerright = shooter_right.get();

            shooter_left.setVelocity(tps);
            shooter_right.setVelocity(tps);
            telemetry.addData("current right rpm", currentrightrpm);
            telemetry.addData("current left rpm", currentleftrpm);
            telemetry.addData("current power left", currentpowerleft);
            telemetry.addData("current power right", currentpowerright);
            telemetry.addData("targetrpm", targetRpm);
            telemetry.update();
        }}


    public void stop() {
        targetRpm = 0;
        shooter_left.setRunMode(Motor.RunMode.RawPower);
        shooter_right.setRunMode(Motor.RunMode.RawPower);

        shooter_left.set(0);
        shooter_right.set(0);
    }

}