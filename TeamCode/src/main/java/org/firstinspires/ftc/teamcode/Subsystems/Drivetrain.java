package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.drivebase.MecanumDrive;
import com.seattlesolvers.solverslib.hardware.motors.Motor;

public class Drivetrain extends SubsystemBase {
    public final Motor rf;
    public final Motor rr;
    public final Motor lf;
    public final Motor lr;
    private final MecanumDrive driveBase;

    public Drivetrain(final HardwareMap hardwareMap, final String name) {
        rf = new Motor(hardwareMap, "rf", Motor.GoBILDA.RPM_435);
        rr = new Motor(hardwareMap, "rr", Motor.GoBILDA.RPM_435);
        lf = new Motor(hardwareMap, "lf", Motor.GoBILDA.RPM_435);
        lr = new Motor(hardwareMap, "lr", Motor.GoBILDA.RPM_435);

        rf.setInverted(true);
        rr.setInverted(true);
        lf.setInverted(true);
        lr.setInverted(true);

        driveBase = new MecanumDrive(lf, rf, lr, rr);

    }


    public void drive(double strafe, double forward, double rotation) {
        driveBase.driveRobotCentric(strafe, forward, rotation);

    }
}