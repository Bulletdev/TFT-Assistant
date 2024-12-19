package com.tftassistant.model;

import java.util.List;
import java.util.ArrayList;

public class Champion {
    private String name;
    private List<String> synergies;
    private int tier;
    private int cost;
    private String imageUrl;
    private List<Item> items;

    public Champion(String name) {
        this.name = name;
        this.synergies = new ArrayList<>();
        this.items = new ArrayList<>();
    }

    // Getters e Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getSynergies() { return synergies; }
    public void setSynergies(List<String> synergies) { this.synergies = synergies; }

    public int getTier() { return tier; }
    public void setTier(int tier) { this.tier = tier; }

    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }

    public void addItem(Item item) {
        if (items.size() < 3) {
            items.add(item);
        }
    }
}