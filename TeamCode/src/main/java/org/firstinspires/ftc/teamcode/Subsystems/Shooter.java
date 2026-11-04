package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.hardware.motors.Motor;

public class Shooter extends SubsystemBase {
    private final Motor ShootermotorOne;
    private final Motor ShootermotorTwo;



    public Shooter(final HardwareMap hardwareMap, final String name) {
        ShootermotorOne = new Motor(hardwareMap, "shooterOne", Motor.GoBILDA.RPM_435);
        ShootermotorTwo = new Motor(hardwareMap, "shooterTwo", Motor.GoBILDA.RPM_435);
        ShootermotorOne.setRunMode(Motor.RunMode.RawPower);
        ShootermotorTwo.setRunMode(Motor.RunMode.RawPower);
        //ShootermotorTwo.setInverted(true);
    }

    public void shoot(double power) {
        ShootermotorOne.set(power);
        ShootermotorTwo.set(power);
    }

}