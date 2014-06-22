package com.autentia.app.domain.web;

import com.autentia.app.domain.AppConfig;

/**
 * To use Servlet 3.0 specification.
 * Disabled for now!
 */
public class WebAppInitializer /*extends AbstractAnnotationConfigDispatcherServletInitializer*/ {

//    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] { AppConfig.class };
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
