package com.jgainey.loggo;

import com.jgainey.loggo.utils.Utils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LoggoApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoggoApplication.class, args);
        Utils.initLogger();
    }

}
