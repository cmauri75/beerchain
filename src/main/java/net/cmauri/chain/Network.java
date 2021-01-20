package net.cmauri.chain;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@Log4j2
@SpringBootApplication
public class Network {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Network.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", "8000"));
        app.run(args);
    }
}
