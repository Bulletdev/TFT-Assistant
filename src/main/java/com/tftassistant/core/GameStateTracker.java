class GameStateTracker {
    private ScreenCaptureModule screenCaptureModule;
    private CompositionAnalyzer compositionAnalyzer;
    private Pattern goldPattern = Pattern.compile("\\d+\\s*gold");
    private Pattern healthPattern = Pattern.compile("\\d+\\s*hp");

    public GameState getCurrentGameState() {
        BufferedImage screenshot = screenCaptureModule.captureScreen();
        String extractedText = screenCaptureModule.extractTextFromScreen(screenshot);
        return processGameState(extractedText);
    }

    private GameState processGameState(String extractedText) {
        GameState state = new GameState();

        // Extrair ouro
        Matcher goldMatcher = goldPattern.matcher(extractedText.toLowerCase());
        if (goldMatcher.find()) {
            state.setGold(Integer.parseInt(goldMatcher.group().replaceAll("\\D", "")));
        }

        // Extrair vida
        Matcher healthMatcher = healthPattern.matcher(extractedText.toLowerCase());
        if (healthMatcher.find()) {
            state.setHealth(Integer.parseInt(healthMatcher.group().replaceAll("\\D", "")));
        }

        // Identificar campeões na tela
        List<Champion> detectedChampions = detectChampionsFromText(extractedText);
        state.setPlayerChampions(detectedChampions);

        // Identificar itens
        List<Item> detectedItems = detectItemsFromText(extractedText);
        state.setPlayerItems(detectedItems);

        return state;
    }

    private List<Champion> detectChampionsFromText(String text) {
        List<Champion> detected = new ArrayList<>();
        // Usar uma lista de nomes de campeões conhecidos e procurar no texto
        for (String championName : getKnownChampionNames()) {
            if (text.contains(championName)) {
                Champion champion = new Champion(championName);
                detected.add(champion);
            }
        }
        return detected;
    }

    private List<String> getKnownChampionNames() {
        // Retornar lista de nomes de campeões do set atual
        return Arrays.asList("Ahri", "Riven", "Yasuo", "Karma", /* etc */);
    }
}