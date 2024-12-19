package com.tftassistant.core;

import com.tftassistant.model.Champion;
import com.tftassistant.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class CompositionAnalyzerTest {
    private CompositionAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        analyzer = new CompositionAnalyzer(null); // Mock do RiotApiConnector seria melhor
    }

    @Test
    void testRecommendBestComposition() {
        // Arrange
        List<Champion> availableChampions = Arrays.asList(
                createChampion("Ahri", Arrays.asList("Star Guardian", "Spirit"), 4),
                createChampion("Riven", Arrays.asList("Dawnbringer", "Legionnaire"), 3)
        );

        // Act
        List<Champion> recommendation = analyzer.recommendBestComposition(availableChampions);

        // Assert
        assertNotNull(recommendation);
        assertTrue(recommendation.size() > 0);
    }

    private Champion createChampion(String name, List<String> synergies, int tier) {
        Champion champion = new Champion(name);
        champion.setSynergies(synergies);
        champion.setTier(tier);
        return champion;
    }
}