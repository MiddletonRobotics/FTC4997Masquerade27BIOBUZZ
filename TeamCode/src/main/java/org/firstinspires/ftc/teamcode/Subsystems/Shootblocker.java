package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.ServoImplEx;

public class Shootblocker {
    private final ServoImplEx shootblocker;

    private static final double open = 0;
    private static final double closed = 1.0;

    private boolean isClosed = false;

    public Shootblocker(final HardwareMap hardwareMap, final String name) {
        shootblocker = hardwareMap.get(ServoImplEx.class, "shootblocker");
        shootblocker.setPwmRange(new PwmControl.PwmRange(500, 2500));
        shootblocker.setPosition(open);
    }

    public void toggle() {
        isClosed = !isClosed;
        shootblocker.setPosition(isClosed ? closed : closed);
    }

    public boolean isDown() {
        return isClosed;
    }
}
