package com.tftassistant.data.cdragon;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * POJO para representar os dados de um item individual vindos do Community Dragon JSON.
 * Mapeia os campos relevantes do JSON para atributos da classe.
 */
@JsonIgnoreProperties(ignoreUnknown = true) // Ignora campos JSON não mapeados aqui
public class ItemData {

    // Identificador único da API (ex: "TFT_Item_BFSword")
    @JsonProperty("apiName")
    private String apiName;

    // Nome de exibição (ex: "B.F. Sword")
    @JsonProperty("name")
    private String name;

    // Descrição do item
    @JsonProperty("desc") // Assumindo que o campo no JSON se chama "desc"
    private String description;

    // Caminho para o ícone (pode precisar de tratamento para formar a URL/path completo)
    @JsonProperty("icon")
    private String icon;

    // Lista de apiNames dos itens componentes (vazio para itens básicos)
    @JsonProperty("from")
    private List<String> from;

    // Efeitos/Stats do item. Assumindo um mapa simples de nome -> valor.
    // A estrutura real pode ser mais complexa (ex: objetos separados para stats).
    @JsonProperty("effects")
    private Map<String, Double> effects;

    // Tier/Tipo do item (ex: 1=componente, 2=completo, 3=radiante, 4=artefato, 5=emblema)
    // O nome exato do campo ("tier", "rank", etc.) pode variar no JSON.
    @JsonProperty("tier")
    private int tier;

    // --- Getters ---

    public String getApiName() {
        return apiName;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public List<String> getFrom() {
        return from;
    }

    public Map<String, Double> getEffects() {
        return effects;
    }

    public int getTier() {
        return tier;
    }

    // toString para facilitar a depuração
    @Override
    public String toString() {
        return "ItemData{" +
               "apiName='" + apiName + '\'' +
               ", name='" + name + '\'' +
               ", tier=" + tier +
               '}';
    }

    // TODO: Implementar equals() e hashCode() se necessário (baseado em apiName).
} 