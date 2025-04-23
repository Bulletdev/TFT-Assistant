package com.tftassistant.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignora campos não mapeados do JSON
public class SummonerInfo {
    private String id; // ID criptografado do invocador
    private String accountId; // ID criptografado da conta
    private String puuid; // PUUID criptografado
    private String name; // Nome de invocador
    private int profileIconId;
    private long revisionDate;
    private int summonerLevel;

    // Getters e Setters (necessários para Jackson ObjectMapper)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getPuuid() {
        return puuid;
    }

    public void setPuuid(String puuid) {
        this.puuid = puuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProfileIconId() {
        return profileIconId;
    }

    public void setProfileIconId(int profileIconId) {
        this.profileIconId = profileIconId;
    }

    public long getRevisionDate() {
        return revisionDate;
    }

    public void setRevisionDate(long revisionDate) {
        this.revisionDate = revisionDate;
    }

    public int getSummonerLevel() {
        return summonerLevel;
    }

    public void setSummonerLevel(int summonerLevel) {
        this.summonerLevel = summonerLevel;
    }

    @Override
    public String toString() {
        return "SummonerInfo{" +
               "name='" + name + '\'' +
               ", puuid='" + puuid + '\'' +
               ", summonerLevel=" + summonerLevel +
               '}';
    }
}
