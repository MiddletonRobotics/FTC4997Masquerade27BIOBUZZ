package org.firstinspires.ftc.teamcode.OpModes.Blue_autos;

import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.instant;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous(name = "ShootPreloadsandParkBlue")
public class CollectShootPark extends OpMode {
    /*
    private static final double TARGET_RPM = 1000;

    private static final double FEED_TIME_MS = 3000;
    private static final double SPINUP_TIMEOUT = 10000;

    private Follower follower;
    private MotorEx shooter_left;
    private MotorEx shooter_right;

    private DcMotorEx intake, transfer;

    private final PoseFactory p = PoseFactory.degrees();
    private final Pose start = poseFactory.of(132, 77.6131, 180);
    private final Pose point1 = poseFactory.of(130.1749, 121.0399, 90);
    private final Pose point1Control1 = poseFactory.of(121.1527, 90.7002, 0);
    private final Pose point2 = poseFactory.of(130.5247, 131.3554, 88.0577);
    private final Pose point3Start = poseFactory.of(130.5247, 131.3554, 0);
    private final Pose point3 = poseFactory.of(83.0693, 131.5416, 270);
    private final Pose point4 = poseFactory.of(128.5658, 51.7659, 109.8895);
    private final Pose point4Control1 = poseFactory.of(116.9045, 83.9984, 0);

    private Path startToShoot() {
        return line(startPose, shootPose).linear(startPose, shootPose);
    }

    private Path shootToPark() {
        return line(shootPose, parkPose).linear(shootPose, parkPose);
    }

    private void shooterSpinUp() {
        shooter_left.setRunMode(Motor.RunMode.VelocityControl);
        shooter_right.setRunMode(Motor.RunMode.VelocityControl);
        shooter_left.setVelocity(TARGET_RPM);
        shooter_right.setVelocity(TARGET_RPM);}

    private void shooterStop()   {
        shooter_left.setRunMode(Motor.RunMode.RawPower);
        shooter_right.setRunMode(Motor.RunMode.RawPower);
        shooter_left.set(0);
        shooter_right.set(0);}


    private Command autoRoutine() {
        return sequential(
                instant(this::shooterSpinUp),
                follow(follower, startToShoot()),

                waitMs(SPINUP_TIMEOUT),

                instant(() -> { intake.setPower(1.0); transfer.setPower(1.0); }),
                waitMs(FEED_TIME_MS),
                instant(() -> { intake.setPower(0); transfer.setPower(0); }),
                instant(this::shooterStop),

                follow(follower, shootToPark())
        );
    }

    @Override
    public void init() {
        Scheduler.reset();

        intake = hardwareMap.get(DcMotorEx.class, "intake");
        transfer = hardwareMap.get(DcMotorEx.class, "transfer");
        shooter_left = new MotorEx(hardwareMap, "shooterOne", Motor.GoBILDA.BARE);
        shooter_right = new MotorEx(hardwareMap, "shooterOne", Motor.GoBILDA.BARE);


        follower = Constants.create(hardwareMap);
        follower.setPose(startPose);
        follower.update();
    }

    @Override
    public void start() {
        schedule(autoRoutine());
    }

    @Override
    public void loop() {
        follower.update();
        Scheduler.execute();
        telemetry.addData("X", follower.pose().x());
        telemetry.addData("Y", follower.pose().y());
        telemetry.addData("Heading", Math.toDegrees(follower.pose().heading()));
        telemetry.update();
    }
     */
}