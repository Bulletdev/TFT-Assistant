package com.tftassistant.data.cdragon;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignora campos do JSON que não mapeamos
public class ChampionData {

    @JsonProperty("apiName") // Mapeia a propriedade "apiName" do JSON para este campo
    private String apiName;

    @JsonProperty("name")
    private String name;

    @JsonProperty("cost")
    private int cost;

    @JsonProperty("traits")
    private List<String> traits; // Nomes das traits/sinergias

    // Getters (necessários para Jackson e para nosso uso)
    public String getApiName() {
        return apiName;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public List<String> getTraits() {
        return traits;
    }

    // Setters podem ser adicionados se necessário, mas Jackson geralmente não precisa deles para deserializar
} 