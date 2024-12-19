package com.tftassistant.core;

import com.tftassistant.model.Champion;
import com.tftassistant.model.GameState;
import com.tftassistant.model.Item;
import com.tftassistant.util.ScreenCaptureModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameStateTrackerTest {

    @Mock
    private ScreenCaptureModule screenCaptureModule;

    @Mock
    private CompositionAnalyzer compositionAnalyzer;

    private GameStateTracker gameStateTracker;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameStateTracker = new GameStateTracker(screenCaptureModule, compositionAnalyzer);
    }

    @Test
    void getCurrentGameState_ShouldReturnValidGameState() {
        // Arrange
        BufferedImage mockScreenshot = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);
        String mockExtractedText = "Gold: 50\nHealth: 100\nStage 3-2";

        when(screenCaptureModule.captureScreen()).thenReturn(mockScreenshot);
        when(screenCaptureModule.extractTextFromScreen(mockScreenshot))
                .thenReturn(mockExtractedText);

        // Act
        GameState gameState = gameStateTracker.getCurrentGameState();

        // Assert
        assertNotNull(gameState);
        assertEquals(50, gameState.getGold());
        assertEquals(100, gameState.getHealth());
    }

    @Test
    void processGameState_ShouldIdentifyChampionsCorrectly() {
        // Arrange
        String extractedText = "Vayne\nGaren\nDarius";
        List<String> expectedChampions = Arrays.asList("Vayne", "Garen", "Darius");

        // Act
        GameState gameState = gameStateTracker.processGameState(extractedText);

        // Assert
        List<Champion> actualChampions = gameState.getPlayerChampions();
        assertEquals(expectedChampions.size(), actualChampions.size());
        assertTrue(actualChampions.stream()
                .map(Champion::getName)
                .allMatch(expectedChampions::contains));
    }

    @Test
    void processGameState_ShouldIdentifyItemsCorrectly() {
        // Arrange
        String extractedText = "B.F. Sword\nChain Vest\nGiant's Belt";
        List<String> expectedItems = Arrays.asList("B.F. Sword", "Chain Vest", "Giant's Belt");

        // Act
        GameState gameState = gameStateTracker.processGameState(extractedText);

        // Assert
        List<Item> actualItems = gameState.getPlayerItems();
        assertEquals(expectedItems.size(), actualItems.size());
        assertTrue(actualItems.stream()
                .map(Item::getName)
                .allMatch(expectedItems::contains));
    }

    @Test
    void trackMatchHistory_ShouldUpdateMatchHistory() {
        // Arrange
        GameState mockGameState = mock(GameState.class);
        when(mockGameState.getPlayerChampions())
                .thenReturn(Arrays.asList(new Champion("Vayne"), new Champion("Garen")));

        // Act
        gameStateTracker.trackMatchHistory();

        // Assert
        // Verify match history is updated (implementation specific assertions)
        verify(compositionAnalyzer, times(1))
                .calculateSynergies(any());
    }
}