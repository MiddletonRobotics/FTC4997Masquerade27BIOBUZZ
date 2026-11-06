package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

public class Shootblocker {
    private final Servo shootblocker;

    private static final double open = 0.8;
    private static final double closed = 0.95;

    private boolean isClosed = false;

    public Shootblocker(final HardwareMap hardwareMap, final String name) {
        shootblocker = hardwareMap.get(Servo.class, "shootblocker");
        shootblocker.setPosition(open);
    }

    public void toggle() {
        isClosed = !isClosed;
        shootblocker.setPosition(isClosed ? open : closed);
    }

    public boolean isDown() {
        return isClosed;
    }
}
