package org.firstinspires.ftc.teamcode.Subsystems;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.drivebase.MecanumDrive;
import com.seattlesolvers.solverslib.hardware.motors.Motor;

import org.firstinspires.ftc.teamcode.pedro.Constants;

public class Drivetrain extends SubsystemBase {
    private final Follower follower;

    public Drivetrain(final HardwareMap hardwareMap, final String name) {
        follower = Constants.create(hardwareMap);
    }

    public void driveFieldCentric(double forward, double strafe, double turn) {
        DrivePowers powers = ManualDrive.fieldCentric(
                forward,
                strafe,
                turn,
                follower.pose().heading()
        );

        follower.manual(powers);
    }

    public double getHeading() {
        return follower.pose().heading();
    }

    @Override
    public void periodic() {
        follower.update();
    }
}