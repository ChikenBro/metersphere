FROM openjdk:8-jdk-alpine 
WORKDIR /workspace/app

COPY backend/target/*.jar .

RUN mkdir -p dependency && (cd dependency; jar -xf ../*.jar)


ARG MS_VERSION=dev

ARG DEPENDENCY=/workspace/app/dependency


COPY --from=hengyunabc/arthas:latest /opt/arthas /opt/arthas

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone && apk add --no-cache tini

RUN mkdir -p /deployments
COPY --from=metersphere/fabric8-java-alpine-openjdk8-jre:latest /deployments/ /deployments/

RUN mv ${DEPENDENCY}/BOOT-INF/lib /app/lib
RUN mv ${DEPENDENCY}/META-INF /app/META-INF
RUN mv ${DEPENDENCY}/BOOT-INF/classes /app

RUN mv /app/jmeter /opt/
RUN mkdir -p /opt/jmeter/lib/junit



ENV FORMAT_MESSAGES_PATTERN_DISABLE_LOOKUPS=true
ENV JAVA_CLASSPATH=/app:/app/lib/*
ENV JAVA_MAIN_CLASS=io.metersphere.Application
ENV AB_OFF=true
ENV MS_VERSION=${MS_VERSION}
ENV JAVA_OPTIONS="-Dfile.encoding=utf-8 -Djava.awt.headless=true  -Dlog4j2.formatMsgNoLookups=true"

CMD ["/deployments/run-java.sh"]