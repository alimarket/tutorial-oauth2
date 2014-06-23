package com.autentia.app.domain.web.rest;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.filter.session.SessionFilter;
import com.jayway.restassured.response.Header;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;

import static com.jayway.restassured.RestAssured.*;
import static com.jayway.restassured.config.RedirectConfig.redirectConfig;
import static com.jayway.restassured.http.ContentType.HTML;
import static com.jayway.restassured.http.ContentType.JSON;
import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class ContentRestTest {

    public static final Header ACCEPT_JSON = new Header("Accept", "application/json");
    private static final RandomValueStringGenerator STATE_GENERATOR = new RandomValueStringGenerator();

    private final SessionFilter userSession = new SessionFilter();
    private final SessionFilter clientSession = new SessionFilter();

    @Before
    public void setUpOnce() throws Exception {
//        RestAssured.port = 7000; // Pass through external proxy (like http://nettool.sourceforge.net/) to debug the requests and responses.
        RestAssured.config = config().redirect(redirectConfig().followRedirects(false));
    }

    @Test
    public void give_unauthenticated_user__When_resource_is_accessed__Then_response_with_unauthorized_error() throws Exception {
        // @formatter:off
        given()
                .header(ACCEPT_JSON)
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
    public void give_authenticated_user__When_resource_is_accessed__Then_response_with_the_resource() throws Exception {
        authenticateUser();

        // @formatter:off
        given()
                .header(ACCEPT_JSON)
                .filter(userSession)
        .when()
                .get("/content/1")
        .then()
                .statusCode(HTTP_OK)
                .contentType(containsString(JSON.toString()))
                .body("title", is("This is the title!"))
        ;
        // @formatter:on
    }

    @Test
    public void given_unauthenticated_user__When_authorization_code_is_requested__Then_redirect_to_login() throws Exception {
        // @formatter:off
        when()
                .get("/oauth/authorize")
        .then()
                .statusCode(HTTP_MOVED_TEMP)
                .header("Location", is("http://localhost:" + RestAssured.port +"/login"))
        ;
        // @formatter:on
    }

    @Test
    public void given_authenticated_user__When_authorization_code_is_requested__Then_code_is_returned_by_authorization_server() throws Exception {
        authenticateUser();
        final String code = userRequestsAuthorizationCode();

        assertThat(code, not(isEmptyOrNullString()));
    }

    private String userRequestsAuthorizationCode() {
        final String clientState = STATE_GENERATOR.generate();
        getAuthorizationForClient(clientState);
        return giveScopesApprovalForClient(clientState);
    }

    private void getAuthorizationForClient(String clientState) {
        // @formatter:off
        given()
                .header(ACCEPT_JSON)
                .filter(userSession)
                .param("response_type", "code")
                .param("client_id", "client")
                .param("redirect_uri", "http://anywhere")
                .param("scope", "read")
                .param("state", clientState)
        .when()
                .post("/oauth/authorize")
        .then()
                .statusCode(HTTP_OK)
                .contentType(containsString(JSON.toString()))
                .body("state", is(clientState))
                .body("redirect_uri", is("http://anywhere"))
        ;
        // @formatter:on
    }

    private String giveScopesApprovalForClient(String clientState) {
        // @formatter:off
        final String locationHeader =
        given()
                .filter(userSession)
                .param("user_oauth_approval", "true")
                .param("scope.read", "true")
        .when()
                .post("/oauth/authorize")
        .then()
                .statusCode(HTTP_MOVED_TEMP)
        .extract()
                .header("Location")
        ;
        // @formatter:on

        assertThat(locationHeader, allOf(startsWith("http://anywhere?code="), endsWith("&state=" + clientState)));

        return locationHeader.replace("http://anywhere?code=", "").replace("&state=" + clientState, "");
    }

    private void authenticateUser() {
        getLoginForm();
        postUserCredentials();
    }

    private void getLoginForm() {
        // @formatter:off
        given()
                .filter(userSession)
        .when()
                .get("/login")
        .then()
                .statusCode(HTTP_OK)
                .contentType(containsString(HTML.toString()))
                .body(containsString("username"), containsString("password"))
        ;
        // @formatter:on
    }

    private void postUserCredentials() {
        // @formatter:off
        given()
                .filter(userSession)
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

    @Test
    public void given_valid_authorization_code__When_client_requests_token__Then_client_can_access_resources_with_received_token() throws Exception {
        authenticateUser();
        final String code = userRequestsAuthorizationCode();
        final String token = clientRequestsAccessToken(code);

        // @formatter:off
        given()
                .header(ACCEPT_JSON)
                .filter(clientSession)
                .header("Authorization", "bearer " + token)
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

    private String clientRequestsAccessToken(String authorizationCode) {
        // @formatter:off
        final String token =
        given()
                .header(ACCEPT_JSON)
                .auth().preemptive().basic("client", "client-secret")
                .filter(clientSession)
                .param("grant_type", "authorization_code")
                .param("code", authorizationCode)
                .param("redirect_uri", "http://anywhere")
                .param("client_id", "client")
        .when()
                .post("/oauth/token")
        .then()
                .statusCode(HTTP_OK)
                .contentType(containsString(JSON.toString()))
                .body("access_token", not(isEmptyOrNullString()))
                .body("token_type", is("bearer"))
                .body("expires_in", not(isEmptyOrNullString()))
                .body("scope", not(isEmptyOrNullString()))
        .extract()
                .path("access_token")
        ;
        // @formatter:on

        assertThat(token, not(isEmptyOrNullString()));
        return token;
    }
}
