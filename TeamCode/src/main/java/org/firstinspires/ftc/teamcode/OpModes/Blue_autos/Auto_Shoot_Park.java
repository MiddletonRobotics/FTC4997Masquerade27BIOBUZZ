package org.firstinspires.ftc.teamcode.OpModes.Blue_autos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

@Autonomous(name = "Auto Shoot Park Left", group = "Autonomous")
public class Auto_Shoot_Park extends OpMode {

    // Shooter / feed (RPM, same units as Shooter.setVelocity and Controls)
    private static final double TARGET_RPM = 1250;   // teleop "pollon" default
    private static final double SPINUP_MS = 5000;
    private static final double FEED_TIME_MS = 5000;

    // Shootblocker position the Shootblocker subsystem starts at in teleop
    private static final double SHOOTBLOCKER_OPEN = 0.8;

    // Drive
    private static final double DRIVE_POWER = 0.25;
    private static final double STRAFE_MS = 1000;   // tune
    private static final double FORWARD_MS = 1000;  // tune
    private static final int CYCLES = 3;            // strafe + forward repeats

    private enum State { SPIN_UP, FEED, STRAFE, FORWARD, DONE }

    private DcMotorEx leftFront, rightFront, leftBack, rightBack;
    private DcMotorEx intake, transfer;
    private MotorEx shooter_left, shooter_right;
    private Servo shootblocker;

    private final ElapsedTime timer = new ElapsedTime();
    private State state = State.SPIN_UP;
    private int cycle = 0;
    private boolean shooterOn = false;

    @Override
    public void init() {
        // Names and directions match pedro/Constants.java
        leftFront = hardwareMap.get(DcMotorEx.class, "lf");
        rightFront = hardwareMap.get(DcMotorEx.class, "rf");
        leftBack = hardwareMap.get(DcMotorEx.class, "lr");
        rightBack = hardwareMap.get(DcMotorEx.class, "rr");

        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightBack.setDirection(DcMotorSimple.Direction.FORWARD);

        for (DcMotorEx m : new DcMotorEx[]{leftFront, rightFront, leftBack, rightBack}) {
            m.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        // Intake subsystem inverts intake, not transfer
        intake = hardwareMap.get(DcMotorEx.class, "intake");
        transfer = hardwareMap.get(DcMotorEx.class, "transfer");
        intake.setDirection(DcMotorSimple.Direction.REVERSE);

        // Shooter subsystem inverts the left flywheel and uses BRAKE
        shooter_left = new MotorEx(hardwareMap, "shooterOne", Motor.GoBILDA.BARE);
        shooter_right = new MotorEx(hardwareMap, "shooterTwo", Motor.GoBILDA.BARE);
        shooter_left.setInverted(true);
        shooter_left.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        shooter_right.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);

        // Make sure the blocker isn't in the way of the shot
        shootblocker = hardwareMap.get(Servo.class, "shootblocker");
        shootblocker.setPosition(SHOOTBLOCKER_OPEN);
    }

    @Override
    public void start() {
        shooterStart();
        nextState(State.SPIN_UP);
    }

    @Override
    public void loop() {
        // SolversLib velocity control is software PID: it only corrects
        // when setVelocity is called, so call it every loop (like Shooter.periodic)
        if (shooterOn) shooterUpdate();

        switch (state) {
            case SPIN_UP:
                if (timer.milliseconds() >= SPINUP_MS) {
                    intake.setPower(1.0);
                    transfer.setPower(1.0);
                    nextState(State.FEED);
                }
                break;

            case FEED:
                if (timer.milliseconds() >= FEED_TIME_MS) {
                    intake.setPower(0);
                    transfer.setPower(0);
                    shooterStop();
                    strafeLeft();
                    nextState(State.STRAFE);
                }
                break;

            case STRAFE:
                    if (timer.milliseconds() >= STRAFE_MS) {
                driveForward();
                nextState(State.FORWARD);
            }
                break;

            case FORWARD:
                if (timer.milliseconds() >= FORWARD_MS) {
                    cycle++;
                    if (cycle >= CYCLES) {
                        stopAll();
                        nextState(State.DONE);
                    } else {
                        strafeLeft();
                        nextState(State.STRAFE);
                    }
                }
                break;

            case DONE:
                break;
        }

        telemetry.addData("State", state);
        telemetry.addData("Cycle", "%d / %d", cycle, CYCLES);
        telemetry.addData("Step time (ms)", "%.0f", timer.milliseconds());
        if (shooterOn) {
            telemetry.addData("Target RPM", TARGET_RPM);
            telemetry.addData("Right RPM", "%.0f",
                    shooter_right.getCorrectedVelocity() * 60.0 / shooter_right.getCPR());
            telemetry.addData("Left RPM", "%.0f",
                    shooter_left.getCorrectedVelocity() * 60.0 / shooter_left.getCPR());
        }
        telemetry.update();
    }

    @Override
    public void stop() {
        stopAll();
    }

    private void nextState(State s) {
        state = s;
        timer.reset();
    }

    private void shooterStart() {
        shooter_left.setRunMode(Motor.RunMode.VelocityControl);
        shooter_right.setRunMode(Motor.RunMode.VelocityControl);
        shooterOn = true;
        shooterUpdate();
    }

    // RPM -> ticks/sec, same conversion as Shooter.periodic()
    private void shooterUpdate() {
        double tps = TARGET_RPM * shooter_right.getCPR() / 60.0;
        shooter_left.setVelocity(tps);
        shooter_right.setVelocity(tps);
    }

    private void shooterStop() {
        shooterOn = false;
        shooter_left.setRunMode(Motor.RunMode.RawPower);
        shooter_right.setRunMode(Motor.RunMode.RawPower);
        shooter_left.set(0);
        shooter_right.set(0);
    }

    // LF back, RF forward, LB forward, RB back
    private void strafeLeft() {
        leftFront.setPower(-DRIVE_POWER);
        rightFront.setPower(DRIVE_POWER);
        leftBack.setPower(DRIVE_POWER);
        rightBack.setPower(-DRIVE_POWER);
    }

    private void driveForward() {
        leftFront.setPower(DRIVE_POWER);
        rightFront.setPower(DRIVE_POWER);
        leftBack.setPower(DRIVE_POWER);
        rightBack.setPower(DRIVE_POWER);
    }

    private void stopDrive() {
        leftFront.setPower(0);
        rightFront.setPower(0);
        leftBack.setPower(0);
        rightBack.setPower(0);
    }

    private void stopAll() {
        stopDrive();
        intake.setPower(0);
        transfer.setPower(0);
        shooterStop();
    }
}