package com.tftassistant.core;

import com.tftassistant.api.RiotApiConnector;
import com.tftassistant.model.Champion;
import com.tftassistant.model.Item;
import com.tftassistant.data.DataDragonLoader;
import com.tftassistant.data.cdragon.ChampionData;
import com.tftassistant.data.cdragon.CommunityDragonData;
import com.tftassistant.data.cdragon.TraitData;
import com.tftassistant.data.cdragon.TraitEffectData;
import com.tftassistant.data.cdragon.ItemData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Objects;

public class CompositionAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(CompositionAnalyzer.class);
    private static final String CURRENT_SET = "14"; // Definir o set atual aqui

    // Tornar públicos para acesso externo (ex: PositioningAssistant)
    public static final Set<String> TANK_TRAITS = Set.of(
        "Warden", "Bruiser", "Bastion", "Behemoth", "Guardian" // Exemplos comuns
    );
    public static final Set<String> CARRY_TRAITS = Set.of(
        "Duelist", "Executioner", "Slayer", "Sharpshooter", "Arcanist", "Invoker", "Sorcerer", "Trickshot" // Exemplos comuns
    );

    private RiotApiConnector apiConnector;
    // Mapas que serão populados com dados carregados
    private Map<String, List<String>> championTraitsMap; // Nome do campeão -> Lista de nomes de Traits
    private Map<String, List<Integer>> traitThresholdsMap; // Nome da Trait -> Lista de limiares (minUnits)
    private Map<String, ChampionData> championsDataMap; // apiName -> ChampionData
    private Map<String, TraitData> traitsDataMap; // apiName -> TraitData
    private Map<String, ItemData> itemsDataMap; // apiName -> ItemData

    public CompositionAnalyzer(RiotApiConnector apiConnector) {
        this.apiConnector = apiConnector;
        // Inicializar mapas vazios
        this.championTraitsMap = new HashMap<>();
        this.traitThresholdsMap = new HashMap<>();
        this.championsDataMap = new HashMap<>();
        this.traitsDataMap = new HashMap<>();
        this.itemsDataMap = new HashMap<>();

        // Carregar dados na inicialização
        loadAndProcessExternalData();

        // System.out.println("CompositionAnalyzer inicializado."); // Log movido para loadAndProcess
    }

    private void loadAndProcessExternalData() {
        DataDragonLoader loader = new DataDragonLoader();
        CommunityDragonData cdragonData = loader.loadData();

        if (cdragonData != null) {
            // Processar Itens (são globais, não por set)
            if (cdragonData.getItems() != null) {
                 processItemsData(cdragonData.getItems());
            }

            // Processar Dados do Set Atual
            if (cdragonData.getSets() != null) {
                CommunityDragonData.SetData currentSetData = cdragonData.getSets().get(CURRENT_SET);
                if (currentSetData != null) {
                    processSetData(currentSetData);
                    logger.info("CompositionAnalyzer inicializado com dados do Set {} e {} itens.", CURRENT_SET, itemsDataMap.size());
                } else {
                    logger.error("Não foram encontrados dados para o Set {} no Community Dragon.", CURRENT_SET);
                    initializeEmptyData(); // Falha ao carregar dados específicos do set
                }
            } else {
                 logger.error("Não foram encontrados dados de Sets no Community Dragon.");
                 initializeEmptyData();
            }
        } else {
            logger.error("Falha ao carregar ou processar dados do Community Dragon. Usando dados vazios.");
            initializeEmptyData(); // Falha geral ao carregar dados
        }
    }

    private void processSetData(CommunityDragonData.SetData setData) {
        if (setData.getChampions() != null) {
            for (ChampionData champ : setData.getChampions()) {
                if (champ.getName() != null && champ.getTraits() != null) {
                    championTraitsMap.put(champ.getName(), champ.getTraits());
                }
                 if (champ.getName() != null) {
                    championsDataMap.put(champ.getName(), champ);
                 } else if (champ.getApiName() != null) {
                     logger.warn("Campeão com apiName {} não possui nome.", champ.getApiName());
                 }
            }
        }

        if (setData.getTraits() != null) {
            for (TraitData trait : setData.getTraits()) {
                 if (trait.getName() != null && trait.getEffects() != null) {
                    List<Integer> thresholds = trait.getEffects().stream()
                            .map(TraitEffectData::getMinUnits)
                            .filter(units -> units > 0)
                            .sorted()
                            .collect(Collectors.toList());
                    if (!thresholds.isEmpty()) {
                        traitThresholdsMap.put(trait.getName(), thresholds);
                    }
                 }
                 if (trait.getApiName() != null) {
                    traitsDataMap.put(trait.getApiName(), trait);
                 }
            }
        }
        logger.info("Processados {} campeões e {} traits do Set {}.", championTraitsMap.size(), traitThresholdsMap.size(), CURRENT_SET);
    }

    private void processItemsData(List<ItemData> items) {
        for (ItemData item : items) {
            if (item.getApiName() != null) {
                itemsDataMap.put(item.getApiName(), item);
            } else if (item.getName() != null) {
                logger.warn("Item '{}' não possui apiName, usando nome como chave.", item.getName());
                itemsDataMap.put(item.getName(), item);
            }
        }
         logger.info("Processados {} itens globais.", itemsDataMap.size());
    }

    private void initializeEmptyData() {
        this.championTraitsMap = new HashMap<>();
        this.traitThresholdsMap = new HashMap<>();
        this.championsDataMap = new HashMap<>();
        this.traitsDataMap = new HashMap<>();
        this.itemsDataMap = new HashMap<>();
        logger.warn("CompositionAnalyzer inicializado com dados vazios devido a erro de carregamento.");
    }

    /**
     * Recomenda a melhor composição possível dado um conjunto de campeões disponíveis e um tamanho máximo.
     * Utiliza uma abordagem "greedy" para adicionar campeões que maximizam o ganho de nível de sinergia a cada passo.
     *
     * @param availableChampions Lista de campeões disponíveis para escolher.
     * @param maxSize O tamanho máximo da composição final (ex: nível do jogador).
     * @return Uma lista de campeões representando a composição recomendada.
     */
    public List<Champion> recommendBestComposition(List<Champion> availableChampions, int maxSize) {
        List<Champion> currentComposition = new ArrayList<>();
        Map<String, Integer> currentTraitCounts = new HashMap<>();
        // Usar uma cópia para não modificar a lista original externa
        List<Champion> remainingChampions = new ArrayList<>(availableChampions);

        while (currentComposition.size() < maxSize && !remainingChampions.isEmpty()) {
            Champion bestChampionToAdd = null;
            int bestImprovementScore = -1; // Prioriza qualquer melhoria, depois a que ativa o tier mais alto

            // Iterar sobre os campeões restantes para encontrar o melhor para adicionar
            for (Champion candidate : remainingChampions) {
                // Calcula qual seria o maior tier NOVO ativado por este campeão
                int currentMaxNewTier = 0;
                boolean improves = false;

                List<String> traits = championTraitsMap.getOrDefault(candidate.getName(), List.of());
                for (String traitName : traits) {
                    List<Integer> thresholds = traitThresholdsMap.get(traitName);
                    if (thresholds == null || thresholds.isEmpty()) continue;

                    int currentCount = currentTraitCounts.getOrDefault(traitName, 0);
                    int newCount = currentCount + 1;

                    int currentTier = getTierForCount(currentCount, thresholds);
                    int newTier = getTierForCount(newCount, thresholds);

                    if (newTier > currentTier) {
                        improves = true;
                        if (newTier > currentMaxNewTier) {
                            currentMaxNewTier = newTier;
                        }
                    }
                }

                // Se este candidato melhora alguma sinergia e ativa um tier mais alto que o melhor encontrado até agora
                if (improves && currentMaxNewTier > bestImprovementScore) {
                    bestImprovementScore = currentMaxNewTier;
                    bestChampionToAdd = candidate;
                }
                 // TODO: Adicionar lógica de desempate? (ex: custo do campeão, número de traits melhoradas)
            }

            // Se encontramos um campeão que melhora as sinergias, adiciona-o
            if (bestChampionToAdd != null) {
                currentComposition.add(bestChampionToAdd);
                updateSynergies(bestChampionToAdd, currentTraitCounts);
                remainingChampions.remove(bestChampionToAdd);
                logger.debug("Adicionado {} (melhorou sinergia)", bestChampionToAdd.getName());
            } else {
                // Nenhum campeão restante melhora as sinergias.
                // Fallback: Adicionar o campeão de maior custo restante.
                Champion highestCostRemaining = null;
                int maxCost = -1;
                for (Champion candidate : remainingChampions) {
                    ChampionData data = championsDataMap.get(candidate.getName()); // Busca pelo NOME
                    int cost = (data != null) ? data.getCost() : 0;
                    if (cost > maxCost) {
                        maxCost = cost;
                        highestCostRemaining = candidate;
                    } else if (cost == maxCost && highestCostRemaining == null) {
                         // Caso simples de desempate se o primeiro tiver custo 0 ou negativo
                         highestCostRemaining = candidate;
                    }
                }

                if (highestCostRemaining != null) {
                    currentComposition.add(highestCostRemaining);
                    updateSynergies(highestCostRemaining, currentTraitCounts);
                    remainingChampions.remove(highestCostRemaining);
                    logger.debug("Adicionado {} (fallback por custo)", highestCostRemaining.getName());
                } else {
                    // Não há mais campeões para adicionar (nem por melhoria, nem por custo)
                    logger.debug("Nenhum campeão para adicionar. Parando a construção.");
                    break; // Para o loop se não houver mais ninguém
                }
            }
        }

        logger.info("Composição recomendada ({} campeões): {}", currentComposition.size(), currentComposition.stream().map(Champion::getName).collect(Collectors.toList()));
        // logger.info("Níveis de sinergia finais: {}", calculateSynergies(currentComposition)); // Log opcional

        return currentComposition;
    }

    private boolean wouldImproveSynergies(Champion champion, Map<String, Integer> currentTraitCounts) {
        if (champion == null || champion.getName() == null) {
            logger.warn("Tentativa de verificar melhoria de sinergias com campeão inválido ou sem nome.");
            return false;
        }

        List<String> traits = championTraitsMap.getOrDefault(champion.getName(), List.of());

        for (String traitName : traits) {
            List<Integer> thresholds = traitThresholdsMap.get(traitName);
            if (thresholds == null || thresholds.isEmpty()) {
                continue; // Pula traits sem thresholds definidos
            }

            int currentCount = currentTraitCounts.getOrDefault(traitName, 0);
            int newCount = currentCount + 1;

            int currentTier = getTierForCount(currentCount, thresholds);
            int newTier = getTierForCount(newCount, thresholds);

            if (newTier > currentTier) {
                // logger.debug("Adicionar {} melhoraria a trait {} para o nível {}.", champion.getName(), traitName, newTier);
                return true; // Encontrou uma melhoria
            }
        }

        return false; // Nenhuma sinergia foi melhorada (nenhum novo tier alcançado)
    }

    /**
     * Calcula o nível (tier) alcançado para uma dada contagem e lista de limiares.
     *
     * @param count A contagem de unidades da trait.
     * @param thresholds A lista ordenada de limiares (minUnits) para a trait.
     * @return O nível alcançado (0 se inativo, 1 para o primeiro tier, etc.).
     */
    private int getTierForCount(int count, List<Integer> thresholds) {
        int achievedTier = 0;
        if (thresholds != null) {
            for (int i = 0; i < thresholds.size(); i++) {
                if (count >= thresholds.get(i)) {
                    achievedTier = i + 1;
                } else {
                    break; // Limiares são ordenados
                }
            }
        }
        return achievedTier;
    }

    private void updateSynergies(Champion champion, Map<String, Integer> traitCounts) {
        if (champion == null || champion.getName() == null) {
            logger.warn("Tentativa de atualizar sinergias com campeão inválido ou sem nome.");
            return;
        }
        List<String> traits = championTraitsMap.getOrDefault(champion.getName(), List.of());
        for (String traitName : traits) {
            traitCounts.merge(traitName, 1, Integer::sum);
        }
    }

    /**
     * Calcula o nível (tier) de cada sinergia ativa para a composição de campeões fornecida.
     *
     * @param champions A lista de campeões na composição atual.
     * @return Um mapa onde a chave é o nome da Trait e o valor é o nível alcançado (0 se inativo, 1 para o primeiro tier, etc.).
     */
    public Map<String, Integer> calculateSynergies(List<Champion> champions) {
        Map<String, Integer> traitCounts = new HashMap<>();
        Map<String, Integer> synergyLevels = new HashMap<>();

        // 1. Contar unidades de cada trait
        for (Champion champ : champions) {
            if (champ == null || champ.getName() == null) {
                logger.warn("Campeão inválido ou sem nome encontrado na lista.");
                continue; // Pula campeão inválido
            }
            List<String> traits = championTraitsMap.getOrDefault(champ.getName(), List.of());
            for (String traitName : traits) {
                traitCounts.merge(traitName, 1, Integer::sum);
            }
        }

        // 2. Calcular o nível (tier) para cada trait com base nos counts e thresholds
        for (Map.Entry<String, Integer> entry : traitCounts.entrySet()) {
            String traitName = entry.getKey();
            int count = entry.getValue();
            List<Integer> thresholds = traitThresholdsMap.get(traitName);
            int achievedTier = 0; // Nível 0 por padrão (inativo)

            if (thresholds != null && !thresholds.isEmpty()) {
                // Os thresholds já estão ordenados durante o processamento
                for (int i = 0; i < thresholds.size(); i++) {
                    if (count >= thresholds.get(i)) {
                        achievedTier = i + 1; // Nível 1 para o primeiro threshold, 2 para o segundo, etc.
                    } else {
                        break; // Como os thresholds são ordenados, não pode alcançar níveis maiores
                    }
                }
            } else {
                // Log se uma trait contada não tem thresholds definidos (pode indicar erro nos dados ou trait sem tiers)
                // logger.warn("Nenhum threshold encontrado para a trait: {}", traitName);
                // Considerar traits sem thresholds como sempre ativas no nível 1 se presentes? Ou nível 0?
                // Por segurança, mantemos nível 0 se não há thresholds definidos.
            }

            if (achievedTier > 0) { // Adiciona ao mapa apenas sinergias ativas (tier > 0)
                synergyLevels.put(traitName, achievedTier);
            }
        }

        // logger.debug("Níveis de sinergia calculados: {}", synergyLevels); // Log opcional para debug
        return synergyLevels;
    }

    // Getter para o mapa de traits por campeão
    public Map<String, List<String>> getChampionTraitsMap() {
        return championTraitsMap;
    }

    // Getter para o mapa de dados dos campeões
    public Map<String, ChampionData> getChampionsDataMap() {
        return championsDataMap;
    }

    /**
     * Recomenda até 3 nomes de itens para um campeão baseado em sua função inferida.
     * Retorna nomes de itens (String) baseados nos dados carregados.
     *
     * @param champion O campeão para o qual recomendar itens.
     * @param currentComposition A composição atual (pode ser usada para contexto futuro).
     * @return Lista de nomes (String) dos itens recomendados (máximo 3).
     */
    public List<String> recommendItems(Champion champion, List<Champion> currentComposition) {
        List<String> recommendedItemNames = new ArrayList<>();
        String championName = champion.getName();

        if (isMainCarry(championName, currentComposition)) {
            recommendedItemNames.addAll(getOffensiveItemNames());
        } else if (isTank(championName)) {
            recommendedItemNames.addAll(getDefensiveItemNames());
        } else {
            recommendedItemNames.addAll(getSupportItemNames());
        }

        // Embaralhar para variedade simples e pegar até 3
        java.util.Collections.shuffle(recommendedItemNames);
        return recommendedItemNames.subList(0, Math.min(3, recommendedItemNames.size()));
    }

    // Método adaptado para usar championName e buscar dados dinâmicos
    private boolean isMainCarry(String championName, List<Champion> composition) {
        if (championName == null) return false;

        ChampionData data = championsDataMap.get(championName); // Busca pelo NOME (display name) que usamos como chave
        int tier = (data != null) ? data.getCost() : 0; // Pega custo do ChampionData

        if (tier == 0) {
             logger.warn("Não foi possível encontrar dados (tier) para: {}. Não pode ser carry.", championName);
             // Tentar buscar pelo apiName se o championName falhar?
             // Por ora, se não achou pelo nome, não é considerado carry.
            return false;
        }

        // Lógica: Tier 4/5 OU Tier 3 com trait de carry
        return tier >= 4 || (tier == 3 && hasCarrySynergies(championName));
    }

    // Método adaptado para usar championName e buscar traits dinâmicas
    private boolean hasCarrySynergies(String championName) {
        if (championName == null) return false;

        List<String> traits = championTraitsMap.getOrDefault(championName, List.of());
        return traits.stream().anyMatch(CARRY_TRAITS::contains);
    }

    // Método adaptado para usar championName e buscar traits dinâmicas
    private boolean isTank(String championName) {
         if (championName == null) return false;

         List<String> traits = championTraitsMap.getOrDefault(championName, List.of());
         return traits.stream().anyMatch(TANK_TRAITS::contains);
    }

    /**
     * Retorna nomes de itens ofensivos completos (Tier 2) com base nos dados carregados.
     * A lógica de filtragem é básica e pode precisar de ajustes.
     */
    private List<String> getOffensiveItemNames() {
        if (itemsDataMap == null) return List.of();
        return itemsDataMap.values().stream()
            .filter(item -> item.getTier() == 2) // Apenas itens completos
            .filter(item -> item.getEffects() != null &&
                           (item.getEffects().containsKey("Damage") ||
                            item.getEffects().containsKey("CritChance") ||
                            item.getEffects().containsKey("AttackSpeed") ||
                            item.getEffects().containsKey("AbilityPower")))
            .map(ItemData::getName)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * Retorna nomes de itens defensivos completos (Tier 2) com base nos dados carregados.
     * A lógica de filtragem é básica e pode precisar de ajustes.
     */
    private List<String> getDefensiveItemNames() {
        if (itemsDataMap == null) return List.of();
        return itemsDataMap.values().stream()
            .filter(item -> item.getTier() == 2)
            .filter(item -> item.getEffects() != null &&
                           (item.getEffects().containsKey("Health") ||
                            item.getEffects().containsKey("Armor") ||
                            item.getEffects().containsKey("MagicResist")))
            .map(ItemData::getName)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * Retorna nomes de itens de suporte/utilidade completos (Tier 2).
     * A lógica é um placeholder, pegando itens conhecidos ou que não são claramente ofensivos/defensivos.
     */
    private List<String> getSupportItemNames() {
         if (itemsDataMap == null) return List.of();
         // Exemplo: Itens com Mana, auras, ou que não foram pegos pelos outros filtros?
         // Placeholder simples por enquanto:
         Set<String> knownSupport = Set.of("Zeke's Herald", "Locket of the Iron Solari", "Redemption", "Zephyr", "Spear of Shojin", "Protector's Vow");
         // Poderia filtrar por tier 2 e nomes conhecidos ou por falta de stats ofens/defens primários.
         return itemsDataMap.values().stream()
             .filter(item -> item.getTier() == 2)
             .map(ItemData::getName)
             .filter(Objects::nonNull)
             .filter(knownSupport::contains) // Filtra por nomes conhecidos (muito básico)
             .collect(Collectors.toList());
    }

    public void analyzeComposition(/* Parâmetros relevantes, ex: lista de campeões/itens */) {
        // TODO: Implementar a lógica de análise de composição
        // - Usar apiConnector para obter dados atualizados (se necessário)
        // - Calcular sinergias de traits
        // - Avaliar força da composição
        // - Sugerir melhorias ou próximos passos
        System.out.println("Analisando composição...");
    }

    // Getter para o mapa de itens (pode ser útil)
    public Map<String, ItemData> getItemsDataMap() {
        return itemsDataMap;
    }
}