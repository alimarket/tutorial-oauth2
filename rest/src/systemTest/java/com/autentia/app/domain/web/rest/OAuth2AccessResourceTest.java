package com.autentia.app.domain.web.rest;

import com.autentia.app.domain.web.rest.agent.ClientAgent;
import com.autentia.app.domain.web.rest.agent.UserAgent;
import com.jayway.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.autentia.app.domain.web.rest.agent.Agent.ACCEPT_JSON;
import static com.jayway.restassured.RestAssured.config;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static com.jayway.restassured.config.RedirectConfig.redirectConfig;
import static com.jayway.restassured.http.ContentType.JSON;
import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

public class OAuth2AccessResourceTest {

    @BeforeClass
    public static void setUpOnce() throws Exception {
//        RestAssured.port = 7000; // Pass through external proxy (like http://nettool.sourceforge.net/) to debug the requests and responses.
        RestAssured.config = config().redirect(redirectConfig().followRedirects(false));
    }

    @Test
    public void give_unauthenticated_client__When_resource_is_accessed__Then_response_with_unauthorized_error() throws Exception {
        // @formatter:off
        given()
                .header(ACCEPT_JSON)
                // SessionFilter is not used because no matter if you are user or client,
                // if you are not authenticated, you can't access the resources.
        .when()
                .get("/content/1")
        .then()
                .statusCode(HTTP_UNAUTHORIZED)
                .contentType(containsString(JSON.toString()))
                .body("error", is("unauthorized"))
                .body("error_description", not(isEmptyString()))
        ;
        // @formatter:on
    }

    @Test
    public void given_unauthenticated_user__When_authorization_code_is_requested__Then_redirect_to_login() throws Exception {
        // @formatter:off
        when()
                .get("/oauth/authorize")
                // SessionFilter is not used because no matter if you are user or client,
                // you are redirected to login form if you try to access authorization end point.
        .then()
                .statusCode(HTTP_MOVED_TEMP)
                .header("Location", is("http://localhost:" + RestAssured.port +"/login"))
        ;
        // @formatter:on
    }

    @Test
    public void given_authenticated_user__When_resource_is_accessed__Then_response_with_the_resource() throws Exception {
        final UserAgent user = new UserAgent();

        user.authenticate();

        // @formatter:off
        given()
                .header(ACCEPT_JSON)
                .filter(user.getSessionFilter())
        .when()
                .get("/content/1")
        .then()
                .statusCode(HTTP_OK)
                .contentType(containsString(JSON.toString()))
                .body("id", not(isEmptyOrNullString()))
                .body("title", not(isEmptyOrNullString()))
                .body("content", not(isEmptyOrNullString()))
        ;
        // @formatter:on
    }

    @Test
    public void given_token_with_only_scope_read__When_client_try_to_DELETE__Then_response_with_unauthorized_error() throws Exception {
        final ClientAgent client = new ClientAgent("client-credentials", "client-credentials-secret");

        client.requestsTokenWithClientCredential("read");
        client.deleteResource();
    }

    @Test
    public void given_token_with_scope_write__When_client_try_to_DELETE__Then_delete_the_content() throws Exception {
        final ClientAgent client = new ClientAgent("client-credentials", "client-credentials-secret");

        client.requestsTokenWithClientCredential("write");
        client.deleteResource();
    }

}
