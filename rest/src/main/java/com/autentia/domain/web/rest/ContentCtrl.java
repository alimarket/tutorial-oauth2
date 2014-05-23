package com.autentia.domain.web.rest;

import com.autentia.domain.Content;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class ContentCtrl {

    private static final Logger logger = LoggerFactory.getLogger(ContentCtrl.class);

    @RequestMapping(value = "/content/{id}", method = GET)
    @ResponseBody
    public Content content(@PathVariable Integer contentId) {
        logger.debug("Content id = {}", contentId);
        return new Content();
    }
}
