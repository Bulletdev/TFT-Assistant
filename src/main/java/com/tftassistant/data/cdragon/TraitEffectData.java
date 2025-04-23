package com.tftassistant.data.cdragon;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TraitEffectData {

    @JsonProperty("minUnits")
    private int minUnits; // Número mínimo de unidades para ativar este nível

    @JsonProperty("maxUnits")
    private int maxUnits; // Número máximo de unidades para este nível (pode ser 0 ou ausente se for o último)

    @JsonProperty("style")
    private int style; // Estilo/Tier do efeito (e.g., 1 Bronze, 2 Silver, 3 Gold, 4 Prismatic)

    @JsonProperty("variables")
    private Map<String, Float> variables; // Variáveis numéricas do efeito (usar Float para flexibilidade)

    // Getters
    public int getMinUnits() {
        return minUnits;
    }

    public int getMaxUnits() {
        return maxUnits;
    }

    public int getStyle() {
        return style;
    }

    public Map<String, Float> getVariables() {
        return variables;
    }
} 