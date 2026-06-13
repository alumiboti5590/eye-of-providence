package com.alumiboti5590.eyeofprovidence.robot.subsystems.vision.util;

import edu.wpi.first.math.geometry.Transform3d;

public class CameraConfig {

    public static enum CameraType {
        PHOTONVISION,
        LIMELIGHT;
    }

    private final String name;
    private final CameraType type;
    private final boolean enabled;
    private final Transform3d robotCenterToCamera;

    public CameraConfig(String name, CameraType type, Transform3d robotToCamera) {
        this(name, type, robotToCamera, true);
    }
    
    public CameraConfig(String name, CameraType type, Transform3d robotToCamera, boolean enabled) {
        this.name = name;
        this.type = type;
        this.robotCenterToCamera = robotToCamera;
        this.enabled = enabled;
    }

    public CameraType getType() { 
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public Transform3d getRobotCenterToCameraTransform() {
        return this.robotCenterToCamera;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}
