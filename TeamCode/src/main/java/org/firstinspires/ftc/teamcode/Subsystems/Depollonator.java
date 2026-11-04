package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

public class Depollonator {
    private final ServoImplEx depollonator;

    private static final double up = 0;
    private static final double depollonate = 1.0;

    private boolean isDown = false;

    public Depollonator(final HardwareMap hardwareMap, final String name) {
        depollonator = hardwareMap.get(ServoImplEx.class, "depollonator");
        depollonator.setPwmRange(new PwmControl.PwmRange(500, 2500));
        depollonator.setPosition(up);
    }

    public void toggle() {
        isDown = !isDown;
        depollonator.setPosition(isDown ? depollonate : up);
    }

    public boolean isDown() {
        return isDown;
    }
}
