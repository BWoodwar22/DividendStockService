package service;

import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DividendStockServiceApplication {
	private static final Logger log = LoggerFactory.getLogger(DividendStockServiceApplication.class);
	
	public static void main(String[] args) {
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
		
		SpringApplication.run(DividendStockServiceApplication.class, args);
		log.debug("Service started");
	}
}
