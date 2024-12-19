package com.tftassistant.util;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.image.BufferedImage;

public class OCRProcessor {
    private static final Logger logger = LoggerFactory.getLogger(OCRProcessor.class);
    private final Tesseract tesseract;

    public OCRProcessor() {
        tesseract = new Tesseract();
        initializeTesseract();
    }

    private void initializeTesseract() {
        try {
            tesseract.setDatapath("data/tessdata");
            tesseract.setLanguage("eng");
            tesseract.setPageSegMode(1);
            tesseract.setOcrEngineMode(1);
        } catch (Exception e) {
            logger.error("Erro ao inicializar Tesseract", e);
            throw new RuntimeException("Falha ao inicializar OCR", e);
        }
    }

    public String processImage(BufferedImage image) throws TesseractException {
        try {
            return tesseract.doOCR(image);
        } catch (TesseractException e) {
            logger.error("Erro ao processar imagem com OCR", e);
            throw e;
        }
    }
}