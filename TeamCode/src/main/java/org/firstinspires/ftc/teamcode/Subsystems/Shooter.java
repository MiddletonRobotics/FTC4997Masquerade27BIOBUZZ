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
@Config
@Configurable
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

/*
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

    @Config @Configurable public static class Profile1150 { public static double kPL = 0.5, kSL = 0, kVL = 2, kPR = 0.5, kSR = 0, kVR = 2; }
    @Config @Configurable public static class Profile1250 { public static double kPL = 0.5, kSL = 0, kVL = 2, kPR = 0.5, kSR = 0, kVR = 2; }
    @Config @Configurable public static class Profile1350 { public static double kPL = 0.5, kSL = 0, kVL = 2, kPR = 0.5, kSR = 0, kVR = 2; }
    @Config @Configurable public static class Profile1400 { public static double kPL = 0.5, kSL = 0, kVL = 0.1, kPR = 0.5, kSR = 0, kVR = 0.1; }
    @Config @Configurable public static class Profile1500 { public static double kPL = 0.5, kSL = 0, kVL = 0.1, kPR = 0.5, kSR = 0, kVR = 0.1; }
    @Config @Configurable public static class Profile1600 { public static double kPL = 0.5, kSL = 0, kVL = 0.1, kPR = 0.5, kSR = 0, kVR = 0.1; }

    private double targetRpm = 0;
    private double currentKPL = 0.5, currentKSL = 0, currentKVL = 2;
    private double currentKPR = 0.5, currentKSR = 0, currentKVR = 2;

    public Shooter(final HardwareMap hMap, final String name, Telemetry telemetry) {
        shooter_left = new MotorEx(hMap, "shooterOne", Motor.GoBILDA.BARE);
        shooter_right = new MotorEx(hMap, "shooterTwo", Motor.GoBILDA.BARE);

        shooter_left.setInverted(true);

        shooter_left.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        shooter_right.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);

        this.telemetry = telemetry;
    }

    public void shooterspeedup() {
    }

    public void setVelocity(double rpm) {
        targetRpm = rpm;
        switch ((int) rpm) {
            case 1150:
                currentKPL = Profile1150.kPL; currentKSL = Profile1150.kSL; currentKVL = Profile1150.kVL;
                currentKPR = Profile1150.kPR; currentKSR = Profile1150.kSR; currentKVR = Profile1150.kVR;
                break;
            case 1250:
                currentKPL = Profile1250.kPL; currentKSL = Profile1250.kSL; currentKVL = Profile1250.kVL;
                currentKPR = Profile1250.kPR; currentKSR = Profile1250.kSR; currentKVR = Profile1250.kVR;
                break;
            case 1350:
                currentKPL = Profile1350.kPL; currentKSL = Profile1350.kSL; currentKVL = Profile1350.kVL;
                currentKPR = Profile1350.kPR; currentKSR = Profile1350.kSR; currentKVR = Profile1350.kVR;
                break;
            case 1400:
                currentKPL = Profile1400.kPL; currentKSL = Profile1400.kSL; currentKVL = Profile1400.kVL;
                currentKPR = Profile1400.kPR; currentKSR = Profile1400.kSR; currentKVR = Profile1400.kVR;
                break;
            case 1500:
                currentKPL = Profile1500.kPL; currentKSL = Profile1500.kSL; currentKVL = Profile1500.kVL;
                currentKPR = Profile1500.kPR; currentKSR = Profile1500.kSR; currentKVR = Profile1500.kVR;
                break;
            case 1600:
                currentKPL = Profile1600.kPL; currentKSL = Profile1600.kSL; currentKVL = Profile1600.kVL;
                currentKPR = Profile1600.kPR; currentKSR = Profile1600.kSR; currentKVR = Profile1600.kVR;
                break;
            default:
                currentKPL = Profile1150.kPL; currentKSL = Profile1150.kSL; currentKVL = Profile1150.kVL;
                currentKPR = Profile1150.kPR; currentKSR = Profile1150.kSR; currentKVR = Profile1150.kVR;
                break;
        }
    }

    public void periodic() {
        if (targetRpm != 0) {
            double tps = targetRpm * shooter_right.getCPR() / 60.0;
            double currentrightrpm = shooter_right.getCorrectedVelocity() * 60 / shooter_right.getCPR();
            double currentleftrpm = shooter_left.getCorrectedVelocity() * 60 / shooter_left.getCPR();
            double currentpowerleft = shooter_left.get();
            double currentpowerright = shooter_right.get();

            shooter_left.setRunMode(Motor.RunMode.VelocityControl);
            shooter_right.setRunMode(Motor.RunMode.VelocityControl);
            shooter_left.setVeloCoefficients(currentKPL, 0, 0);
            shooter_right.setVeloCoefficients(currentKPR, 0, 0);
            shooter_left.setFeedforwardCoefficients(currentKSL, currentKVL);
            shooter_right.setFeedforwardCoefficients(currentKSR, currentKVR);

            shooter_left.setVelocity(tps);
            shooter_right.setVelocity(tps);

            telemetry.addData("current right rpm", currentrightrpm);
            telemetry.addData("current left rpm", currentleftrpm);
            telemetry.addData("current power left", currentpowerleft);
            telemetry.addData("current power right", currentpowerright);
            telemetry.addData("targetrpm", targetRpm);
            telemetry.update();
        }
    }

    public void stop() {
        targetRpm = 0;
        shooter_left.setRunMode(Motor.RunMode.RawPower);
        shooter_right.setRunMode(Motor.RunMode.RawPower);

        shooter_left.set(0);
        shooter_right.set(0);
    }
}

 */