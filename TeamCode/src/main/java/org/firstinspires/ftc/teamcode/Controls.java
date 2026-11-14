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
    private final Trigger shooterpollon;
    private final Trigger shooternectar;

   // private final Trigger intakeModeToggle;
    private final Trigger intake;
    private final Trigger outtake;
    private final Trigger transferin;
    private final Trigger transferout;

    private final Trigger depollonate;
    private final Trigger toggleshooter;
    private double shooter_rpm_pollon = 1250;
    private double shooter_rpm_nectar = 1500;

    private Trigger  shooterspeedup;
    private Trigger  shooterspeeddown;


    private boolean intakeDriver2 = false;


    public Controls(GamepadEx driver1, GamepadEx driver2, Masq robot) {
        this.driver1 = driver1;
        this.driver2 = driver2;
        this.robot = robot;

        shooterpollon = new Trigger(() -> driver2.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER)>0.5);
        shooternectar = new Trigger(() -> driver2.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER)>0.5);
       // intakeModeToggle = new Trigger(() -> driver1.getButton(GamepadKeys.Button.SQUARE));
        intake = new Trigger(() -> driver1.getButton(GamepadKeys.Button.RIGHT_BUMPER));
        outtake = new Trigger(() -> driver1.getButton(GamepadKeys.Button.LEFT_BUMPER));
        transferin = new Trigger(() -> driver2.getButton(GamepadKeys.Button.CROSS));
        transferout = new Trigger(() -> driver2.getButton(GamepadKeys.Button.TRIANGLE));
        depollonate = new Trigger(() -> driver2.getButton(GamepadKeys.Button.DPAD_LEFT));
        toggleshooter = new Trigger(() -> driver2.getButton(GamepadKeys.Button.DPAD_RIGHT));
        shooterspeedup = new Trigger(()-> driver2.getButton(GamepadKeys.Button.DPAD_UP));
        shooterspeeddown = new Trigger(()-> driver2.getButton(GamepadKeys.Button.DPAD_DOWN));
    }
    public void ControlMap() {

       // intakeModeToggle.whenActive(new InstantCommand(() -> intakeDriver2 = !intakeDriver2));
        depollonate.whenActive(new InstantCommand(() -> robot.depollonator.toggle()));
        toggleshooter.whenActive(new InstantCommand(() -> robot.shootblocker.toggle()));
        shooterspeedup.whenActive(new InstantCommand(()-> shooter_rpm_nectar = shooter_rpm_nectar + 100)
                .alongWith(new InstantCommand(()-> shooter_rpm_pollon = shooter_rpm_pollon + 100)));
        shooterspeeddown.whenActive(new InstantCommand(()-> shooter_rpm_pollon = shooter_rpm_pollon - 100)
                .alongWith(new InstantCommand(()->shooter_rpm_nectar = shooter_rpm_nectar - 100)));
        shooterpollon.whileActiveContinuous(new StartEndCommand(
                () -> robot.shooter.setVelocity(shooter_rpm_pollon),
                () -> robot.shooter.stop(),
                robot.shooter
        ));
        shooternectar.whileActiveContinuous(new StartEndCommand(
                () -> robot.shooter.setVelocity(shooter_rpm_nectar),
                () -> robot.shooter.stop(),
                robot.shooter
        ));
        intake.whileActiveContinuous(new StartEndCommand(
                ()-> robot.intake.setIntake(1),
                ()-> robot.intake.setIntake(0),
                robot.intake
        ));
        outtake.whileActiveContinuous(new StartEndCommand(
                ()-> robot.intake.setIntake(-1),
                ()-> robot.intake.setIntake(0),
                robot.intake
        ));
        transferin.whileActiveContinuous(new StartEndCommand(
                ()-> robot.intake.setTransfer(1),
                ()-> robot.intake.setTransfer(0),
                robot.intake
        ));
        transferout.whileActiveContinuous(new StartEndCommand(
                ()-> robot.intake.setTransfer(-1),
                ()-> robot.intake.setTransfer(0),
                robot.intake
        ));
    }
    public void configureDefaultCommands () {
        robot.drivetrain.setDefaultCommand(new drivetraincontroller(
                robot.drivetrain,
                () -> driver1.getLeftY(),
                () -> driver1.getRightX(),
                () -> driver1.getLeftX()
        ));
       // robot.intake.setDefaultCommand(new intakecontroller(
      //          robot.intake, driver1, driver2,
       //         () -> intakeDriver2
      //  ));

}
}



