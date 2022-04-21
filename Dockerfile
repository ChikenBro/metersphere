FROM openjdk:8-jdk-alpine as build
WORKDIR /workspace/app

COPY backend/target/*.jar .

RUN mkdir -p dependency && (cd dependency; jar -xf ../*.jar)

FROM metersphere/fabric8-java-alpine-openjdk8-jre as build1

COPY --from=hengyunabc/arthas:latest /opt/arthas /opt/arthas
LABEL maintainer="FIT2CLOUD <support@fit2cloud.com>"

ARG MS_VERSION=dev
ARG DEPENDENCY=/workspace/app/dependency

COPY --from=build1 /deployments/run-java.sh /deployments/run-java.sh

COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
FROM openjdk:8-jdk-alpine

COPY --from=build1 /app/jmeter /opt/

RUN mkdir -p /opt/jmeter/lib/junit

ENV FORMAT_MESSAGES_PATTERN_DISABLE_LOOKUPS=true
ENV JAVA_CLASSPATH=/app:/app/lib/*
ENV JAVA_MAIN_CLASS=io.metersphere.Application
ENV AB_OFF=true
ENV MS_VERSION=${MS_VERSION}
ENV JAVA_OPTIONS="-Dfile.encoding=utf-8 -Djava.awt.headless=true  -Dlog4j2.formatMsgNoLookups=true"

CMD ["/deployments/run-java.sh"]