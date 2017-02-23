FROM openjdk:8

EXPOSE 8000

ADD ./target/myapi-1.0.0-jar-with-dependencies.jar .
ENTRYPOINT java -cp myapi-1.0.0-jar-with-dependencies.jar com.slb.grpc.myapi.MyApiServer