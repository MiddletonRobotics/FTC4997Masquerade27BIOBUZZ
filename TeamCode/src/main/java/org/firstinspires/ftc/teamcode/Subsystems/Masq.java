package org.firstinspires.ftc.teamcode.Subsystems;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Masq {
    public final Drivetrain drivetrain;
    public final Intake intake;
    public final Shooter shooter;
    public final Depollonator depollonator;
    public final Shootblocker shootblocker;


    public Masq(HardwareMap hardwareMap, Telemetry telemetry) {
        Telemetry telem = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        drivetrain = new Drivetrain(hardwareMap, "Drivetrain");
        intake = new Intake(hardwareMap, "Intake");
        depollonator = new Depollonator(hardwareMap, "depollonator");
        shootblocker = new Shootblocker(hardwareMap, "shootblocker");
        shooter = new Shooter(hardwareMap, "shooter", telem);


    }


}
