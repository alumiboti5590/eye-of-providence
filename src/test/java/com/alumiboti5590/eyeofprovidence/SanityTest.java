package com.alumiboti5590.eyeofprovidence;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import edu.wpi.first.math.geometry.Rotation2d;

public class SanityTest {

    @Test
    public void testBasicMathAsserts() {
        // Simple assertion to verify the JUnit framework works
        assertTrue(true, "True should be true!");
        assertEquals(4, 2 + 2, "Basic arithmetic should work.");
    }

    @Test
    public void testWPILibDependencyLoads() {
        // Creates a real WPILib class instance to verify WPILib is successfully 
        // linked and accessible within your test environment.
        Rotation2d rotation = Rotation2d.fromDegrees(180);
        
        assertEquals(Math.PI, rotation.getRadians(), 0.001, 
            "WPILib Rotation2d should correctly convert 180 degrees to Pi radians.");
    }
}