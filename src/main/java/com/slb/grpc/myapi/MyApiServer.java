package com.slb.grpc.myapi;

import com.google.protobuf.Empty;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.logging.Logger;

public class MyApiServer {
    private static final Logger logger = Logger.getLogger(MyApiServer.class.getName());

    /* The port on which the server should run */
    private int port = 8000;
    private Server server;

    private void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new MyApiImpl())
                .directExecutor()
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                MyApiServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final MyApiServer server = new MyApiServer();
        server.start();
        server.blockUntilShutdown();
    }

    private class MyApiImpl extends MyApiGrpc.MyApiImplBase {

        @Override
        public void ping(Empty request, StreamObserver<MyApiProto.HealthCheckResponse> responseObserver) {
            MyApiProto.HealthCheckResponse response = MyApiProto.HealthCheckResponse
                    .newBuilder()
                    .setStatus("ok")
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
