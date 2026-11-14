package org.firstinspires.ftc.teamcode.Subsystems;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad2;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.hardware.motors.Motor;

public class Intake extends SubsystemBase {
    private final Motor intake;
    private final Motor transfer;

    public Intake(final HardwareMap hardwareMap, final String name) {
        intake = new Motor(hardwareMap, "intake", Motor.GoBILDA.RPM_312);
        transfer = new Motor(hardwareMap, "transfer", Motor.GoBILDA.RPM_312);


        intake.setRunMode(Motor.RunMode.RawPower);
        transfer.setRunMode(Motor.RunMode.RawPower);
    }

    public void setIntake(double power)   {
        intake.set(power);
    }
    public void setTransfer(double power) {
        transfer.set(power);
    }
}