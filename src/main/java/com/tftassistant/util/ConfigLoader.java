package com.tftassistant.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "config.properties"; // Nome do arquivo de configuração

    static {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
            System.out.println("Arquivo de configuração carregado.");
        } catch (IOException ex) {
            System.err.println("Erro ao carregar arquivo de configuração: " + CONFIG_FILE);
            // Considerar lançar uma exceção ou usar valores padrão
            // ex.printStackTrace();
        }
    }

    public static String getApiKey() {
        // Retorna a chave da API ou um valor padrão/null se não encontrada
        return properties.getProperty("riot.api.key", "SUA_CHAVE_API_AQUI"); // Chave padrão para evitar null
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    // Outros métodos para obter configurações específicas podem ser adicionados aqui
} 