
[![CodeQL Advanced](https://github.com/Bulletdev/TFT-Assistant/actions/workflows/codeql.yml/badge.svg)](https://github.com/Bulletdev/TFT-Assistant/actions/workflows/codeql.yml)
<p align="center"> 
  
  <img alt="GitHub top language" src="https://img.shields.io/github/languages/top/Bulletdev/TFT-Assistant?color=04D361&labelColor=000000">
  
  <a href="https://www.linkedin.com/in/Michael-Bullet/">
    <img alt="Made by" src="https://img.shields.io/static/v1?label=made%20by&message=Michael%20Bullet&color=04D361&labelColor=000000">
  </a> 
  
  <img alt="Repository size" src="https://img.shields.io/github/repo-size/bulletdev/TFT-Assistant?color=04D361&labelColor=000000">
  
  <a href="https://github.com/Bulletdev/TFT-Assistant/commits/master">
    <img alt="GitHub last commit" src="https://img.shields.io/github/last-commit/bulletdev/TFT-Assistant?color=04D361&labelColor=000000">
  </a>

  
</p>

  

<div>
<p align="center"> 
  <img alt="screenshot" src="img.png" width="1920" height="400">
</p>

<div align="center"> <h1>TFT ASSISTANT</h1> </div>

<div>
<p align="center"> 
  <img a src="/public/tftlogo.png" width="90" height="90">
</p>

## <img alt="screenshot" src="public/TFT_Gold.png" width="20" height="20"> Descrição

- Uma ferramenta de assistência para Teamfight Tactics (TFT) desenvolvida em Java, com funcionalidades avançadas para melhorar sua performance no jogo.

### Funcionalidades Principais

> Recomendação de Composições
 
> Análise de Sinergias

> Sugestão de Itens
 
> Captura e Análise de Estado do Jogo

> Assistente de Posicionamento

> Histórico de Partidas

## <img alt="screenshot" src="public/TFT_Gold.png" width="20" height="20"> Pré-requisitos

> Java 17+

> Maven

> Tesseract OCR

> Chave de API da Riot Games

## <img alt="screenshot" src="public/TFT_Gold.png" width="20" height="20"> Configuração

### 1. Instalação de Dependências

# Instalar Tesseract OCR


# No Ubuntu/Debian

```
sudo apt-get install tesseract-ocr
```

# No macOS com Homebrew

```
brew install tesseract
```

# No Windows, baixe o instalador do site oficial

https://github.com/UB-Mannheim/tesseract/wiki


2. Configuração do Projeto

> Clone o repositório

````
git clone https://github.com/bulletdev/tft-assistant.git
````

````
cd tft-assistant
````

## <img alt="screenshot" src="public/TFT_Gold.png" width="20" height="20"> Configure a Chave da API da Riot


> Crie um arquivo config.properties na raiz do projeto

RIOT_API_KEY=sua_chave_api_aqui

https://developer.riotgames.com

## Compile e Execute


````
mvn clean package
````
# Executar a aplicação

````
mvn javafx:run
````
## <img alt="screenshot" src="public/TFT_Gold.png" width="20" height="20"> Uso

> Abra o TFT

> Inicie o TFT Assistant

> A ferramenta irá capturar e analisar automaticamente o estado do jogo

## <img alt="screenshot" src="public/TFT_Gold.png" width="20" height="20"> Contribuição

Faça um fork do projeto

- Crie uma branch para sua feature (git checkout -b feature/nova-feature)
- Commit suas mudanças (git commit -m 'Adiciona nova feature')
- Push para a branch (git push origin feature/nova-feature)
- Abra um Pull Request

## <img alt="screenshot" src="public/TFT_Gold.png" width="20" height="20">  Limitações e Avisos

- Requer permissão de captura de tela
- Precisão depende da qualidade da captura
- Respeite os Termos de Serviço da Riot Games
