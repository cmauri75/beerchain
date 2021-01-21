package net.cmauri.chain;

import lombok.extern.log4j.Log4j2;
import net.cmauri.chain.rest.ChainController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Log4j2
@SpringBootApplication
public class Network {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Network.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", args[0]));
        app.run(args);
    }

    ChainController chainController;

    @Autowired
    private void setChainController(ChainController cc) {
        this.chainController = cc;
    }

    /**
     * Needed in order to configure listing port and spread to network, it must know it's own port
     * @param ctx
     * @return
     */
    @Bean
    public CommandLineRunner CommandLineRunnerBean(ApplicationContext ctx) {
        return (args) -> {
            log.info("Setting post-contruct params");
            chainController.setCurrentNodeUrl("http://localhost:"+args[0]);
        };
    }

    /**
     * Preparing rest caller
     * @param builder
     * @return
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }


}
