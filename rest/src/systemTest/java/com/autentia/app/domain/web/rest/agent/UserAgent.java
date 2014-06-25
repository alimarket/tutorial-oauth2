package com.autentia.app.domain.web.rest.agent;

import com.jayway.restassured.RestAssured;

import static com.jayway.restassured.RestAssured.*;
import static com.jayway.restassured.http.ContentType.HTML;
import static com.jayway.restassured.http.ContentType.JSON;
import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.*;

public class UserAgent extends Agent {

    public void authenticate() {
        getLoginForm();
        postCredentials();
    }

    private void getLoginForm() {
        // @formatter:off
        given()
                .filter(sessionFilter)
        .when()
                .get("/login")
        .then()
                .statusCode(HTTP_OK)
                .contentType(containsString(HTML.toString()))
                .body(containsString("username"), containsString("password"))
        ;
        // @formatter:on
    }

    private void postCredentials() {
        // @formatter:off
        given()
                .filter(sessionFilter)
                .param("username", "user1")
                .param("password", "user1")
        .when()
                .post("/login")
        .then()
                .statusCode(HTTP_MOVED_TEMP)
                .header("Location", "http://localhost:" + RestAssured.port +"/")
        ;
        // @formatter:on
    }

    public String requestsAuthorizationCodeGrant(String clientState) {
        requestsAuthorizationForClient("code", "client", clientState, "http://anywhere");
        return giveScopesApprovalForClient();
    }

    public String requestsImplicitGrant(String clientState) {
        requestsAuthorizationForClient("token", "client-implicit", clientState, "http://registered-to-anywhere");
        return giveScopesApprovalForClient();
    }

    private void requestsAuthorizationForClient(String responseType, String client_id, String clientState, String clientRedirectUri) {
        // @formatter:off
        given()
                .header(ACCEPT_JSON)
                .filter(sessionFilter)
                .param("response_type", responseType)
                .param("client_id", client_id)
                .param("redirect_uri", clientRedirectUri)
                .param("scope", "read")
                .param("state", clientState)
        .when()
                .post("/oauth/authorize")
        .then()
                .statusCode(HTTP_OK)
                .contentType(containsString(JSON.toString()))
                .body("state", is(clientState))
                .body("redirect_uri", is(clientRedirectUri))
        ;
        // @formatter:on
    }

    private String giveScopesApprovalForClient() {
        // @formatter:off
        return given()
                .filter(sessionFilter)
                .param("user_oauth_approval", "true")
                .param("scope.read", "true")
        .when()
                .post("/oauth/authorize")
        .then()
                .statusCode(HTTP_MOVED_TEMP)
                .header("Location", not(isEmptyOrNullString()))
        .extract()
                .header("Location")
        ;
        // @formatter:on
    }
}
