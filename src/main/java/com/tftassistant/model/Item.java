package com.tftassistant.model;

import java.util.List;

public class Item {
    private String name;
    private String description;
    private List<String> components; // Lista de itens básicos que o compõem
    private String imageUrl;
    // Adicionar outros atributos, como stats fornecidos, efeitos especiais, etc.

    // Construtor
    public Item(String name) {
        this.name = name;
        // TODO: Inicializar description, components, imageUrl (talvez buscando de uma fonte de dados)
        this.description = "Descrição do item..."; // Placeholder
        this.components = List.of(); // Placeholder
        this.imageUrl = "path/to/image.png"; // Placeholder
    }

    // Getters (e Setters, se necessário)
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getComponents() {
        return components;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String toString() {
        return "Item{" +
               "name='" + name + '\'' +
               '}';
    }

    // TODO: Implementar equals() e hashCode() se for usar Itens em Sets ou como chaves de Map
}