FROM registry.access.redhat.com/ubi8/openjdk-17@sha256:e8cc2e476282b75d89c73057bfa713db22d72bdb2808d62d981a84c33beb2575 as builder

WORKDIR /home/jboss

COPY --chown=jboss: . .

RUN ./gradlew build -x test 

FROM registry.access.redhat.com/ubi8/openjdk-17@sha256:e8cc2e476282b75d89c73057bfa713db22d72bdb2808d62d981a84c33beb2575 as runtime

WORKDIR /home/jboss

RUN mkdir ./pdf

COPY --chown=jboss: --from=builder /home/jboss/build/libs/*.jar . 
ENTRYPOINT ["java", "-jar" , "crm-1.1.0.jar"] 
