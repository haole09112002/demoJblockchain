package config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
@EnableWebMvc
@ComponentScan
public class ResourcesConfig implements WebMvcConfigurer {
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		 // src/main/resources/static/...
		registry
        //.addResourceHandler("/**") // « /css/myStatic.css
        .addResourceHandler("/css/**") // « /static/css/myStatic.css
        .addResourceLocations("classpath:/static/css/"); // Default Static Loaction
      
		registry
        //.addResourceHandler("/**") // « /css/myStatic.css
        .addResourceHandler("/js/**") // « /static/css/myStatic.css
        .addResourceLocations("classpath:/static/js/"); // Default Static Loaction
	}
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		// TODO Auto-generated method stub
		registry.addMapping("/**").allowedOrigins("http://127.0.0.1:5500","http://127.0.0.1:5501", "http://127.0.0.1:5502")
		.allowedMethods("GET", "POST", "PUT", "DELETE");
	}

}
