FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:17-slim
WORKDIR /app

RUN apt-get update && apt-get install -y \
    tesseract-ocr \
    libgl1-mesa-glx \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/tft-assistant-*.jar /app/tft-assistant.jar

COPY config.properties /app/config.properties
COPY data /app/data

ENV DISPLAY=:0
ENV RIOT_API_KEY=""

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/tft-assistant.jar"]