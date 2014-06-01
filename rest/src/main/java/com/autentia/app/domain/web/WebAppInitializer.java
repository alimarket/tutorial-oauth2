package com.autentia.app.domain.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebAppInitializer /*extends AbstractAnnotationConfigDispatcherServletInitializer*/ {

    private static final Logger logger = LoggerFactory.getLogger(WebAppInitializer.class);

    public WebAppInitializer() {
        logger.debug("Constructor");
    }

//    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null;
    }

//    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[] { WebConfig.class };
    }

//    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

}
