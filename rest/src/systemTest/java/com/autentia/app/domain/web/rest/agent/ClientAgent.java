package com.autentia.app.domain.web.rest.agent;

import com.jayway.restassured.response.ValidatableResponse;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;

import static com.jayway.restassured.RestAssured.*;
import static com.jayway.restassured.http.ContentType.JSON;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class ClientAgent extends Agent {

    private static final RandomValueStringGenerator CLIENT_STATE_GENERATOR = new RandomValueStringGenerator();

    private final String clientState = CLIENT_STATE_GENERATOR.generate();
    private final String id;
    private final String secret;

    private String authorizationCode;
    private String accessToken;
    private String refreshToken;

    public ClientAgent(String id, String secret) {
        this.id = id;
        this.secret = secret;
    }

    public void requestsTokenWithAuthorizationCode() {
        if (authorizationCode == null) throw new IllegalStateException("You don't have authorization code yet. Try to authorize this client with an UserAgent first.");

        // @formatter:off
        accessToken =
        given()
                .header(ACCEPT_JSON)
                .auth().preemptive().basic(id, secret)
                .filter(sessionFilter)
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
    }

    public void extractAuthorizationCodeFrom(String locationHeader) {
        assertThat(locationHeader, allOf(startsWith("http://anywhere?code="), endsWith("&state=" + clientState)));
        authorizationCode = locationHeader.replace("http://anywhere?code=", "").replace("&state=" + clientState, "");
        assertThat(authorizationCode, not(isEmptyOrNullString()));
    }

    public void extractTokenFrom(String locationHeader) {
        final String[] url = locationHeader.split("#");
        final String redirectUri = url[0];
        assertThat(redirectUri, is("http://registered-to-anywhere"));

        final String[] parameters = url[1].split("&");
        final String access_token = parameters[0];
        final String token_type = parameters[1];
        final String state = parameters[2];
        final String expires_in = parameters[3];

        assertThat(access_token, startsWith("access_token="));
        assertThat(token_type, is("token_type=bearer"));
        assertThat(state, is("state=" + clientState));
        assertThat(expires_in, startsWith("expires_in="));

        accessToken = access_token.split("=")[1];
    }

    public void requestsTokenWithUserCredentials(String username, String password) {
        // @formatter:off
        final ValidatableResponse response =
        given()
                .header(ACCEPT_JSON)
                .auth().preemptive().basic(id,  secret)
                .filter(sessionFilter)
                .param("grant_type", "password")
                .param("username", username)
                .param("password", password)
                .param("scope", "read")
        .when()
                .post("/oauth/token")
        .then()
                .statusCode(HTTP_OK)
                .contentType(containsString(JSON.toString()))
                .body("access_token", not(isEmptyOrNullString()))
                .body("token_type", is("bearer"))
                .body("expires_in", not(isEmptyOrNullString()))
                .body("scope", not(isEmptyOrNullString()));
        // @formatter:on

        accessToken = response.extract().path("access_token");
        refreshToken = response.extract().path("refresh_token");
    }

    public void requestsTokenWithClientCredential(String scope) {
        // @formatter:off
        accessToken = given()
                .header(ACCEPT_JSON)
                .auth().preemptive().basic(id, secret)
                .filter(sessionFilter)
                .param("grant_type", "client_credentials")
                .param("scope", scope)
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
    }

    public void requestsTokenWithRefreshToken() {
        if (refreshToken == null) throw new IllegalStateException("You don't have refresh token yet. Try to request an access token first.");

        // @formatter:off
        accessToken = given()
                .header(ACCEPT_JSON)
                .auth().preemptive().basic(id, secret)
                .filter(sessionFilter)
                .param("grant_type", "refresh_token")
                .param("refresh_token", refreshToken)
        .when()
                .post("/oauth/token")
        .then()
                .statusCode(HTTP_OK)
                .contentType(containsString(JSON.toString()))
                .body("access_token", not(isEmptyOrNullString()))
                .body("token_type", is("bearer"))
                .body("expires_in", not(isEmptyOrNullString()))
                .body("scope", not(isEmptyOrNullString()))
                .body("refresh_token", not(isEmptyOrNullString()))
        .extract()
                .path("access_token")
        ;
        // @formatter:on
    }

    public void getResource() {
        if (accessToken == null) throw new IllegalStateException("You don't have access token yet. Try to request an access token first.");

        // @formatter:off
        given()
                .header(ACCEPT_JSON)
                .filter(sessionFilter)
                .header("Authorization", "bearer " +  accessToken)
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

    public void deleteResource() {
        if (accessToken == null) throw new IllegalStateException("You don't have access token yet. Try to request an access token first.");

        // @formatter:off
        given()
                .header(ACCEPT_JSON)
                .filter(sessionFilter)
                .header("Authorization", "bearer " +  accessToken)
        .when()
                .delete("/content/1")
        .then()
                .log().all()
//                .statusCode(HTTP_OK)
//                .contentType(containsString(JSON.toString()))
//                .body("id", not(isEmptyOrNullString()))
//                .body("title", not(isEmptyOrNullString()))
//                .body("content", not(isEmptyOrNullString()))
        ;
        // @formatter:on
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getClientState() {
        return clientState;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
