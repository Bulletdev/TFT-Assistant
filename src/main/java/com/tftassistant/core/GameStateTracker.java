package com.tftassistant.core;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import com.tftassistant.util.ScreenCaptureModule;
import com.tftassistant.core.CompositionAnalyzer;
import com.tftassistant.model.Champion;
import com.tftassistant.model.Item;
import com.tftassistant.model.GameState;

public class GameStateTracker {
    private ScreenCaptureModule screenCaptureModule;
    private CompositionAnalyzer compositionAnalyzer;
    private Pattern goldPattern = Pattern.compile("\\d+\\s*gold");
    private Pattern healthPattern = Pattern.compile("\\d+\\s*hp");

    public GameStateTracker(ScreenCaptureModule screenCaptureModule, CompositionAnalyzer compositionAnalyzer) {
        this.screenCaptureModule = screenCaptureModule;
        this.compositionAnalyzer = compositionAnalyzer;
        System.out.println("GameStateTracker inicializado."); // Log inicial
    }

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
        return Arrays.asList("Lux", "Swain", "Draven", "Jayce");
    }

    private List<Item> detectItemsFromText(String text) {
        List<Item> detected = new ArrayList<>();
        // TODO: Implementar lógica real de detecção de itens a partir do texto (OCR)
        // Exemplo simples baseado em palavras-chave
        if (text.contains("Espada G.F.")) {
            detected.add(new Item("Espada G.F."));
        }
        if (text.contains("Cinto Gigante")) {
            detected.add(new Item("Cinto do Gigante")); // Corrigindo nome para consistência
        }
        // Adicionar mais itens...
        System.out.println("Detectando itens (stub)...");
        return detected;
    }

    public void trackGameState() {
        // TODO: Implementar a lógica de rastreamento do estado do jogo
        // - Capturar a tela ou regiões relevantes usando screenCaptureModule
        // - Analisar a imagem (talvez usando OCR) para extrair informações:
        //   - Campeões no tabuleiro e banco
        //   - Itens
        //   - Nível do jogador, ouro, vida
        //   - Estágio atual
        // - Chamar compositionAnalyzer para analisar a composição atual
        // - Atualizar o estado interno do jogo
        System.out.println("Rastreando estado do jogo..."); // Log inicial
    }

    // TODO: Adicionar métodos para acessar o estado atual do jogo
}
