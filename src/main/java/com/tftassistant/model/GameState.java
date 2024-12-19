package com.tftassistant.model;

import java.util.List;
import java.util.Map;

public class GameState {
    private List<Champion> playerChampions;
    private List<Item> playerItems;
    private int health;
    private int gold;
    private int level;
    private Map<String, Double> activeSynergies;
    private int stage;
    private int round;

    // Getters e Setters
    public List<Champion> getPlayerChampions() { return playerChampions; }
    public void setPlayerChampions(List<Champion> playerChampions) {
        this.playerChampions = playerChampions;
    }

    public List<Item> getPlayerItems() { return playerItems; }
    public void setPlayerItems(List<Item> playerItems) { this.playerItems = playerItems; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public int getGold() { return gold; }
    public void setGold(int gold) { this.gold = gold; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public Map<String, Double> getActiveSynergies() { return activeSynergies; }
    public void setActiveSynergies(Map<String, Double> activeSynergies) {
        this.activeSynergies = activeSynergies;
    }

    public int getStage() { return stage; }
    public void setStage(int stage) { this.stage = stage; }

    public int getRound() { return round; }
    public void setRound(int round) { this.round = round; }
}