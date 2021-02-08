package com.jgainey.loggo.nozzles;


import com.jgainey.loggo.generated.*;
import com.jgainey.loggo.objs.V2NozzleData;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth;
import io.grpc.stub.StreamObserver;

import javax.net.ssl.SSLException;
import java.io.*;
import java.util.ArrayList;

public class V2Nozzle {

    V2NozzleData v2NozzleData;
    File ca,crt,key;
    EgressGrpc.EgressStub stub;
    int sleep = 0;


    public V2Nozzle(V2NozzleData v2NozzleData) {
        this.v2NozzleData = v2NozzleData;
        createFiles();
    }

    private void createFiles() {
        try {

            ca = getTempFile(v2NozzleData.getCa());
            crt = getTempFile(v2NozzleData.getCrt());
            key = getTempFile(v2NozzleData.getKey());

        } catch (IOException e) {
            System.out.println("An error occurred.\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private File getTempFile(String stringIn) throws IOException {
        File file = File.createTempFile("pre", "suf");
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        ArrayList<String> stringList = new ArrayList<>();
        for (String line : stringIn.split("\\r?\\n")) {
            stringList.add(line);
        }
        for (String str : stringList) {
            bw.write(str);
            bw.write(System.lineSeparator());
        }
        bw.flush();
        return file;
    }

    public void start (){
        ManagedChannel channel = null;

        try {
            channel = NettyChannelBuilder.forAddress(v2NozzleData.getLogsProvider(), 8082)
                    .negotiationType(NegotiationType.TLS)
                    .sslContext(
                            GrpcSslContexts.forClient()
                                    .trustManager(ca)
                                    .keyManager(crt,key)
                                    .clientAuth(ClientAuth.REQUIRE).build()
                    )
                    .overrideAuthority("reverselogproxy")
                    .build();
        } catch (SSLException e) {
            e.printStackTrace();
        }

        stub = EgressGrpc.newStub(channel);

        stub.batchedReceiver(buildEgressBatchRequest(), new StreamObserver<EnvelopeBatch>() {
            @Override
            public void onNext(EnvelopeBatch envelopeBatch) {
                envelopeBatch.getBatchList().forEach(envelope ->
                {
                    try {
                        if(sleep > 0){
                            Thread.sleep(sleep);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
                StringWriter sw = new StringWriter();
                throwable.printStackTrace(new PrintWriter(sw));
                String exceptionAsString = sw.toString();
                System.out.println("Yoda\n"+exceptionAsString);
            }

            @Override
            public void onCompleted() {
                System.out.println("completed");
            }
        });

    }

    private EgressBatchRequest buildEgressBatchRequest(){
        EgressBatchRequest.Builder builder = EgressBatchRequest
                .newBuilder();

        builder.setUsePreferredTags(false);
        builder.addSelectors(
                Selector
                        .newBuilder()
                        .setCounter(CounterSelector.newBuilder().build())
                        .build()
                )
                .addSelectors(
                        Selector
                                .newBuilder()
                                .setGauge(GaugeSelector.newBuilder().build())
                                .build()
                )
                .addSelectors(
                        Selector
                                .newBuilder()
                                .setLog(LogSelector.newBuilder().build())
                                .build()
                )
                .addSelectors(
                        Selector
                                .newBuilder()
                                .setEvent(EventSelector.newBuilder().build())
                                .build()
                )
                .addSelectors(
                        Selector
                                .newBuilder()
                                .setTimer(TimerSelector.newBuilder().build())
                                .build()
                );

//        builder.addSelectors(
//                Selector
//                        .newBuilder()
//                        .setLog(LogSelector.newBuilder()).setSourceId("6e792bbc-0cbb-42b1-84fe-68183616248f")
//                        .build());

        builder.setShardId(v2NozzleData.getShardID());
        return builder.build();
    }


    public int getSleep() {
        return sleep;
    }

    public void setSleep(int sleep) {
        this.sleep = sleep;
    }
}
