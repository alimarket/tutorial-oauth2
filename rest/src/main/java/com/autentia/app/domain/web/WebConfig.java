package com.autentia.app.domain.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan
public class WebConfig {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    public WebConfig() {
        logger.debug("Constructor");
    }
}
