package net.cmauri.chain;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Hello world!
 */
@Log4j2
public class App {
    public static void main(String[] args) {

        System.out.println("Hello World!");
        log.error("It's log4j2");
        log.debug("It's lombok log4j2");
    }
}
