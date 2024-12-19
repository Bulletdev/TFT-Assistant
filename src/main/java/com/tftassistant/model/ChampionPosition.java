package com.tftassistant.model;

import java.awt.Point;

public class ChampionPosition {
    private Champion champion;
    private Point position;

    public ChampionPosition(Champion champion, Point position) {
        this.champion = champion;
        this.position = position;
    }

    public Champion getChampion() {
        return champion;
    }

    public void setChampion(Champion champion) {
        this.champion = champion;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return String.format("%s at (%d, %d)",
                champion.getName(),
                position.x,
                position.y);
    }
}
