package com.tftassistant.core;

import com.tftassistant.model.Champion;
import com.tftassistant.model.ChampionPosition;
import java.util.List;
import java.util.ArrayList;
import java.awt.Point;
import java.util.Map;
import java.util.HashMap;

// Importar dependência
import com.tftassistant.core.CompositionAnalyzer;

public class PositioningAssistant {
    private static final int BOARD_ROWS = 4;
    private static final int BOARD_COLS = 7;

    // Adicionar dependência
    private final CompositionAnalyzer compositionAnalyzer;

    // Modificar construtor para receber a dependência
    public PositioningAssistant(CompositionAnalyzer compositionAnalyzer) {
        this.compositionAnalyzer = compositionAnalyzer;
        System.out.println("PositioningAssistant inicializado.");
    }

    public List<ChampionPosition> recommendPositioning(List<Champion> playerChampions, List<Champion> enemyChampions) {
        List<ChampionPosition> recommendations = new ArrayList<>();

        // Analisar sinergias e roles dos campeões
        Map<String, List<Champion>> roleGroups = groupChampionsByRole(playerChampions);

        // Posicionar tanques na frente
        positionTanks(recommendations, roleGroups.get("TANK"));

        // Posicionar carregadores atrás dos tanques
        positionCarries(recommendations, roleGroups.get("CARRY"));

        // Posicionar suportes próximos aos carregadores
        positionSupports(recommendations, roleGroups.get("SUPPORT"));

        return recommendations;
    }

    private Map<String, List<Champion>> groupChampionsByRole(List<Champion> champions) {
        Map<String, List<Champion>> groups = new HashMap<>();
        for (Champion champion : champions) {
            String role = determineRole(champion);
            groups.computeIfAbsent(role, k -> new ArrayList<>()).add(champion);
        }
        return groups;
    }

    // Refatorar para usar traits do CompositionAnalyzer
    private String determineRole(Champion champion) {
        if (champion == null || champion.getName() == null || compositionAnalyzer == null) {
            return "SUPPORT"; // Default seguro
        }

        String championName = champion.getName();
        Map<String, List<String>> traitMap = compositionAnalyzer.getChampionTraitsMap();
        List<String> traits = List.of(); // Default para lista vazia
        if (traitMap != null) {
            traits = traitMap.getOrDefault(championName, List.of());
        }

        // Verificar usando os Sets públicos do CompositionAnalyzer
        boolean isTank = traits.stream().anyMatch(CompositionAnalyzer.TANK_TRAITS::contains);
        if (isTank) {
            return "TANK";
        }

        boolean isCarry = traits.stream().anyMatch(CompositionAnalyzer.CARRY_TRAITS::contains);
        if (isCarry) {
            return "CARRY";
        }

        // Se não for Tank nem Carry, assume Support
        return "SUPPORT";
    }

    private void positionTanks(List<ChampionPosition> positions, List<Champion> tanks) {
        if (tanks == null || tanks.isEmpty()) return;

        // Posicionar tanques na primeira linha
        int col = 1;
        for (Champion tank : tanks) {
            if (col >= BOARD_COLS - 1) break;
            positions.add(new ChampionPosition(tank, new Point(col, 0)));
            col += 2;
        }
    }

    private void positionCarries(List<ChampionPosition> positions, List<Champion> carries) {
        if (carries == null || carries.isEmpty()) return;

        // Posicionar carregadores na última linha
        int col = 1;
        for (Champion carry : carries) {
            if (col >= BOARD_COLS - 1) break;
            positions.add(new ChampionPosition(carry, new Point(col, BOARD_ROWS - 1)));
            col += 2;
        }
    }

    private void positionSupports(List<ChampionPosition> positions, List<Champion> supports) {
        if (supports == null || supports.isEmpty()) return;

        // Posicionar suportes próximos aos carregadores
        int col = 2;
        int row = BOARD_ROWS - 2;
        for (Champion support : supports) {
            if (col >= BOARD_COLS - 1) break;
            positions.add(new ChampionPosition(support, new Point(col, row)));
            col += 2;
        }
    }

    public void suggestPositioning(/* Parâmetros: unidades atuais, talvez inimigos */) {
        // TODO: Implementar lógica de sugestão de posicionamento
        // - Analisar unidades e suas habilidades/alcance
        // - Considerar sinergias e itens
        // - Avaliar ameaças inimigas (se disponível)
        // - Sugerir posições ótimas no tabuleiro
        System.out.println("Sugerindo posicionamento..."); // Log inicial
    }

    // TODO: Adicionar métodos auxiliares para cálculos de posicionamento
}