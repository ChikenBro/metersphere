FROM openjdk:8-jdk-alpine as build
WORKDIR /app

COPY backend/target/*.jar .

RUN mkdir -p dependency && (cd dependency; jar -xf ../*.jar)


ARG MS_VERSION=dev



COPY --from=hengyunabc/arthas:latest /opt/arthas /opt/arthas

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone && apk add --no-cache tini

RUN mkdir -p /deployments
COPY --from=metersphere/fabric8-java-alpine-openjdk8-jre:latest /deployments/ /deployments/



RUN mv /app/dependency/BOOT-INF/classes/jmeter /opt/
RUN mkdir -p /opt/jmeter/lib/junit



ENV FORMAT_MESSAGES_PATTERN_DISABLE_LOOKUPS=true
ENV JAVA_CLASSPATH=/app/dependency/BOOT-INF/classes:/app/dependency/BOOT-INF/lib/*
ENV JAVA_MAIN_CLASS=io.metersphere.Application
ENV AB_OFF=true
ENV MS_VERSION=${MS_VERSION}
ENV JAVA_OPTIONS="-Dfile.encoding=utf-8 -Djava.awt.headless=true  -Dlog4j2.formatMsgNoLookups=true"

CMD ["java -Dfile.encoding=utf-8 -Djava.awt.headless=true -Dlog4j2.formatMsgNoLookups=true -Xss5m -XX:+UseParallelGC -XX:GCTimeRatio=4 -XX:AdaptiveSizePolicyWeight=90 -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=40 -XX:+ExitOnOutOfMemoryError -cp /app/dependency/BOOT-INF/classes:/app/dependency/BOOT-INF/lib/* io.metersphere.Application"]