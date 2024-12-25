package com.tftassistant.app;

import com.tftassistant.core.*;
import com.tftassistant.api.RiotApiConnector;
import com.tftassistant.ui.UIManager;
import com.tftassistant.util.ScreenCaptureModule;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TFTAssistantApp extends Application {
    private static final Logger logger = LoggerFactory.getLogger(TFTAssistantApp.class);
    private UIManager uiManager;

    @Override
    public void start(Stage primaryStage) {
        try {
            logger.info("Iniciando TFT Assistant...");
            initializeModules();
            uiManager.setupMainInterface(primaryStage);
            primaryStage.setTitle("TFT Assistant");
            primaryStage.show();
            logger.info("TFT Assistant iniciado com sucesso");
        } catch (Exception e) {
            logger.error("Erro ao iniciar aplicação", e);
            showErrorAndExit("Erro ao iniciar aplicação", e);
        }
    }

    private void initializeModules() {
        ScreenCaptureModule screenCaptureModule = new ScreenCaptureModule();
        RiotApiConnector apiConnector = new RiotApiConnector(ConfigLoader.getApiKey());
        CompositionAnalyzer compositionAnalyzer = new CompositionAnalyzer(apiConnector);
        GameStateTracker gameStateTracker = new GameStateTracker(screenCaptureModule, compositionAnalyzer);
        PositioningAssistant positioningAssistant = new PositioningAssistant();
        uiManager = new UIManager(compositionAnalyzer, gameStateTracker, positioningAssistant);
    }

    public static void main(String[] args) {
        launch(args);
    }
}