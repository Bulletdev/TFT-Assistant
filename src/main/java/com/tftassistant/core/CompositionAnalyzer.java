class CompositionAnalyzer {
    private RiotApiConnector apiConnector;
    private Map<String, List<String>> championSynergies;
    private Map<String, Integer> synergyThresholds;

    public CompositionAnalyzer(RiotApiConnector apiConnector) {
        this.apiConnector = apiConnector;
        initializeSynergies();
    }

    private void initializeSynergies() {
        championSynergies = new HashMap<>();
        synergyThresholds = new HashMap<>();

        championSynergies.put("Renata Glasc", Arrays.asList("Visionário", "Barão da Química"));
        championSynergies.put("Trundle", Arrays.asList("Brutamontes", "Sucateiro"));
        championSynergies.put("Violet", Arrays.asList("Lutador Feroz", "Família"));
        championSynergies.put("Jinx", Arrays.asList("Emboscador", "Rebelde"));
        championSynergies.put("Sevika", Arrays.asList("Dominador", "Apostadora"));
        championSynergies.put("Ekko", Arrays.asList("Emboscador", "Sucateiro"));
        championSynergies.put("Vi", Arrays.asList("Lutador Feroz", "Sentinela"));
        championSynergies.put("Heimerdinger", Arrays.asList("Acadêmico", "Visionário"));
        championSynergies.put("Irelia", Arrays.asList("Rebelde", "Sentinela"));
        championSynergies.put("Leona", Arrays.asList("Acadêmico", "Sentinela"));

        synergyThresholds.put("Rosa Negra", Arrays.asList(3, 4, 5, 7));
        synergyThresholds.put("Feiticeiro", Arrays.asList(2, 4, 6, 8));
        synergyThresholds.put("Sucateiro", Arrays.asList(2, 4, 6, 9));
        synergyThresholds.put("Lutador Feroz", Arrays.asList(2, 4, 6, 8));
        synergyThresholds.put("Rebelde", Arrays.asList(3, 5, 7, 10));
        synergyThresholds.put("Sentinela", Arrays.asList(2, 4, 6));
        synergyThresholds.put("Emboscador", Arrays.asList(2, 3, 4, 5));
        synergyThresholds.put("Visionário", Arrays.asList(2, 4, 6, 8));
        synergyThresholds.put("Dominador", Arrays.asList(2, 4, 6));
        synergyThresholds.put("Acadêmico", Arrays.asList(3, 4, 5, 6));
        // Adicione mais limiares de sinergias conforme necessário
    }

    public List<Champion> recommendBestComposition(List<Champion> availableChampions) {
        List<Champion> bestComp = new ArrayList<>();
        Map<String, Integer> currentSynergies = new HashMap<>();

        for (Champion champ : availableChampions) {
            if (wouldImproveSynergies(champ, currentSynergies) && bestComp.size() < 9) {
                bestComp.add(champ);
                updateSynergies(champ, currentSynergies);
            }
        }

        return bestComp;
    }

    private boolean wouldImproveSynergies(Champion champion, Map<String, Integer> currentSynergies) {
        Map<String, Integer> testSynergies = new HashMap<>(currentSynergies);

        for (String synergy : champion.getSynergies()) {
            testSynergies.merge(synergy, 1, Integer::sum);
            if (testSynergies.get(synergy).equals(synergyThresholds.get(synergy))) {
                return true;
            }
        }

        return false;
    }

    private void updateSynergies(Champion champion, Map<String, Integer> currentSynergies) {
        for (String synergy : champion.getSynergies()) {
            currentSynergies.merge(synergy, 1, Integer::sum);
        }
    }

    public Map<String, Double> calculateSynergies(List<Champion> champions) {
        Map<String, Double> synergiesStrength = new HashMap<>();
        Map<String, Integer> synergyCount = new HashMap<>();

        // Contar sinergias
        for (Champion champ : champions) {
            for (String synergy : champ.getSynergies()) {
                synergyCount.merge(synergy, 1, Integer::sum);
            }
        }

        for (Map.Entry<String, Integer> entry : synergyCount.entrySet()) {
            String synergy = entry.getKey();
            int count = entry.getValue();
            int threshold = synergyThresholds.getOrDefault(synergy, 3);

            double strength = (double) count / threshold;
            synergiesStrength.put(synergy, Math.min(1.0, strength));
        }

        return synergiesStrength;
    }

    public List<Item> recommendItems(Champion champion, List<Champion> currentComposition) {
        List<Item> recommendedItems = new ArrayList<>();

        if (isMainCarry(champion, currentComposition)) {
            recommendedItems.addAll(getOffensiveItems());
        } else if (isTank(champion)) {
            recommendedItems.addAll(getDefensiveItems());
        } else {
            recommendedItems.addAll(getSupportItems());
        }

        return recommendedItems.subList(0, Math.min(3, recommendedItems.size()));
    }

    private boolean isMainCarry(Champion champion, List<Champion> composition) {
        // Identificar se é o carry principal baseado no custo e sinergias
        return champion.getTier() >= 4 ||
                (champion.getTier() == 3 && hasCarrySynergies(champion));
    }

    private boolean hasCarrySynergies(Champion champion) {
        Set<String> carrySynergies = Set.of("Assassin", "Sharpshooter", "Slayer");
        return champion.getSynergies().stream()
                .anyMatch(carrySynergies::contains);
    }
}