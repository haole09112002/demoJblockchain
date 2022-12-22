package de.neozo.jblockchain.node;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@SpringBootApplication
public class BlockchainNode {

	public static void main(String[] args) {
		SpringApplication.run(BlockchainNode.class, args);
	}
	
	@Bean
	public WebMvcConfigurer corsConfigure()
	{
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				// TODO Auto-generated method stub
				registry.addMapping("/**").allowedOrigins("http://127.0.0.1:5500","http://127.0.0.1:5501", "http://127.0.0.1:5503")
				.allowedMethods("GET", "POST", "PUT", "DELETE");
			}
		};
	}
}
