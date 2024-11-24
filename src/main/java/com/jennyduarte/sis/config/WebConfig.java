package com.jennyduarte.sis.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Sirve las imágenes almacenadas en /var/uploads/ desde la URL /images/
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:/var/uploads/");
    }
}
