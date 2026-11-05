package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Masq {
    public final Drivetrain drivetrain;
    public final Intake intake;
    public final Shooter shooter;
    public final Depollonator depollonator;
    public final Shootblocker shootblocker;


    public Masq(HardwareMap hardwareMap, Telemetry telemetry) {
        drivetrain = new Drivetrain(hardwareMap, "Drivetrain");
        intake = new Intake(hardwareMap, "Intake");
        shooter = new Shooter(hardwareMap, "Shooter", telemetry);
        depollonator = new Depollonator(hardwareMap, "depollonator");
        shootblocker = new Shootblocker(hardwareMap, "shootblocker");

    }


}
