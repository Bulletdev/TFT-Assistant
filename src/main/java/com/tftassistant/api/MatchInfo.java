package com.tftassistant.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchInfo {
    private Metadata metadata;
    private Info info;

    // Getters e Setters
    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    // Classes internas para representar estruturas aninhadas no JSON da API
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Metadata {
        private String dataVersion;
        private String matchId;
        private List<String> participants; // Lista de PUUIDs

        // Getters e Setters
        public String getDataVersion() { return dataVersion; }
        public void setDataVersion(String dataVersion) { this.dataVersion = dataVersion; }
        public String getMatchId() { return matchId; }
        public void setMatchId(String matchId) { this.matchId = matchId; }
        public List<String> getParticipants() { return participants; }
        public void setParticipants(List<String> participants) { this.participants = participants; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Info {
        private long gameDatetime;
        private float gameLength;
        private String gameVariation;
        private String gameVersion;
        private List<Participant> participants;
        private int queueId;
        private int tftSetNumber;

        // Getters e Setters
        public long getGameDatetime() { return gameDatetime; }
        public void setGameDatetime(long gameDatetime) { this.gameDatetime = gameDatetime; }
        public float getGameLength() { return gameLength; }
        public void setGameLength(float gameLength) { this.gameLength = gameLength; }
        public String getGameVariation() { return gameVariation; }
        public void setGameVariation(String gameVariation) { this.gameVariation = gameVariation; }
        public String getGameVersion() { return gameVersion; }
        public void setGameVersion(String gameVersion) { this.gameVersion = gameVersion; }
        public List<Participant> getParticipants() { return participants; }
        public void setParticipants(List<Participant> participants) { this.participants = participants; }
        public int getQueueId() { return queueId; }
        public void setQueueId(int queueId) { this.queueId = queueId; }
        public int getTftSetNumber() { return tftSetNumber; }
        public void setTftSetNumber(int tftSetNumber) { this.tftSetNumber = tftSetNumber; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Participant {
        private Companion companion;
        private int goldLeft;
        private int lastRound;
        private int level;
        private int placement;
        private int playersEliminated;
        private String puuid;
        private float timeEliminated;
        private int totalDamageToPlayers;
        private List<Trait> traits;
        private List<Unit> units;

        // Getters e Setters (simplificado)
        public String getPuuid() { return puuid; }
        public void setPuuid(String puuid) { this.puuid = puuid; }
        public int getPlacement() { return placement; }
        public void setPlacement(int placement) { this.placement = placement; }
        public List<Unit> getUnits() { return units; }
        public void setUnits(List<Unit> units) { this.units = units; }
        public List<Trait> getTraits() { return traits; }
        public void setTraits(List<Trait> traits) { this.traits = traits; }
        // Adicionar outros getters/setters conforme necessário
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Companion {
        private String contentId;
        private int skinId;
        private String species;
        // Getters e Setters
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Trait {
        private String name;
        private int numUnits;
        private int style;
        private int tierCurrent;
        private int tierTotal;
        // Getters e Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getNumUnits() { return numUnits; }
        public void setNumUnits(int numUnits) { this.numUnits = numUnits; }
        public int getTierCurrent() { return tierCurrent; }
        public void setTierCurrent(int tierCurrent) { this.tierCurrent = tierCurrent; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Unit {
        private String characterId;
        private List<Integer> itemIds; // Mapear para nomes de itens posteriormente se necessário
        private String name; // Geralmente vazio, use characterId
        private int rarity; // 0 para tier 1, etc.
        private int tier; // Nível de estrela (1, 2, 3)
        // Getters e Setters
        public String getCharacterId() { return characterId; }
        public void setCharacterId(String characterId) { this.characterId = characterId; }
        public List<Integer> getItemIds() { return itemIds; }
        public void setItemIds(List<Integer> itemIds) { this.itemIds = itemIds; }
        public int getTier() { return tier; }
        public void setTier(int tier) { this.tier = tier; }
    }

    @Override
    public String toString() {
        return "MatchInfo{" +
               "matchId='" + (metadata != null ? metadata.matchId : "N/A") + '\'' +
               '}';
    }
} 