FROM eclipse-temurin:21-jre AS java-runtime
FROM scipoptsuite/scipoptsuite@sha256:4c351e5c15dc70f8a6fa495a23d7b9a32448eefd89b229a5544ede610d3c352d
COPY --from=java-runtime /opt/java/openjdk /opt/java/openjdk
ENV JAVA_HOME=/opt/java/openjdk PATH="/opt/java/openjdk/bin:${PATH}"
COPY target/srv.optimization.services-*.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
