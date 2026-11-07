package org.firstinspires.ftc.teamcode.Commands;

import com.seattlesolvers.solverslib.command.CommandBase;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.Subsystems.Intake;

import java.util.function.BooleanSupplier;

public class intakecontroller extends CommandBase {
    private final Intake intake;
    private final GamepadEx driver1;
    private final GamepadEx driver2;
    private final BooleanSupplier intakeDriver2;

    public intakecontroller(Intake subsystem, GamepadEx driver1, GamepadEx driver2, BooleanSupplier intakeDriver2) {
        this.intake = subsystem;
        this.driver1 = driver1;
        this.driver2 = driver2;
        this.intakeDriver2 = intakeDriver2;

        addRequirements(intake);
    }

    @Override
    public void execute() {
        double intakePower;
        double transferPower;
        double rollers;

        if (!intakeDriver2.getAsBoolean()) {
            rollers = 0;
            if (driver1.getButton(GamepadKeys.Button.RIGHT_BUMPER)) {
                rollers = 1;
            } else if (driver1.getButton(GamepadKeys.Button.LEFT_BUMPER)) {
                rollers = -1;
            }
            intakePower = rollers;
            transferPower = rollers;
        } else {
            intakePower = 0;
            if (driver2.getButton(GamepadKeys.Button.DPAD_UP)) {
                intakePower = 1;
            } else if (driver2.getButton(GamepadKeys.Button.DPAD_DOWN)) {
                intakePower = -1;
            }
            transferPower = 0;
            if (driver2.getButton(GamepadKeys.Button.DPAD_LEFT)) {
                transferPower = 1;
            } else if (driver2.getButton(GamepadKeys.Button.DPAD_RIGHT)) {
                transferPower = -1;
            }
        }

        intake.setIntake(intakePower);
        intake.setTransfer(transferPower);
    }
}
