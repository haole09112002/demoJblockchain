package config;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@CacheConfig
@EnableWebMvc
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

}
