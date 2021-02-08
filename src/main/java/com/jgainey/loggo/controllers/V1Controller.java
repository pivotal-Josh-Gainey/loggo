package com.jgainey.loggo.controllers;


import com.jgainey.loggo.nozzles.V1Nozzle;
import com.jgainey.loggo.nozzles.V2Nozzle;
import com.jgainey.loggo.objs.V1NozzleData;
import com.jgainey.loggo.objs.V2NozzleData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class V1Controller {

    V1Nozzle v1Nozzle;

        @RequestMapping(method = RequestMethod.POST, value = "/start-V1-nozzle", produces = "application/json")
    public ResponseEntity<String> start(@RequestBody V1NozzleData v1NozzleData){


            if(v1Nozzle == null){
                v1Nozzle = new V1Nozzle(v1NozzleData);
            }else{
                return new ResponseEntity<>("Nozzle already started. You can only adjust the delay of message consumption now.", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            v1Nozzle.start();

            return new ResponseEntity<>("ok", HttpStatus.OK);
    }

        @RequestMapping(method = RequestMethod.GET, value = "/delayV1", produces = "application/json")
    public ResponseEntity<String> setThrottle(@RequestParam(value = "sleeptime", defaultValue = "0") int sleep){

            if(v1Nozzle == null){
                return new ResponseEntity<>("The nozzle has not been started yet.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            v1Nozzle.setSleep(sleep);

            return new ResponseEntity<>("ok - set sleep delay to "+ sleep + " milliseconds.", HttpStatus.OK);

    }


}
