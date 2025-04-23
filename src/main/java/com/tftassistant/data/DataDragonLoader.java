package com.tftassistant.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tftassistant.data.cdragon.CommunityDragonData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DataDragonLoader {

    private static final Logger logger = LoggerFactory.getLogger(DataDragonLoader.class);
    private static final String CDRAGON_URL = "https://raw.communitydragon.org/latest/cdragon/tft/en_us.json";
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public DataDragonLoader() {
        this.objectMapper = new ObjectMapper(); // Usar um ObjectMapper compartilhado seria melhor
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public CommunityDragonData loadData() {
        logger.info("Carregando dados do Community Dragon de: {}", CDRAGON_URL);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CDRAGON_URL))
                .GET()
                .build();

        try {
            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() == 200) {
                try (InputStream responseBody = response.body()) {
                    CommunityDragonData data = objectMapper.readValue(responseBody, CommunityDragonData.class);
                    logger.info("Dados do Community Dragon carregados com sucesso.");
                    return data;
                }
            } else {
                logger.error("Falha ao carregar dados do Community Dragon. Código de status: {}", response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Erro ao fazer requisição ou processar dados do Community Dragon", e);
            Thread.currentThread().interrupt(); // Restaura o status de interrupção
            return null;
        }
    }

    // Poderíamos adicionar métodos aqui para extrair dados de um set específico,
    // mas vamos deixar isso para o CompositionAnalyzer por enquanto.
} 