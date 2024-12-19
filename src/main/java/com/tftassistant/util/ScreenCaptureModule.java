class ScreenCaptureModule {
    private Robot robot;
    private Tesseract tesseract;

    public ScreenCaptureModule() {
        try {
            robot = new Robot();
            tesseract = new Tesseract();
            tesseract.setDatapath("path/to/tessdata"); // Configure caminho do Tesseract
        } catch (AWTException e) {
            throw new RuntimeException("Erro ao inicializar captura de tela", e);
        }
    }

    public BufferedImage captureScreen() {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        return robot.createScreenCapture(screenRect);
    }

    public String extractTextFromScreen(BufferedImage screenshot) {
        try {
            return tesseract.doOCR(screenshot);
        } catch (TesseractException e) {
            throw new RuntimeException("Erro ao extrair texto da imagem", e);
        }
    }
}
