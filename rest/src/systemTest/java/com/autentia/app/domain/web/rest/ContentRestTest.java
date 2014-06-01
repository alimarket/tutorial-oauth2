package com.autentia.app.domain.web.rest;

import com.jayway.restassured.response.Header;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.*;
import static com.jayway.restassured.http.ContentType.JSON;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.*;

public class ContentRestTest {

    public static final Header ACCEPT_JSON = new Header("Accept", "application/json");

    @Test
    public void getContent() throws Exception {
        given()
                .header(ACCEPT_JSON)
        .when()
                .get("/content/1")
        .then()
                .statusCode(HTTP_OK)
                .contentType(JSON)
                .body("title", is("This is the title!"));
    }


}
