package com.tftassistant.data.cdragon;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommunityDragonData {

    // Mapeia a lista de itens
    @JsonProperty("items")
    private List<ItemData> items;

    // Mapeia os dados dos sets
    @JsonProperty("sets")
    private Map<String, SetData> sets; // A chave será "11", "12", "14" etc.

    // Getters
    public List<ItemData> getItems() { return items; }
    public Map<String, SetData> getSets() { return sets; }

    // Classe interna para representar os dados de um Set específico
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SetData {

        @JsonProperty("champions")
        private List<ChampionData> champions;

        @JsonProperty("traits")
        private List<TraitData> traits;

        // Getters
        public List<ChampionData> getChampions() { return champions; }
        public List<TraitData> getTraits() { return traits; }
    }
} 