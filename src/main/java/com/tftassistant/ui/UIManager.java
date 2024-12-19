
class UIManager {
    private CompositionAnalyzer compositionAnalyzer;
    private GameStateTracker gameStateTracker;

    public UIManager(CompositionAnalyzer compositionAnalyzer,
                     GameStateTracker gameStateTracker) {
        this.compositionAnalyzer = compositionAnalyzer;
        this.gameStateTracker = gameStateTracker;
    }

    public void setupMainInterface(Stage primaryStage) {
        // Configurar a interface principal do JavaFX
        // Incluir painéis para:
        // - Recomendação de composição
        // - Estado atual do jogo
        // - Histórico de partidas
        // - Sugestões de posicionamento
    }

    private void createCompositionPanel() {
        // Painel para mostrar composições recomendadas
    }

    private void createGameStatePanel() {
        // Painel para mostrar estado atual do jogo
    }
}