package org.firstinspires.ftc.teamcode.pedro;

import com.acmerobotics.dashboard.config.Config;
import com.bylazar.configurables.annotations.Configurable;
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
        c.xPodOffset.set(8.120455403966227);
        c.yPodOffset.set(-4.246463925819698);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });

    // ------------------------------------------------------------------
    // Foresight values, exposed as plain static fields so Panels / Dashboard
    // can edit them. They are the autotune results; ForesightLiveTuner reads
    // them every time you start a test.
    // Once you're happy, copy the final numbers back here as the defaults.
    // ------------------------------------------------------------------

    // Translational kP (power per inch of error). "Primary" is used when error is
    // larger than the switch distance, "secondary" when closer than it.
    public static double TRANSLATIONAL_SWITCH_INCHES = 2.5;
    public static double FWD_KP_PRIMARY = 0.670929834452962;
    public static double FWD_KP_SECONDARY = 0.247890646255016;
    public static double STRAFE_KP_PRIMARY = 1.236630183012217;
    public static double STRAFE_KP_SECONDARY = 0.4569018092559557;

    // Feedforward
    public static double COAST_KV = 0.021968183355776296;
    public static double BRAKE_KV = 0.018672955852409853;

    // Heading
    public static double HEADING_KP = 7.684027320603955;
    public static double HEADING_BRAKE_LINEAR = 0.0630287956210774;
    public static double HEADING_BRAKE_QUADRATIC = 0.007120858346817649;

    // Braking model: predicted braking distance = linear * v + quadratic * v^2
    public static double FWD_BRAKE_LINEAR = 0.1412713323581407;
    public static double STRAFE_BRAKE_LINEAR = 0.1034485613034504;
    public static double FWD_BRAKE_QUADRATIC = 0.002032536912478538;
    public static double STRAFE_BRAKE_QUADRATIC = 0.002223377126724499;

    // Drivetrain limits (inches/sec, inches/sec^2)
    public static double MAX_FWD_VELOCITY = 46.29920029234863;
    public static double MAX_STRAFE_VELOCITY = 35.46132659863008;
    public static double NATURAL_FWD_DECEL = 18.336489912732393;
    public static double NATURAL_STRAFE_DECEL = 46.216462207466115;

    /** Builds a fresh ForesightConfig from the current values of the fields above. */
    public static ForesightConfig buildForesightConfig() {
        return new ForesightConfig(c -> {
            Controller primaryForward = Controller.proportional(FWD_KP_PRIMARY);
            Controller secondaryForward = Controller.proportional(FWD_KP_SECONDARY);
            Controller primaryLateral = Controller.proportional(STRAFE_KP_PRIMARY);
            Controller secondaryLateral = Controller.proportional(STRAFE_KP_SECONDARY);

            c.forwardTranslational.set(Controller.piecewise(secondaryForward).put(TRANSLATIONAL_SWITCH_INCHES, primaryForward));
            c.strafeTranslational.set(Controller.piecewise(secondaryLateral).put(TRANSLATIONAL_SWITCH_INCHES, primaryLateral));

            c.coast.set(Controller.proportionalFeedforward(COAST_KV));
            c.brake.set(Controller.proportionalFeedforward(BRAKE_KV));

            c.headingFeedback.set(Controller.proportional(HEADING_KP));
            c.headingBrakeCoefficients.set(Vector2D.cartesian(HEADING_BRAKE_LINEAR, HEADING_BRAKE_QUADRATIC));

            c.linearBrakeCoefficients.set(Matrix.diag(FWD_BRAKE_LINEAR, STRAFE_BRAKE_LINEAR));
            c.quadraticBrakeCoefficients.set(Matrix.diag(FWD_BRAKE_QUADRATIC, STRAFE_BRAKE_QUADRATIC));

            c.maxAchievableForwardVelocity.set(MAX_FWD_VELOCITY);
            c.maxAchievableStrafeVelocity.set(MAX_STRAFE_VELOCITY);
            c.naturalForwardDeceleration.set(NATURAL_FWD_DECEL);
            c.naturalStrafeDeceleration.set(NATURAL_STRAFE_DECEL);
        });
    }

    public static Follower create(HardwareMap h) {
        return new Follower(
                new PinpointLocalizer(h, Constants.localizerConfig),
                new Mecanum(h, Constants.drivetrainConfig),
                new Foresight(buildForesightConfig())
        );
    }
}