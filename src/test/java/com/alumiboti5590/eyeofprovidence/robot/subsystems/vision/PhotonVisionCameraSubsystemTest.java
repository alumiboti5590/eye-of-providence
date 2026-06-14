package com.alumiboti5590.eyeofprovidence.robot.subsystems.vision;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;
import org.photonvision.targeting.TargetCorner;

import com.alumiboti5590.eyeofprovidence.robot.subsystems.vision.util.GeneralPoseEstimate;

@ExtendWith(MockitoExtension.class) // Automatically initializes fields annotated with @Mock
class PhotonVisionCameraSubsystemTest {

    @Mock
    private PhotonCamera mockCamera;

    @Mock
    private PhotonPoseEstimator mockEstimator;

    private PhotonVisionCameraSubsystem visionSubsystem;

    @BeforeEach
    void setUp() {
        // Since the camera's name is requested during construction (super call),
        // we stub it right before instantiating the subsystem.
        when(mockCamera.getName()).thenReturn("TestCam");

        // Inject the mocked dependencies directly into the constructor
        visionSubsystem = new PhotonVisionCameraSubsystem(mockCamera, mockEstimator);
    }

    // =========================================================================
    // Core Subsystem & Initialization Tests
    // =========================================================================

    @Test
    void testCameraNameGetter() {
        // Assert
        assertEquals("TestCam", visionSubsystem.getCameraName());
    }

    @Test
    void testPeriodicDrainsAndCachesQueue() {
        // Arrange: Simulate 2 frames waiting in the network queue
        PhotonPipelineResult frame1 = mock(PhotonPipelineResult.class);
        PhotonPipelineResult frame2 = mock(PhotonPipelineResult.class);
        List<PhotonPipelineResult> simulatedQueue = List.of(frame1, frame2);

        when(mockCamera.getAllUnreadResults()).thenReturn(simulatedQueue);

        // Act: Run the subsystem's periodic loop phase
        visionSubsystem.periodic();

        // Assert: Ensure the hardware queue was drained exactly once
        verify(mockCamera, times(1)).getAllUnreadResults();

        // Assert: Verify the subsystem isolated the absolute latest frame (frame2)
        assertEquals(frame2, visionSubsystem.getPipelineResult());
    }

    // =========================================================================
    // Pose Estimation Tests
    // =========================================================================

    @Test
    void testGetEstimatedGlobalPoseProcessesCache() {
        // Arrange: Populate the cache by feeding periodic() an unread frame
        PhotonPipelineResult frame = mock(PhotonPipelineResult.class);
        List<PhotonTrackedTarget> targets = new ArrayList<>();
        when(frame.getTargets()).thenReturn(targets);
        when(mockCamera.getAllUnreadResults()).thenReturn(List.of(frame));

        // Mock the estimator behavior
        EstimatedRobotPose dummyPose = mock(EstimatedRobotPose.class);
        when(mockEstimator.estimateCoprocMultiTagPose(frame)).thenReturn(Optional.of(dummyPose));

        // Act: Step through the subsystem lifecycle
        visionSubsystem.periodic(); // Drains and stores the frame in m_unreadResults
        Optional<EstimatedRobotPose> outputPose = visionSubsystem.getEstimatedGlobalPose();

        // Assert: Verify the estimation pipeline successfully evaluated our cached frame
        assertTrue(outputPose.isPresent());
        assertEquals(dummyPose, outputPose.get());
        verify(mockEstimator, times(1)).estimateCoprocMultiTagPose(frame);
    }

    @Test
    void testGetPipelineTargetsReturnsEmptyWhenNoData() {
        // Act & Assert
        // Before periodic runs or if no targets are found, it should safely return an empty list rather than null
        assertNotNull(visionSubsystem.getPipelineTargets());
        assertTrue(visionSubsystem.getPipelineTargets().isEmpty());
    }

    // =========================================================================
    // Periodic Loop & Frame Cache Tests
    // =========================================================================

    @Test
    void testPeriodicCachesAllFramesAndIsolatesLatest() {
        // Arrange: Create a sequence of 3 distinct mock frames
        PhotonPipelineResult frame1 = mock(PhotonPipelineResult.class);
        PhotonPipelineResult frame2 = mock(PhotonPipelineResult.class);
        PhotonPipelineResult frame3 = mock(PhotonPipelineResult.class);
        List<PhotonPipelineResult> mockNetworkQueue = List.of(frame1, frame2, frame3);

        // Stub the camera to return the full multi-frame queue
        when(mockCamera.getAllUnreadResults()).thenReturn(mockNetworkQueue);

        // Act: Run the subsystem's periodic loop phase
        visionSubsystem.periodic();

        // Assert: Verify that getPipelineResult() isolated the absolute latest frame (index 2)
        assertEquals(frame3, visionSubsystem.getPipelineResult(),
                "The pipelineResult should always cache the absolute newest frame available in the queue.");

        // Assert: Verify that m_unreadResults retains ALL frames.
        // Since m_unreadResults is private, we verify it behaviorally by calling getEstimatedGlobalPose()
        // and asserting that the estimator processed every single item that was cached.
        visionSubsystem.getEstimatedGlobalPose();

        verify(mockEstimator, times(1)).estimateCoprocMultiTagPose(frame1);
        verify(mockEstimator, times(1)).estimateCoprocMultiTagPose(frame2);
        verify(mockEstimator, times(1)).estimateCoprocMultiTagPose(frame3);
    }

    @Test
    void testGetGeneralPoseEstimateReturnsCorrectData() {
        // Arrange: Define test values using real geometry objects (safe now that EJML is fixed)
        Pose2d expected2dPose = new Pose2d(3.5, 2.0,
                new Rotation2d(Math.toRadians(90)));
        Pose3d test3dPose = new Pose3d(expected2dPose);
        double expectedTimestamp = 44.15;

        // Instantiate a real EstimatedRobotPose to bypass mock field limits
        EstimatedRobotPose expectedPoseData = new EstimatedRobotPose(test3dPose, expectedTimestamp, List.of());

        PhotonPipelineResult mockFrame = mock(PhotonPipelineResult.class);
        when(mockCamera.getAllUnreadResults()).thenReturn(List.of(mockFrame));
        when(mockEstimator.estimateCoprocMultiTagPose(mockFrame)).thenReturn(Optional.of(expectedPoseData));

        // Act: Process the frames into the local cache and query the interface projection
        visionSubsystem.periodic();
        Optional<GeneralPoseEstimate> outputOpt = visionSubsystem.getGeneralPoseEstimate();

        // Assert: Verify the optional is present and the mapped packet parameters match perfectly
        assertTrue(outputOpt.isPresent(), "GeneralPoseEstimate should be populated when a valid pose is tracked.");

        GeneralPoseEstimate finalizedEstimate = outputOpt.get();
        assertEquals(expected2dPose, finalizedEstimate.pose2d,
                "The 3D coordinates must project accurately to a 2D pose.");
        assertEquals(expectedTimestamp, finalizedEstimate.timestampSeconds,
                "Timestamps must align for odometry latency correction.");
        assertNotNull(finalizedEstimate.measurementStdDevs, "Standard deviation matrix must be computed and returned.");
    }

    @Test
    void testGetGeneralPoseEstimateReturnsEmptyWhenTrackingLost() {
        // Arrange: Simulate an empty vision loop frame sequence (no tags tracked)
        when(mockCamera.getAllUnreadResults()).thenReturn(List.of());

        // Act
        visionSubsystem.periodic();
        Optional<GeneralPoseEstimate> outputOpt = visionSubsystem.getGeneralPoseEstimate();

        // Assert: Verify that tracking losses gracefully bubble up as empty Optionals rather than throwing errors
        assertTrue(outputOpt.isEmpty(), "GeneralPoseEstimate must return empty if tracking data is absent.");
    }

    // =========================================================================
    // Target Extraction Utility Tests
    // =========================================================================

    @Test
    void testGetPipelineTargets() {
        // Arrange
        var targets = setupMockTargets(5, 7);

        // Act
        List<PhotonTrackedTarget> activeTargets = visionSubsystem.getPipelineTargets();

        // Assert
        assertEquals(2, activeTargets.size());
        assertTrue(activeTargets.contains(targets.get(0)));
        assertTrue(activeTargets.contains(targets.get(1)));
    }

    @Test
    void testGetPipelineTargetFiducialIds() {
        // Arrange
        setupMockTargets(5, 7);

        // Act
        long[] activeIds = visionSubsystem.getPipelineTargetFiducialIds();

        // Assert
        assertEquals(2, activeIds.length);
        assertEquals(5, activeIds[0]);
        assertEquals(7, activeIds[1]);
    }

    @Test
    void testGetPipelineTargetById() {
        // Arrange
        setupMockTargets(5, 7);

        // Act & Assert: Found
        PhotonTrackedTarget foundTarget = visionSubsystem.getPipelineTarget(5);
        assertNotNull(foundTarget);
        assertEquals(5, foundTarget.getFiducialId());

        // Act & Assert: Not Found
        PhotonTrackedTarget missingTarget = visionSubsystem.getPipelineTarget(99);
        assertNull(missingTarget);
    }

    // =========================================================================
    // Test Helper Methods
    // =========================================================================

    /**
     * Helper method to dynamically generate and seed target lists into the 
     * mock camera pipeline, streamlining targeting tests without modifying the setup defaults.
     */
    private List<PhotonTrackedTarget> setupMockTargets(int... ids) {
        List<PhotonTrackedTarget> generatedMocks = new ArrayList<>();
        
        for (int id : ids) {
            // Create exactly 4 dummy corners to satisfy the validation requirement
            List<TargetCorner> dummyCorners = List.of(
                new TargetCorner(0.0, 0.0),
                new TargetCorner(0.0, 0.0),
                new TargetCorner(0.0, 0.0),
                new TargetCorner(0.0, 0.0)
            );

            PhotonTrackedTarget realTarget = new PhotonTrackedTarget(
                0.0, 0.0, 0.0, 0.0, 
                id,  // fiducialId
                -1,  // classId (default to none)
                -1.0f, // objDetectConf
                new edu.wpi.first.math.geometry.Transform3d(), 
                new edu.wpi.first.math.geometry.Transform3d(), 
                0.0, dummyCorners, List.of()
            );
            generatedMocks.add(realTarget);
        }

        assertEquals(2, generatedMocks.size());

        PhotonPipelineResult mockFrame = mock(PhotonPipelineResult.class);
        when(mockFrame.getTargets()).thenReturn(generatedMocks);
        when(mockCamera.getAllUnreadResults()).thenReturn(List.of(mockFrame));
        
        // Cycle periodic to capture the frame into the subsystem context
        visionSubsystem.periodic();
        
        return generatedMocks;
    }
}