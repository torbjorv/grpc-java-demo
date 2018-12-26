package torbjorv.grpc;

import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.logging.Logger;

public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    /* The port on which the server should run */
    private int port = 8000;
    private io.grpc.Server server;

    private void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new MyServiceImpl())
                .directExecutor()
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                Server.this.stop();
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
        final Server server = new Server();
        server.start();
        server.blockUntilShutdown();
    }
}
