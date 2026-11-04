package org.firstinspires.ftc.teamcode.OpModes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;

import org.firstinspires.ftc.teamcode.Controls;
import org.firstinspires.ftc.teamcode.Subsystems.Masq;

@TeleOp(name = "Teleop")
public class Teleop  extends CommandOpMode {

    private Masq robot;
    private GamepadEx driver1;
    private GamepadEx driver2;

    @Override
    public void initialize() {
        robot = new Masq(hardwareMap, telemetry);

        driver1 = new GamepadEx(gamepad1);

        driver2 = new GamepadEx(gamepad2);

        Controls bindings = new Controls(driver1, driver2, robot);

        bindings.ControlMap();
        bindings.configureDefaultCommands();
    }
    @Override
    public void run(){
        super.run();
        telemetry.update();

    }
}
