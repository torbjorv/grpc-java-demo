package com.slb.grpc.myapi;

import com.google.common.collect.Lists;
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

    @Override
    public void listFish(MyApiProto.FishyRequest request, StreamObserver<MyApiProto.FishyResponse> responseObserver) {

        MyApiProto.FishyResponse response = MyApiProto.FishyResponse.newBuilder()
                .addAllFishNames(Lists.newArrayList("cod", "shark", "moby dick"))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
