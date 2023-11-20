package com.igatec.bop.core.config;

import com.igatec.utilsspring.utils.services.converters.DBObjectParamConverterProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final DBObjectParamConverterProvider provider;

    public WebConfig(DBObjectParamConverterProvider provider) {
        this.provider = provider;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(provider);
    }
}
