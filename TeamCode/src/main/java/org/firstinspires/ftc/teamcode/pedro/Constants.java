package org.firstinspires.ftc.teamcode.pedro;

import com.acmerobotics.dashboard.config.Config;
import com.bylazar.ftcontrol.panels.configurables.annotations.Configurable;
import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
@Config
@Configurable
public class Constants {
    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set("lf");
        c.frontRightName.set("rf");
        c.backLeftName.set("lr");
        c.backRightName.set("rr");
        c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
        c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);
    });
    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD);
        c.xPodOffset.set(1.6136241522360975);
        c.yPodOffset.set(6.098711509404221);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });
        public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.670929834452962);
                Controller secondaryTranslationalForward = Controller.proportional(0.247890646255016);
                Controller primaryTranslationalLateral = Controller.proportional(1.236630183012217);
                Controller secondaryTranslationalLateral = Controller.proportional(0.4569018092559557);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.021968183355776296));
                c.brake.set(Controller.proportionalFeedforward(0.018672955852409853));

                c.headingFeedback.set(Controller.proportional(7.684027320603955));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.0630287956210774, 0.007120858346817649));

                c.linearBrakeCoefficients.set(Matrix.diag(0.1412713323581407, 0.1034485613034504));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.002032536912478538, 0.002223377126724499));

                c.maxAchievableForwardVelocity.set(46.29920029234863);
                c.maxAchievableStrafeVelocity.set(35.46132659863008);
                c.naturalForwardDeceleration.set(18.336489912732393);
                c.naturalStrafeDeceleration.set(46.216462207466115);
            }
    );

    public static Follower create(HardwareMap h) {
        return new Follower(
                new PinpointLocalizer(h, Constants.localizerConfig),
                new Mecanum(h, Constants.drivetrainConfig),
                new Foresight(Constants.foresightConfig)
        );
    }
}