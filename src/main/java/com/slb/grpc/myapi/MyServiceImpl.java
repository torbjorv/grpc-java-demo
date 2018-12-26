package com.slb.grpc.myapi;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;

import java.util.Map;


public class MyServiceImpl extends MyServiceGrpc.MyServiceImplBase {

    Map<Integer, MyApiProto.PayloadResponse> cache = Maps.newHashMap();

    @Override
    public void ping(Empty request, StreamObserver<Empty> responseObserver) {
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void getPayload(MyApiProto.PayloadRequest request, StreamObserver<MyApiProto.PayloadResponse> responseObserver) {

        if (!cache.containsKey(request.getSize())) {
            byte[] payload = new byte[request.getSize()];
            for (int i = 0; i < request.getSize(); i++)
                payload[i] = (byte)i;

            MyApiProto.PayloadResponse response = MyApiProto.PayloadResponse.newBuilder()
                    .setPayload(ByteString.copyFrom(payload))
                    .build();

            cache.put(request.getSize(), response);
        }

        responseObserver.onNext(cache.get(request.getSize()));
        responseObserver.onCompleted();
    }
}
