FROM registry.access.redhat.com/ubi8/openjdk-17@sha256:e8cc2e476282b75d89c73057bfa713db22d72bdb2808d62d981a84c33beb2575 as builder

WORKDIR /opt/crm

COPY --chown=jboss: . .

RUN ./gradlew assemble

FROM registry.access.redhat.com/ubi8/openjdk-17@sha256:e8cc2e476282b75d89c73057bfa713db22d72bdb2808d62d981a84c33beb2575 as runtime

WORKDIR /opt/crm

COPY --from=builder /opt/crm/build/libs/*.jar . 
ENTRYPOINT ["java", "-jar" , "crm-1.1.0.jar"] 
