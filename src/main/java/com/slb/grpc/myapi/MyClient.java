package com.slb.grpc.myapi;

import com.google.protobuf.Empty;
import io.grpc.*;
import org.apache.commons.cli.*;
import org.omg.PortableInterceptor.Interceptor;

import java.util.concurrent.TimeUnit;

public class MyClient {

    public static void main(String[] args) throws Exception {

        Channel channel = getChannel(args);

        MyServiceGrpc.MyServiceBlockingStub blockingStub = MyServiceGrpc.newBlockingStub(channel);
        blockingStub.ping(Empty.getDefaultInstance());
    }

    static private Channel getChannel(String[] args) throws ParseException {
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
                .usePlaintext(true)
                .build();

        channel = ClientInterceptors.intercept(channel,  new ApiKeyInterceptor(apiKey, ""));
        return channel;
    }

    private static final class ApiKeyInterceptor implements ClientInterceptor {
        private final String apiKey;
        private final String authToken;

        private static Metadata.Key<String> API_KEY_HEADER =
                Metadata.Key.of("x-api-key", Metadata.ASCII_STRING_MARSHALLER);
        private static Metadata.Key<String> AUTHORIZATION_HEADER =
                Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);

        public ApiKeyInterceptor(String apiKey, String authToken) {
            this.apiKey = apiKey;
            this.authToken = authToken;
        }

        @Override
        public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
                MethodDescriptor<ReqT, RespT> method,
                CallOptions callOptions,
                Channel next) {

            ClientCall<ReqT, RespT> call = next.newCall(method, callOptions);

            call = new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(call) {
                @Override
                public void start(Listener<RespT> responseListener, Metadata headers) {

                    if (apiKey != null && !apiKey.isEmpty()) {
                        headers.put(API_KEY_HEADER, apiKey);
                    }

                    if (authToken != null && !authToken.isEmpty()) {
                        headers.put(AUTHORIZATION_HEADER, "Bearer " + authToken);
                    }
                    super.start(responseListener, headers);
                }
            };
            return call;
        }
    }
}