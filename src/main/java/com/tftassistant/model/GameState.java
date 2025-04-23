package com.tftassistant.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameState {
    private int gold;
    private int health;
    private int level;
    private int currentStage;
    private List<Champion> playerChampions; // Campeões no tabuleiro e banco
    private List<Item> playerItems; // Itens no banco
    private Map<String, Double> activeSynergies;
    private int stage;
    private int round;

    public GameState() {
        this.gold = 0;
        this.health = 100;
        this.level = 1;
        this.currentStage = 1;
        this.playerChampions = new ArrayList<>();
        this.playerItems = new ArrayList<>();
    }

    // Getters e Setters
    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(int currentStage) {
        this.currentStage = currentStage;
    }

    public List<Champion> getPlayerChampions() {
        return playerChampions;
    }

    public void setPlayerChampions(List<Champion> playerChampions) {
        this.playerChampions = playerChampions;
    }

    public List<Item> getPlayerItems() {
        return playerItems;
    }

    public void setPlayerItems(List<Item> playerItems) {
        this.playerItems = playerItems;
    }

    public Map<String, Double> getActiveSynergies() {
        return activeSynergies;
    }

    public void setActiveSynergies(Map<String, Double> activeSynergies) {
        this.activeSynergies = activeSynergies;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    @Override
    public String toString() {
        return "GameState{" +
               "gold=" + gold +
               ", health=" + health +
               ", level=" + level +
               ", currentStage=" + currentStage +
               ", playerChampions=" + playerChampions.size() +
               ", playerItems=" + playerItems.size() +
               '}';
    }
}