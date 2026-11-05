package org.firstinspires.ftc.teamcode;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.RunCommand;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.StartEndCommand;
import com.seattlesolvers.solverslib.command.button.Button;

import com.seattlesolvers.solverslib.command.button.GamepadButton;
import com.seattlesolvers.solverslib.command.button.Trigger;

import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.Commands.drivetraincontroller;
import org.firstinspires.ftc.teamcode.Commands.intakecontroller;
import org.firstinspires.ftc.teamcode.Subsystems.Masq;

/*
Wiring:

Control Hub:
Motors:
0: lf
1: rf
2: lr
3: rr

Servos:
5: depollonator
0: shootblocker

Expansion Hub:
Motors:
0: intake
1: transfer
2: shooterOne
3: shooterTwo

 */



public class Controls {
    private final GamepadEx driver1;
    private final GamepadEx driver2;
    private final Masq robot;
    private final Trigger shooter;
    private final Trigger intakeModeToggle;
    private final Trigger depollonate;
    private final Trigger toggleshooter;
    private final double shooter_rpm = 1500;

    private boolean intakeDriver2 = false;


    public Controls(GamepadEx driver1, GamepadEx driver2, Masq robot) {
        this.driver1 = driver1;
        this.driver2 = driver2;
        this.robot = robot;

        shooter = new Trigger(() -> driver1.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER)>0.5);
        intakeModeToggle = new Trigger(() -> driver1.getButton(GamepadKeys.Button.SQUARE));
        depollonate = new Trigger(() -> driver2.getButton(GamepadKeys.Button.TRIANGLE));
        toggleshooter = new Trigger(() -> driver2.getButton(GamepadKeys.Button.CIRCLE));

    }
    public void ControlMap() {

        intakeModeToggle.whenActive(new InstantCommand(() -> intakeDriver2 = !intakeDriver2));
        depollonate.whenActive(new InstantCommand(() -> robot.depollonator.toggle()));
        toggleshooter.whenActive(new InstantCommand(() -> robot.shootblocker.toggle()));
        shooter.whileActiveContinuous(new StartEndCommand(
                () -> robot.shooter.setVelocity(shooter_rpm),
                () -> robot.shooter.stop(),
                robot.shooter
        ));
    }
    public void configureDefaultCommands () {
        robot.drivetrain.setDefaultCommand(new drivetraincontroller(
                robot.drivetrain,
                () -> driver1.getLeftY(),
                () -> driver1.getRightX(),
                () -> driver1.getLeftX()
        ));
        robot.intake.setDefaultCommand(new intakecontroller(
                robot.intake, driver1, driver2,
                () -> intakeDriver2
        ));

}
}



