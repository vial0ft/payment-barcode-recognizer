# syntax = docker/dockerfile:1.2
FROM clojure:temurin-19-tools-deps-1.11.1.1208-bullseye-slim AS build

WORKDIR /
COPY . /

RUN apt-get update && apt-get -y install nodejs npm

RUN clj -Sforce -T:build all

FROM azul/zulu-openjdk-alpine:17

COPY --from=build /target/payment-barcode-recognizer-standalone.jar /payment-barcode-recognizer/payment-barcode-recognizer-standalone.jar

EXPOSE $PORT

ENTRYPOINT exec java $JAVA_OPTS -jar /payment-barcode-recognizer/payment-barcode-recognizer-standalone.jar
