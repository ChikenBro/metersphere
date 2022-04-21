FROM openjdk:8-jdk-alpine
WORKDIR /workspace/app

COPY backend/target/*.jar .

RUN mkdir -p dependency && (cd dependency; jar -xf ../*.jar)


COPY --from=hengyunabc/arthas:latest /opt/arthas /opt/arthas
LABEL maintainer="FIT2CLOUD <support@fit2cloud.com>"

ARG MS_VERSION=dev
ARG DEPENDENCY=/workspace/app/dependency

RUN mkdir -p /app

RUN cp -r ${DEPENDENCY}/BOOT-INF/lib/* /app/lib
RUN cp -r ${DEPENDENCY}/META-INF/* /app/META-INF
RUN cp -r ${DEPENDENCY}/BOOT-INF/classes/* /app

COPY --from=metersphere/fabric8-java-alpine-openjdk8-jre /app/jmeter /opt/
COPY --from=metersphere/fabric8-java-alpine-openjdk8-jre /deployments/run-java.sh /deployments/run-java.sh
RUN mkdir -p /opt/jmeter/lib/junit

ENV FORMAT_MESSAGES_PATTERN_DISABLE_LOOKUPS=true
ENV JAVA_CLASSPATH=/app:/workspace/app/dependency/BOOT-INF/lib/*
ENV JAVA_MAIN_CLASS=io.metersphere.Application
ENV AB_OFF=true
ENV MS_VERSION=${MS_VERSION}
ENV JAVA_OPTIONS="-Dfile.encoding=utf-8 -Djava.awt.headless=true  -Dlog4j2.formatMsgNoLookups=true"

CMD ["/deployments/run-java.sh"]