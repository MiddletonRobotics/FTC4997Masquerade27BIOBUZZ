package org.firstinspires.ftc.teamcode.Subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
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
        final double rpm, kP, kS, kV;
        Profile(double rpm, double kP, double kS, double kV) {
            this.rpm = rpm; this.kP = kP; this.kS = kS; this.kV = kV;
        }
    }

  //  public static Profile[] profiles = {
   //         new Profile(1250,  0.5, 0, 2),
   //         new Profile(1500,  0.5, 0, 0.1)
  //  };

    private double targetRpm = 0;


    public void shooterspeedup () {

    }
    public void setVelocity(double rpm) {
//        Profile p = null;
  //      for (Profile c : profiles) if (c.rpm == rpm) p = c;
    //    if (p == null) p = profiles[0];

        shooter_left.setRunMode(Motor.RunMode.VelocityControl);
        shooter_right.setRunMode(Motor.RunMode.VelocityControl);
      //  shooter_left.setVeloCoefficients(p.kP, 0, 0);
    //    shooter_right.setVeloCoefficients(p.kP, 0, 0);
  //      shooter_left.setFeedforwardCoefficients(p.kS, p.kV);
//        shooter_right.setFeedforwardCoefficients(p.kS, p.kV);
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