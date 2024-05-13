package com.tucanoo.crm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Maps the "/pdf/" URL path to serve files from the "pdf" directory located in the project root
        registry.addResourceHandler("/pdf/**")
                .addResourceLocations("file:./pdf/");
    }
}
