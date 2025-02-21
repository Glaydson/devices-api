package com.glaydson.devicesapi;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class DevicesapiApplication {

    public static void main(String[] args) {
        log.info("Starting Devices API");
        SpringApplication.run(DevicesapiApplication.class, args);
    }

}
