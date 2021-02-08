package com.jgainey.loggo.controllers;


import com.jgainey.loggo.nozzles.V2Nozzle;
import com.jgainey.loggo.objs.V2NozzleData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class V2Controller {

    V2Nozzle v2Nozzle;

        @RequestMapping(method = RequestMethod.POST, value = "/start-V2-nozzle", produces = "application/json")
    public ResponseEntity<String> start(@RequestBody V2NozzleData v2NozzleData){


            if(v2Nozzle == null){
                v2Nozzle = new V2Nozzle(v2NozzleData);
            }else{
                return new ResponseEntity<>("Nozzle already started. You can only adjust the delay of message consumption now.", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            v2Nozzle.start();

            return new ResponseEntity<>("ok", HttpStatus.OK);
    }

        @RequestMapping(method = RequestMethod.GET, value = "/delayV2", produces = "application/json")
    public ResponseEntity<String> setThrottle(@RequestParam(value = "sleeptime", defaultValue = "0") int sleep){

            if(v2Nozzle == null){
                return new ResponseEntity<>("The nozzle has not been started yet.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            v2Nozzle.setSleep(sleep);

            return new ResponseEntity<>("ok - set sleep delay to "+ sleep + " milliseconds.", HttpStatus.OK);

    }


}
