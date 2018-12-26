package com.slb.grpc.myapi;

import com.google.protobuf.Empty;
import io.grpc.*;
import io.grpc.stub.MetadataUtils;
import org.apache.commons.cli.*;

public class MyClient {

    private static Metadata.Key<String> API_KEY_HEADER =
            Metadata.Key.of("x-api-key", Metadata.ASCII_STRING_MARSHALLER);

    public static void main(String[] args) throws Exception {

        Options options = new Options();
        options.addOption(Option.builder().longOpt("host").optionalArg(false).hasArg().desc("host").build());
        options.addOption(Option.builder().longOpt("port").optionalArg(false).hasArg().desc("port").build());
        options.addOption(Option.builder().longOpt("key").optionalArg(false).hasArg().desc("API key").build());
        CommandLineParser parser = new DefaultParser();
        CommandLine line = parser.parse(options, args);

        String host = line.getOptionValue("host");
        int port = Integer.parseInt(line.getOptionValue("port"));
        String apiKey = line.getOptionValue("key");


        Channel channel = ManagedChannelBuilder.forAddress(host, port)
                .directExecutor()
                .usePlaintext()
                .build();
        
        Metadata headers = new Metadata();
        headers.put(API_KEY_HEADER, apiKey);

        MyServiceGrpc.MyServiceBlockingStub blockingStub = MyServiceGrpc.newBlockingStub(channel);
        blockingStub = MetadataUtils.attachHeaders(blockingStub, headers);

        MyApiProto.HealthCheckResponse response = blockingStub.ping(Empty.getDefaultInstance());
        System.out.println(response.toString());
    }
}