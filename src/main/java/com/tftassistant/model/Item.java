package com.tftassistant.model;

import java.util.List;

public class Item {
    private String name;
    private String description;
    private List<String> effects;
    private List<Item> components;
    private String imageUrl;
    private boolean isComponent;

    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters e Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getEffects() { return effects; }
    public void setEffects(List<String> effects) { this.effects = effects; }

    public List<Item> getComponents() { return components; }
    public void setComponents(List<Item> components) { this.components = components; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isComponent() { return isComponent; }
    public void setComponent(boolean component) { isComponent = component; }
}