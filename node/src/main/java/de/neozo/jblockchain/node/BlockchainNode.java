package de.neozo.jblockchain.node;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@SpringBootApplication
@ComponentScan({"config","de.neozo.jblockchain.node"})
public class BlockchainNode {

	public static void main(String[] args) {
		try {
			SpringApplication.run(BlockchainNode.class, args);
		} catch (Exception e) {
			SpringApplication.run(BlockchainNode.class, new String[]{"--server.port=8081"});
		}
		
	}
	
	
}
