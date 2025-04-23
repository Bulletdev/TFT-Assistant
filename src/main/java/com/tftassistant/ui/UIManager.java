package com.tftassistant.ui;

import com.tftassistant.core.CompositionAnalyzer;
import com.tftassistant.core.GameStateTracker;
import com.tftassistant.core.PositioningAssistant;
import com.tftassistant.data.cdragon.ChampionData;
import com.tftassistant.model.Champion;
import com.tftassistant.model.ChampionPosition;
import com.tftassistant.model.Item;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.HashMap;

public class UIManager {

    private static final Logger logger = LoggerFactory.getLogger(UIManager.class);

    private CompositionAnalyzer compositionAnalyzer;
    private GameStateTracker gameStateTracker;
    private PositioningAssistant positioningAssistant;
    private TilePane resultsPane;
    private ScrollPane resultsScrollPane;
    private TilePane championSelectionPane;
    private final Set<String> selectedChampionNames = new HashSet<>();
    private List<Champion> lastRecommendedComp = null;
    private VBox currentResultsContainer = null; // Container para Sinergias, Campeões e Posições
    private StackPane centerContentArea; // Área central que troca de conteúdo

    // Conjunto de nomes/padrões a serem excluídos da seleção de campeões
    private static final Set<String> EXCLUDED_UNIT_NAMES_PATTERNS = Set.of(
        "Bloblet", "Elder Dragon", "Rift Herald", "Rift Scuttler", "Target Dummy",
        "Training Dummy", "Golem", "Murk Wolf", "Razorbeak", "Krug", "Voidspawn",
        "Mechadrone", "Mechacannon", "Mechaminion", "Mechasoldier", "Mechaurelion", "Mechazir",
        "Tibbers", "T-URR37", "T-43X", "R-080T", "Hacked Egg", "Mercenary Chest",
        "SightWard", "Tome of Traits" // Adicionar outros se necessário
    );
    private static final List<String> EXCLUDED_API_NAME_SUBSTRINGS = List.of(
        "Item_", "Anvil", "_Avatar", "Consumable"
    );

    // Dados Placeholder - Simula composições do Set 14 (baseado em Mobalytics)
    private static class PlaceholderComp {
        String name;
        String tier;
        String playstyle;
        Map<String, List<String>> championsAndItems;

        PlaceholderComp(String name, String tier, String playstyle, Map<String, List<String>> championsAndItems) {
            this.name = name;
            this.tier = tier;
            this.playstyle = playstyle;
            this.championsAndItems = championsAndItems;
        }
    }

    private List<PlaceholderComp> getPlaceholderTeamComps() {
        List<PlaceholderComp> comps = new ArrayList<>();
        comps.add(new PlaceholderComp("SlayGuards", "S", "Level 7 Slow Roll", Map.of(
                "Zed", List.of("Edge of Night", "Hand of Justice", "Infinity Edge"),
                "Jarvan IV", List.of("Evenshroud", "Sunfire Cape", "Redemption"),
                "Senna", List.of("Infinity Edge", "Giant Slayer", "Runaan's Hurricane"),
                "Vayne", List.of("Guinsoo's Rageblade", "Runaan's Hurricane", "Guinsoo's Rageblade"),
                "Garen", List.of(),
                "Renekton", List.of(),
                "Leona", List.of(),
                "Rhaast", List.of()
        )));
        comps.add(new PlaceholderComp("Techmate", "S", "Fast 8", Map.of(
                "Brand", List.of("Guardbreaker", "Spear of Shojin", "Jeweled Gauntlet"),
                "Neeko", List.of("Ionic Spark", "Warmog's Armor", "Bramble Vest"),
                "Ziggs", List.of("Jeweled Gauntlet", "Spear of Shojin", "Red Buff"),
                "Samira", List.of("Infinity Edge", "Last Whisper"),
                "Viego", List.of("Bloodthirster", "Jeweled Gauntlet"),
                "Kobuko", List.of(),
                "Mordekaiser", List.of(),
                "Ekko", List.of()
        )));
         comps.add(new PlaceholderComp("Wild Cards", "S", "Level 6 Slow Roll", Map.of(
                "Miss Fortune", List.of("Infinity Edge", "Spear of Shojin", "Last Whisper"),
                "Braum", List.of("Sunfire Cape", "Gargoyle Stoneplate", "Warmog's Armor"),
                "Twisted Fate", List.of("Guinsoo's Rageblade", "Guinsoo's Rageblade", "Hextech Gunblade"),
                "Kog'Maw", List.of("Statikk Shiv"),
                "Aurora", List.of(),
                "Gragas", List.of(),
                "Darius", List.of(),
                "Skarner", List.of()
        )));
        // Adicionar mais composições placeholder se necessário
        return comps;
    }

    public UIManager(CompositionAnalyzer compositionAnalyzer, GameStateTracker gameStateTracker, PositioningAssistant positioningAssistant) {
        this.compositionAnalyzer = compositionAnalyzer;
        this.gameStateTracker = gameStateTracker;
        this.positioningAssistant = positioningAssistant;
        System.out.println("UIManager inicializado.");
    }

    public void setupMainInterface(Stage primaryStage) {
        System.out.println("Configurando a interface principal...");

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-pane"); // Adicionar classe para estilizar raiz

        // --- Header (Topo) ---
        HBox header = new HBox();
        header.setPadding(new Insets(10, 20, 10, 20));
        header.setSpacing(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("header");

        Label titleLabel = new Label("TFT Assistant");
        titleLabel.getStyleClass().add("header-title");

        // Separador para empurrar controles para a direita (se necessário)
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Controles que ficavam no header antigo (manter por ora)
        Label maxSizeLabel = new Label("Tamanho Máx:");
        TextField maxSizeField = new TextField("8");
        maxSizeField.setPrefWidth(50);
        Button recommendButton = new Button("Recomendar"); // Nome menor
        Button positionButton = new Button("Posicionar"); // Nome menor

        header.getChildren().addAll(titleLabel, spacer, maxSizeLabel, maxSizeField, recommendButton, positionButton);
        root.setTop(header);

        // --- Menu Lateral Esquerdo ---
        VBox sideMenu = new VBox();
        sideMenu.setPadding(new Insets(15, 0, 15, 0)); // Padding vertical, sem horizontal
        sideMenu.setSpacing(5);
        sideMenu.getStyleClass().add("side-menu");
        sideMenu.setPrefWidth(180); // Um pouco mais largo

        // Botões do Menu (Exemplos)
        Button compButton = createMenuButton("Recomendador");
        Button posButton = createMenuButton("Posicionamento");
        Button trackerButton = createMenuButton("Tracker (WIP)");
        Button settingsButton = createMenuButton("Config (WIP)");

        // Adicionar ação ao botão principal (Recomendador)
        compButton.setOnAction(e -> showRecommenderView());
        // Adicionar ações aos outros botões depois

        sideMenu.getChildren().addAll(compButton, posButton, trackerButton, settingsButton);
        root.setLeft(sideMenu);

        // --- Área de Conteúdo Central (StackPane) ---
        centerContentArea = new StackPane();
        centerContentArea.getStyleClass().add("center-content-area");
        root.setCenter(centerContentArea);

        // Inicialmente, mostrar a view do Recomendador
        setupRecommenderView(); // Cria o SplitPane (seleção/resultado)
        showRecommenderView(); // Adiciona ao StackPane

        // --- Ações dos Botões do Header (recommend/position) ---
        // (Handlers permanecem os mesmos, mas talvez devessem ser movidos
        //  para lógicas específicas das views no futuro)
        recommendButton.setOnAction(event -> {
            // ... Lógica de recomendação ...
            // Atualiza lastRecommendedComp e chama updateResultsPane
            // updateResultsPane agora atualiza o conteúdo dentro do recommenderSplitPane
            try {
                int maxSize = Integer.parseInt(maxSizeField.getText());
                if (maxSize <= 0) { showError("Tamanho máximo deve ser positivo."); return; }
                List<Champion> selectedChampions = new ArrayList<>();
                if (selectedChampionNames.isEmpty()) { showError("Nenhum campeão selecionado!"); return; }
                for (String champName : selectedChampionNames) { selectedChampions.add(new Champion(champName)); }
                logger.info("Recomendando composição com {} campeões selecionados e tamanho {}", selectedChampions.size(), maxSize);
                if (compositionAnalyzer == null) { showError("Analyzer não inicializado."); return; }
                lastRecommendedComp = compositionAnalyzer.recommendBestComposition(selectedChampions, maxSize);
                Map<String, Integer> synergyLevels = compositionAnalyzer.calculateSynergies(lastRecommendedComp);
                Map<String, List<String>> recommendedItemNamesMap = new HashMap<>();
                for (Champion champ : lastRecommendedComp) { List<String> itemNames = compositionAnalyzer.recommendItems(champ, lastRecommendedComp); recommendedItemNamesMap.put(champ.getName(), itemNames); }
                updateResultsPane(lastRecommendedComp, synergyLevels, recommendedItemNamesMap);
            } catch (NumberFormatException ex) { showError("Tamanho máximo inválido: " + maxSizeField.getText());
            } catch (Exception ex) { logger.error("Erro inesperado ao recomendar composição", ex); showError("Erro inesperado. Verifique os logs."); }
        });
        positionButton.setOnAction(event -> {
             // ... Lógica de posicionamento ...
             // Modifica o currentResultsContainer dentro do recommenderSplitPane
            if (lastRecommendedComp == null || lastRecommendedComp.isEmpty()) { showError("Nenhuma composição recomendada para posicionar..."); return; }
            if (positioningAssistant == null) { showError("PositioningAssistant não inicializado."); return; }
            if (currentResultsContainer == null) { showError("Container de resultados não está pronto."); return; }
            try {
                logger.info("Solicitando sugestão de posicionamento para {} campeões.", lastRecommendedComp.size());
                List<ChampionPosition> positions = positioningAssistant.recommendPositioning(lastRecommendedComp, new ArrayList<>());
                logger.info("Posições recomendadas: {}", positions);
                currentResultsContainer.getChildren().removeIf(node -> node.getStyleClass().contains("positioning-area"));
                VBox positioningArea = new VBox(5); positioningArea.getStyleClass().add("positioning-area"); positioningArea.setPadding(new Insets(15, 0, 0, 0));
                Label title = new Label("Posicionamento Sugerido:"); title.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;"); positioningArea.getChildren().add(title);
                if (positions.isEmpty()) { Label msg = new Label("Nenhuma sugestão..."); msg.setStyle("-fx-text-fill: white;"); positioningArea.getChildren().add(msg); }
                else { for (ChampionPosition cp : positions) { String posText = String.format("%s: (%d, %d)", cp.getChampion().getName(), cp.getPosition().x, cp.getPosition().y); Label posLabel = new Label(posText); positioningArea.getChildren().add(posLabel); } }
                currentResultsContainer.getChildren().add(positioningArea);
            } catch (Exception ex) { logger.error("Erro ao sugerir posicionamento", ex); showError("Erro ao calcular sugestão de posicionamento."); }
        });

        // --- Configuração da Cena e Stage ---
        Scene scene = new Scene(root, 1200, 800);
        // Carregar CSS
        try {
            String cssPath = getClass().getResource("/styles.css").toExternalForm();
            if (cssPath != null) { scene.getStylesheets().add(cssPath); System.out.println("CSS carregado com sucesso de: " + cssPath); }
            else { System.err.println("Erro: Não foi possível encontrar styles.css"); }
        } catch (NullPointerException e) { System.err.println("Erro ao carregar styles.css..."); }

        primaryStage.setScene(scene);
        primaryStage.setTitle("TFT Assistant v0.1"); // Atualizar título
        System.out.println("Interface principal configurada.");
    }

    // Node que contém a view do Recomendador (Seleção + Resultado)
    private SplitPane recommenderSplitPane;

    /**
     * Cria o SplitPane para a view do recomendador.
     * Deve ser chamado uma vez durante a inicialização.
     */
    private void setupRecommenderView() {
        // Painel de Seleção de Campeões
        championSelectionPane = new TilePane();
        championSelectionPane.setPadding(new Insets(15));
        championSelectionPane.setHgap(10); championSelectionPane.setVgap(10);
        championSelectionPane.getStyleClass().add("champion-selection-pane");
        championSelectionPane.setPrefColumns(4);
        ScrollPane selectionScrollPane = new ScrollPane(championSelectionPane);
        selectionScrollPane.setFitToWidth(true);

        // Painel de Resultados (inicialização)
        // O VBox interno (currentResultsContainer) será criado/atualizado em updateResultsPane
        // O ScrollPane é o container externo
        resultsScrollPane = new ScrollPane(); // Inicializa vazio
        resultsScrollPane.setFitToWidth(true);
        resultsScrollPane.getStyleClass().add("results-scroll-pane");

        recommenderSplitPane = new SplitPane();
        recommenderSplitPane.getItems().addAll(selectionScrollPane, resultsScrollPane);
        recommenderSplitPane.setDividerPositions(0.4);
        recommenderSplitPane.getStyleClass().add("recommender-split-pane");

        // Popular o painel de seleção logo após criar a view
        populateChampionSelectionPane();
    }

    /**
     * Mostra a view do Recomendador na área de conteúdo central.
     */
    private void showRecommenderView() {
        if (recommenderSplitPane != null) {
            centerContentArea.getChildren().setAll(recommenderSplitPane);
            logger.debug("Mostrando view do Recomendador.");
        } else {
            logger.error("Tentativa de mostrar view do Recomendador antes de ser configurada.");
        }
    }

    /**
     * Cria um botão estilizado para o menu lateral.
     */
    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.getStyleClass().add("side-menu-button");
        // Adicionar ícone aqui se tivermos (new ImageView(...))
        // button.setGraphic(imageView);
        return button;
    }

    /**
     * Popula o painel de seleção de campeões com todos os campeões disponíveis.
     */
    private void populateChampionSelectionPane() {
        championSelectionPane.getChildren().clear();
        selectedChampionNames.clear();

        if (compositionAnalyzer == null || compositionAnalyzer.getChampionsDataMap() == null || compositionAnalyzer.getChampionTraitsMap() == null) {
            logger.error("Não é possível popular seleção: Analyzer ou dados não disponíveis.");
            championSelectionPane.getChildren().add(new Label("Erro ao carregar campeões."));
            return;
        }

        Map<String, ChampionData> allChampionsData = compositionAnalyzer.getChampionsDataMap();
        Map<String, List<String>> championTraits = compositionAnalyzer.getChampionTraitsMap();

        // Ordenar por nome, filtrando unidades não-campeãs (custo>0 e com traits)
        List<ChampionData> sortedChampions = allChampionsData.values().stream()
             .filter(champ -> champ != null &&
                              champ.getName() != null &&
                              champ.getCost() > 0 && // Precisa ter custo
                              championTraits.containsKey(champ.getName()) && // Precisa ter traits definidas
                              !championTraits.get(champ.getName()).isEmpty()) // Lista de traits não pode ser vazia
             .sorted(Comparator.comparing(ChampionData::getName))
             .collect(Collectors.toList());

        for (ChampionData champData : sortedChampions) {
             Node championNode = createChampionSelectableNode(champData);
             championSelectionPane.getChildren().add(championNode);
        }
    }

    /**
     * Verifica se uma unidade deve ser excluída da lista de seleção de campeões.
     * @param champData Dados da unidade.
     * @return true se deve ser excluída, false caso contrário.
     */
    private boolean isExcludedUnit(ChampionData champData) {
        String name = champData.getName();
        String apiName = champData.getApiName(); // Assumindo que apiName existe em ItemData

        if (name == null) return true; // Excluir se nome for nulo
        if (EXCLUDED_UNIT_NAMES_PATTERNS.contains(name)) {
            return true;
        }
        if (apiName != null) {
            for (String substring : EXCLUDED_API_NAME_SUBSTRINGS) {
                if (apiName.contains(substring)) {
                    return true;
                }
            }
        }
        // Poderia adicionar verificação de traits aqui se necessário,
        // mas a exclusão explícita pode ser suficiente.
        // if (!compositionAnalyzer.getChampionTraitsMap().containsKey(name)) return true;

        return false;
    }

    /**
     * Cria um nó visual (VBox) para um campeão que pode ser selecionado.
     * @param champData Dados do campeão.
     * @return Node representando o campeão selecionável.
     */
    private Node createChampionSelectableNode(ChampionData champData) {
        VBox champBox = new VBox(5);
        champBox.setAlignment(Pos.CENTER);
        ImageView champIcon = createImageView("/images/champions/" + formatNameForImage(champData.getName()) + ".png", 40);
        Label champLabel = new Label(champData.getName());
        champLabel.setStyle("-fx-text-fill: white; -fx-font-size: 10px;"); // Fonte menor
        champBox.getChildren().addAll(champIcon, champLabel);

        // Estilo inicial e de seleção
        String baseStyle = "-fx-border-color: #666; -fx-border-width: 1; -fx-padding: 5; -fx-background-radius: 5; -fx-border-radius: 5;";
        String selectedStyle = baseStyle + "-fx-background-color: #5c7a8c;"; // Cor de fundo para selecionado
        String deselectedStyle = baseStyle + "-fx-background-color: transparent;";

        champBox.setStyle(deselectedStyle);
        champBox.setUserData(champData.getName()); // Armazena o nome para fácil acesso no handler

        // Handler de clique para selecionar/desselecionar
        champBox.setOnMouseClicked(event -> {
            String champName = (String) champBox.getUserData();
            if (selectedChampionNames.contains(champName)) {
                selectedChampionNames.remove(champName);
                champBox.setStyle(deselectedStyle);
            } else {
                selectedChampionNames.add(champName);
                champBox.setStyle(selectedStyle);
            }
            logger.trace("Seleção atual: {}", selectedChampionNames);
        });

        return champBox;
    }

    /**
     * Atualiza o painel de resultados com a composição recomendada, suas sinergias e itens.
     * @param recommendedComp Lista de campeões recomendados.
     * @param synergyLevels Mapa de traits ativas e seus níveis (tier).
     * @param recommendedItemNamesMap Mapa do nome do campeão para sua lista de NOMES de itens recomendados.
     */
    private void updateResultsPane(List<Champion> recommendedComp, Map<String, Integer> synergyLevels, Map<String, List<String>> recommendedItemNamesMap) {
        // Criar ou limpar o container principal de resultados
        if (currentResultsContainer == null) {
            currentResultsContainer = new VBox(15);
            currentResultsContainer.setPadding(new Insets(15));
            currentResultsContainer.getStyleClass().add("results-container"); // Adicionar classe de estilo
        } else {
            currentResultsContainer.getChildren().clear(); // Limpa o conteúdo anterior
        }

        if (recommendedComp == null || recommendedComp.isEmpty()) {
            Label msg = new Label("Nenhuma composição pode ser formada com a seleção/tamanho.");
            msg.setStyle("-fx-text-fill: white;");
            currentResultsContainer.getChildren().add(msg); // Adiciona mensagem ao container
            resultsScrollPane.setContent(currentResultsContainer);
            return;
        }

        // --- Área de Sinergias Ativas ---
        VBox synergiesArea = new VBox(5);
        synergiesArea.setPadding(new Insets(0, 0, 15, 0)); // Espaçamento abaixo
        Label synergiesTitle = new Label("Sinergias Ativas:");
        synergiesTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        synergiesArea.getChildren().add(synergiesTitle);

        // Usar FlowPane para que as sinergias quebrem a linha
        FlowPane synergiesFlowPane = new FlowPane(Orientation.HORIZONTAL, 8, 5);

        // Ordenar sinergias (opcional, talvez por nome ou nível)
        List<Map.Entry<String, Integer>> sortedSynergies = synergyLevels.entrySet().stream()
             .sorted(Map.Entry.<String, Integer>comparingByValue().reversed() // Ordenar por nível (maior primeiro)
                    .thenComparing(Map.Entry.comparingByKey())) // Desempate por nome
             .collect(Collectors.toList());

        for (Map.Entry<String, Integer> entry : sortedSynergies) {
            String traitName = entry.getKey();
            int level = entry.getValue();

            HBox synergyBox = new HBox(5);
            synergyBox.setAlignment(Pos.CENTER_LEFT);

            // Ícone da Trait (Tentar carregar)
            ImageView traitIcon = createImageView("/images/traits/" + formatNameForImage(traitName) + ".png", 20); // Tamanho menor

            Label traitLabel = new Label(traitName + " (" + level + ")");
            traitLabel.setStyle("-fx-text-fill: white;");
            // TODO: Adicionar estilo baseado no nível (ex: cor diferente para tier máximo?)

            synergyBox.getChildren().addAll(traitIcon, traitLabel);
            synergyBox.setStyle("-fx-border-color: #777; -fx-border-width: 1; -fx-padding: 3 5 3 5; -fx-background-radius: 3; -fx-border-radius: 3; -fx-background-color: #555;"); // Estilo para destacar
            synergiesFlowPane.getChildren().add(synergyBox);
        }
        synergiesArea.getChildren().add(synergiesFlowPane);

        currentResultsContainer.getChildren().add(synergiesArea);

        // --- Área de Campeões (com Itens) ---
        TilePane championsPane = new TilePane();
        championsPane.setHgap(15);
        championsPane.setVgap(15);

        for (Champion champ : recommendedComp) {
            VBox champDisplay = new VBox(5);
            champDisplay.setAlignment(Pos.CENTER);
            ImageView champIcon = createImageView("/images/champions/" + formatNameForImage(champ.getName()) + ".png", 50);
            Label champLabel = new Label(champ.getName());
            champLabel.setStyle("-fx-text-fill: white;");

            // Área para Itens Recomendados
            HBox itemsBox = new HBox(3);
            itemsBox.setAlignment(Pos.CENTER);
            itemsBox.setMinHeight(22);

            // Buscar NOMES dos itens
            List<String> itemNames = recommendedItemNamesMap.getOrDefault(champ.getName(), List.of());
            for (String itemName : itemNames) { // Iterar sobre nomes (String)
                if (itemName != null && !itemName.isEmpty()) {
                    // Usar itemName diretamente para buscar imagem
                    ImageView itemIcon = createImageView("/images/items/" + formatNameForImage(itemName) + ".png", 20);
                    itemsBox.getChildren().add(itemIcon);
                }
            }

            // Adiciona ícone, nome e itens ao VBox do campeão
            champDisplay.getChildren().addAll(champIcon, champLabel, itemsBox);
            champDisplay.setStyle("-fx-border-color: #555; -fx-border-width: 1; -fx-padding: 5; -fx-background-radius: 5; -fx-border-radius: 5;");
            championsPane.getChildren().add(champDisplay);
        }
        currentResultsContainer.getChildren().add(championsPane); // Adiciona o painel de campeões ao container

        // Define o VBox (agora referenciado por currentResultsContainer) como conteúdo do ScrollPane da direita
        resultsScrollPane.setContent(currentResultsContainer);
    }

     /**
     * Exibe uma mensagem de erro para o usuário usando um diálogo de Alerta.
     * @param message Mensagem de erro a ser exibida.
     */
    private void showError(String message) {
        logger.error("Erro UI: {}", message); // Mantém o log do erro

        // Cria e configura o Alerta
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText("Ocorreu um erro"); // Cabeçalho simples
        alert.setContentText(message);

        // Mostra o alerta e espera o usuário fechar
        alert.showAndWait();
    }

    // --- Criação do Cartão de Composição (Helper Method - não usado mais para recomendação) ---
    private VBox createCompCard(PlaceholderComp compData) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(10));
        card.getStyleClass().add("comp-card");

        // Nome e Tier
        HBox titleBox = new HBox(10);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        Label nameLabel = new Label(compData.name);
        nameLabel.getStyleClass().add("comp-name");
        Label tierLabel = new Label("[" + compData.tier + "]");
        tierLabel.getStyleClass().add("comp-tier-" + compData.tier.toLowerCase());
        Label playstyleLabel = new Label("(" + compData.playstyle + ")");
        playstyleLabel.getStyleClass().add("comp-playstyle");
        titleBox.getChildren().addAll(nameLabel, tierLabel, playstyleLabel);

        // Campeões e Itens (Grid)
        GridPane championsGrid = new GridPane();
        championsGrid.setHgap(8);
        championsGrid.setVgap(5);

        int col = 0;
        int row = 0;
        for (Map.Entry<String, List<String>> entry : compData.championsAndItems.entrySet()) {
            String champName = entry.getKey();
            List<String> items = entry.getValue();

            VBox championSlot = new VBox(2);
            championSlot.setAlignment(Pos.CENTER);

            // Carregar imagem do campeão
            ImageView champIcon = createImageView("/images/champions/" + formatNameForImage(champName) + ".png", 40);

            Label champLabel = new Label(champName);
            champLabel.getStyleClass().add("champ-label");

            // Itens
            HBox itemsBox = new HBox(3);
            itemsBox.setAlignment(Pos.CENTER);
            itemsBox.setMinHeight(15); // Garante espaço mesmo sem itens
            for(String itemName : items) {
                 // Carregar imagem do item
                 ImageView itemIcon = createImageView("/images/items/" + formatNameForImage(itemName) + ".png", 15);
                 itemsBox.getChildren().add(itemIcon);
            }

            championSlot.getChildren().addAll(champIcon, champLabel, itemsBox);
            championsGrid.add(championSlot, col, row);

            col++;
            if (col >= 4) { // Limitar a 4 campeões por linha
                col = 0;
                row++;
            }
        }

        card.getChildren().addAll(titleBox, championsGrid);
        return card;
    }

    // Helper para formatar nome para buscar imagem (substitui espaço, remove apóstrofo)
    private String formatNameForImage(String name) {
        return name.replace(" ", "_").replace("'", "");
    }

    // Helper para criar ImageView com placeholder
    private ImageView createImageView(String resourcePath, double size) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setPreserveRatio(true);

        try (InputStream stream = getClass().getResourceAsStream(resourcePath)) {
            if (stream != null) {
                imageView.setImage(new Image(stream));
            } else {
                // Imagem não encontrada, usar placeholder
                System.err.println("Imagem não encontrada: " + resourcePath);
                imageView.setImage(createPlaceholderImage(size)); // Usa o placeholder
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagem: " + resourcePath + " - " + e.getMessage());
            imageView.setImage(createPlaceholderImage(size)); // Usa o placeholder em caso de erro
        }
        return imageView;
    }

    // Helper para criar uma imagem placeholder (retângulo colorido)
    private Image createPlaceholderImage(double size) {
        // Cria um WritableImage e desenha um retângulo nele
        // Para simplificar, vamos retornar null e deixar o ImageView vazio ou com estilo
        // Alternativa: Retornar uma imagem padrão pequena ou usar CSS para fundo
        return null; // O ImageView ficará vazio se a imagem não carregar
        // Poderia retornar: new WritableImage((int)size, (int)size); e desenhar algo
    }

    // Métodos antigos createCompositionPanel e createGameStatePanel removidos
    // Serão incorporados na interface principal ou em componentes separados depois.
}