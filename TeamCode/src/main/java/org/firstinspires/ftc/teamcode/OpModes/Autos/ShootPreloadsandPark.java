package org.firstinspires.ftc.teamcode.OpModes.Autos;

import static com.pedropathing.api.Paths.line;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;

import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.Masq;
import org.firstinspires.ftc.teamcode.Subsystems.Shooter;
import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous(name = "ShootPreloadsandPark")
public class ShootPreloadsandPark extends CommandOpMode {
    private Follower follower;
    private final Masq robot = new Masq(hardwareMap, telemetry);


    @Override
    public void initialize() {
        follower = Constants.create(hardwareMap);
        follower.setPose(Pose.zero());

        Path leftaftershot = line(Pose.zero(), new Pose(0, 24, 0)).constant(0);
        Path park = line(new Pose(0, 24, 0), new Pose(101, 24, 0)).constant(0);

        schedule(new SequentialCommandGroup(
                new InstantCommand(()-> robot.shooter.setVelocity(1200)),
                new InstantCommand(()->robot.intake.setTransfer(-1)),
                new WaitCommand(5000),

                new InstantCommand(()->robot.intake.setIntake(1)),
                new InstantCommand(()->robot.intake.setTransfer(1)),
                new WaitCommand(7000),

                new InstantCommand(()-> robot.shooter.stop()),
                new InstantCommand(()->robot.intake.setIntake(0)),
                new InstantCommand(()->robot.intake.setTransfer(0)),

                new FollowPathCommand(follower, leftaftershot),
                new FollowPathCommand(follower, park)
        ));
    }

    @Override
    public void run() {
        super.run();
        follower.update();
    }
}