package com.jgainey.loggo.configs;

import com.jgainey.loggo.objs.V1NozzleData;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.doppler.DopplerClient;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.doppler.ReactorDopplerClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.cloudfoundry.reactor.uaa.ReactorUaaClient;
import org.cloudfoundry.uaa.UaaClient;
import org.springframework.context.annotation.Bean;


public class Configuration {

    private V1NozzleData properties;

    DefaultConnectionContext connectionContext;

    public Configuration(V1NozzleData properties) {
        this.properties = properties;
        initConnectionContext();
    }


    public void initConnectionContext() {
        connectionContext = DefaultConnectionContext.builder()
                .apiHost(properties.getApi())
                .skipSslValidation(true)
                .build();
    }



    public PasswordGrantTokenProvider tokenProvider() {
        return PasswordGrantTokenProvider.builder()
                .password(properties.getPassword())
                .username(properties.getUser())
                .build();
    }


    public ReactorCloudFoundryClient cloudFoundryClient() {
        return ReactorCloudFoundryClient.builder()
                .connectionContext(connectionContext)
                .tokenProvider(tokenProvider())
                .build();
    }


    public ReactorDopplerClient dopplerClient() {
        return ReactorDopplerClient.builder()
                .connectionContext(connectionContext)
                .tokenProvider(tokenProvider())
                .build();
    }

    public ReactorUaaClient uaaClient() {
        return ReactorUaaClient.builder()
                .connectionContext(connectionContext)
                .tokenProvider(tokenProvider())
                .build();
    }

    public DefaultCloudFoundryOperations cloudFoundryOperations() {
        return DefaultCloudFoundryOperations.builder()
                .cloudFoundryClient(cloudFoundryClient())
                .dopplerClient(dopplerClient())
                .uaaClient(uaaClient())
                .build();
    }



}
