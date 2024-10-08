FROM openjdk:17-jdk
VOLUME /tmp
COPY /arthas-bin.zip /opts/arthas-bin.zip
ADD jiushi-live-user-provider-docker.jar app.jar
ENTRYPOINT java   -Djava.security.egd=file:/dev/./urandom --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.util.concurrent=ALL-UNNAMED --add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.math=ALL-UNNAMED -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:12000 -jar app.jar