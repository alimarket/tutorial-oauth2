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

    @RequestMapping(method = GET)
    public List<Content> getList() {
        return new ArrayList<>();
    }

    @RequestMapping(value = "/{id}", method = GET)
    public Content getDetail(@PathVariable Integer id) {
        logger.debug("Get content id = {}", id);
        return new Content();
    }

    @RequestMapping(method = POST, consumes = "application/json")
    public Content create() {
        logger.debug("Create new content");
        return new Content();
    }

    @RequestMapping(value = "/{id}", method = PUT, consumes = "application/json")
    public Content update(@PathVariable Integer id) {
        logger.debug("Update content id = {}", id);
        return new Content();
    }

    @RequestMapping(value = "/{id}", method = PATCH, consumes = "application/json")
    public Content partialUpdate(@PathVariable Integer id) {
        logger.debug("Patch content id = {}", id);
        return new Content();
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    public void delete(@PathVariable Integer id) {
        logger.debug("Delete content id = {}", id);
    }

}
