package com.tftassistant.data.cdragon;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TraitData {

    @JsonProperty("apiName")
    private String apiName;

    @JsonProperty("name")
    private String name;

    @JsonProperty("effects")
    private List<TraitEffectData> effects; // Lista dos níveis/efeitos da trait

    // Getters
    public String getApiName() {
        return apiName;
    }

    public String getName() {
        return name;
    }

    public List<TraitEffectData> getEffects() {
        return effects;
    }
} 