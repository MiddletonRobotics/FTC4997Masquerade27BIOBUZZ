package org.firstinspires.ftc.teamcode.OpModes.Autos;

import com.pedropathing.api.Paths;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

import org.firstinspires.ftc.teamcode.pedro.Constants;
    @Autonomous(name = "ParkLeft")
    public class ParkLeft extends OpMode {
        private MotorEx rf, rr, lf, lr;
        @Override
        public void init() {
            rf = new MotorEx(hardwareMap, "rf", Motor.GoBILDA.RPM_435);
            rr = new MotorEx(hardwareMap, "rr", Motor.GoBILDA.RPM_435);
            lf = new MotorEx(hardwareMap, "lf", Motor.GoBILDA.RPM_435);
            lr = new MotorEx(hardwareMap, "lr", Motor.GoBILDA.RPM_435);

            rf.setRunMode(Motor.RunMode.VelocityControl);
            rr.setRunMode(Motor.RunMode.VelocityControl);
            lf.setRunMode(Motor.RunMode.VelocityControl);
            lr.setRunMode(Motor.RunMode.VelocityControl);

            rf.setInverted(true);
            rr.setInverted(true);
            lf.setInverted(true);
            lr.setInverted(true);
        }

        @Override
        public void loop() {
            rf.setVelocity(1000);
            lf.setVelocity(-1000);
            rr.setVelocity(-1000);
            lr.setVelocity(1000);
        }

}
