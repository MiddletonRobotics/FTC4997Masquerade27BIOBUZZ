package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.hardware.motors.Motor;

public class Shooter extends SubsystemBase {
    private final DcMotorEx ShootermotorOne;
    private final DcMotorEx ShootermotorTwo;



    public Shooter(final HardwareMap hardwareMap, final String name) {
        ShootermotorOne = hardwareMap.get(DcMotorEx.class, "shooterOne");
        ShootermotorTwo = hardwareMap.get(DcMotorEx.class, "shooterTwo");

        ShootermotorOne.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        ShootermotorTwo.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        ShootermotorOne.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ShootermotorTwo.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //ShootermotorTwo.setInverted(true);
    }

    public void shoot(double tps) {
        ShootermotorOne.setVelocity(tps);
        ShootermotorTwo.setVelocity(tps);
    }

}