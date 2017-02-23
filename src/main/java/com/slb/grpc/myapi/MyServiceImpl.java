package com.slb.grpc.myapi;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;


public class MyServiceImpl extends MyServiceGrpc.MyServiceImplBase {

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
