package com.tftassistant.core;

import com.tftassistant.model.Champion;
import com.tftassistant.model.ChampionPosition;
import java.util.List;
import java.util.ArrayList;
import java.awt.Point;

public class PositioningAssistant {
    private static final int BOARD_ROWS = 4;
    private static final int BOARD_COLS = 7;

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

    private String determineRole(Champion champion) {
        // Lógica para determinar o papel do campeão baseado em suas características
        // Retorna "TANK", "CARRY" ou "SUPPORT"
        return switch (champion.getPrimaryRole()) {
            case "ASSASSIN", "MARKSMAN" -> "CARRY";
            case "TANK", "FIGHTER" -> "TANK";
            default -> "SUPPORT";
        };
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
}