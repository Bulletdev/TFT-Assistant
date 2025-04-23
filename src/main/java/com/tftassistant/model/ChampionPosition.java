package com.tftassistant.model;

import java.awt.Point;

public class ChampionPosition {
    private Champion champion;
    private Point position; // Coordenadas (x, y) no tabuleiro

    public ChampionPosition(Champion champion, Point position) {
        this.champion = champion;
        this.position = position;
    }

    // Getters
    public Champion getChampion() {
        return champion;
    }

    public Point getPosition() {
        return position;
    }

    // Setters (se necessário)
    public void setPosition(Point position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "ChampionPosition{" +
               "champion=" + (champion != null ? champion.getName() : "null") +
               ", position=(" + position.x + "," + position.y + ")" +
               '}';
    }
}
