package com.slb.grpc.myapi;

import com.google.common.base.Stopwatch;
import com.google.protobuf.Empty;
import io.grpc.*;
import io.grpc.stub.MetadataUtils;
import org.apache.commons.cli.*;

import java.nio.ByteBuffer;
import java.time.Duration;

public class MyClient {

    private static Metadata.Key<String> API_KEY_HEADER =
            Metadata.Key.of("x-api-key", Metadata.ASCII_STRING_MARSHALLER);

    public static void main(String[] args) throws Exception {

        Options options = new Options();
        options.addOption(Option.builder().longOpt("host").optionalArg(true).hasArg().desc("host").build());
        options.addOption(Option.builder().longOpt("port").optionalArg(true).hasArg().desc("port").build());
        options.addOption(Option.builder().longOpt("key").optionalArg(true).hasArg().desc("API key").build());
        options.addOption(Option.builder().longOpt("requests").optionalArg(true).hasArg().desc("Number of requests").build());
        options.addOption(Option.builder().longOpt("payload").optionalArg(true).hasArg().desc("Payload in bytes").build());

        CommandLineParser parser = new DefaultParser();
        CommandLine line = parser.parse(options, args);

        String host = line.getOptionValue("host", "localhost");
        int port = Integer.parseInt(line.getOptionValue("port", "8000"));
        String apiKey = line.getOptionValue("key", "undefined");
        int requestCount = Integer.parseInt(line.getOptionValue("requests", "1"));
        int payload = Integer.parseInt(line.getOptionValue("payload", "0"));

        Channel channel = ManagedChannelBuilder.forAddress(host, port)
                .directExecutor()
                .usePlaintext()
                .build();

        Metadata headers = new Metadata();
        headers.put(API_KEY_HEADER, apiKey);

        MyServiceGrpc.MyServiceBlockingStub blockingStub = MyServiceGrpc.newBlockingStub(channel);
        blockingStub = MetadataUtils.attachHeaders(blockingStub, headers);

        Stopwatch stopwatch = Stopwatch.createStarted();
        if (payload == 0) {
            System.out.println("0 byte payload, using method 'ping'.");
            Empty request = Empty.getDefaultInstance();
            for (int i = 1; i <= requestCount; i++) {
                blockingStub.ping(request);
                if (i % 5000 == 0)
                    System.out.format("Avg latency: %f\n", (float)stopwatch.elapsed().toMillis()/i);
            }

        } else {
            MyApiProto.PayloadRequest request = MyApiProto.PayloadRequest.newBuilder().setSize(payload).build();

            for (int i = 1; i <= requestCount; i++) {
                MyApiProto.PayloadResponse response = blockingStub.getPayload(request);

                if (i % 5000 == 0) {
                    double mb = (float)i*response.getPayload().size()/(1024*1024);
                    System.out.format("Avg latency: %f, %f MB/s\n",
                            (float)stopwatch.elapsed().toMillis() / i,
                            (float)mb*1000/stopwatch.elapsed().toMillis());

                }
            }
        }

        Duration elapsed = stopwatch.elapsed();
        System.out.format("Request count: %d\n", requestCount);
        System.out.format("Avg latency: %fms\n", (double)elapsed.toMillis()/requestCount);
    }
}