package com.jgainey.loggo.nozzles;


import com.jgainey.loggo.configs.Configuration;
import com.jgainey.loggo.objs.V1NozzleData;
import com.jgainey.loggo.utils.Utils;
import org.cloudfoundry.doppler.Envelope;
import org.cloudfoundry.doppler.FirehoseRequest;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import reactor.core.publisher.Flux;


public class V1Nozzle {


    private Flux<Envelope> stream;
    V1NozzleData v1NozzleData;
    public int sleep;
    Configuration configuration;


    public V1Nozzle(V1NozzleData v1NozzleData){
        this.v1NozzleData = v1NozzleData;
        this.configuration = new Configuration(this.v1NozzleData);
    }

    private void initStream() {
        Utils.logInfo("Initializing Nozzle");
        DefaultCloudFoundryOperations ops = configuration.cloudFoundryOperations();
        Utils.logInfo("Successfully obtained CF credentials and access...requesting firehose stream.");
        stream = ops.getDopplerClient().firehose(
                FirehoseRequest
                        .builder()
                        .subscriptionId(v1NozzleData.getShardID()).build())
                .doOnNext(envelope ->  {
                    if(sleep > 0){
                        try {
                            Thread.sleep(sleep);
                        } catch (InterruptedException e) {
                            Utils.logError("Something went wrong sleeping in the nozzle");
                            e.printStackTrace();
                        }
                    }
                })
                .doOnTerminate(() -> {
                    Utils.logError("Unexpected error, reconnecting...");
                    start();
                });
    }

    public void start(){
        initStream();
        stream.subscribe();
    }

    public int getSleep() {
        return sleep;
    }

    public void setSleep(int sleep) {
        this.sleep = sleep;
    }

}
