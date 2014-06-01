package com.autentia.app.domain.web.rest;

import com.autentia.app.domain.Content;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping(value = "/content", produces="application/json")
public class ContentController {

    private static final Logger logger = LoggerFactory.getLogger(ContentController.class);

    public ContentController() {
        logger.debug("Constructor");
    }

    @RequestMapping(method = GET)
    public List<Content> getList() {
        return new ArrayList<>();
    }

    @RequestMapping(value = "/{id}", method = GET)
    public Content getDetail(@PathVariable Integer id) {
        logger.debug("Content id = {}", id);

        return new Content();
    }

    @RequestMapping(method = POST, consumes = "application/json")
    public Content create() {
        return new Content();
    }

    @RequestMapping(value = "/{id}", method = PUT, consumes = "application/json")
    public Content update() {
        return new Content();
    }

    @RequestMapping(value = "/{id}", method = PATCH, consumes = "application/json")
    public Content partialUpdate() {
        return new Content();
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    public Content delete() {
        return new Content();
    }

}
