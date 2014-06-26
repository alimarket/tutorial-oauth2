package com.autentia.app.domain.web.rest;

import com.autentia.app.domain.web.rest.agent.ClientAgent;
import com.autentia.app.domain.web.rest.agent.UserAgent;
import com.jayway.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.autentia.app.domain.web.rest.agent.Agent.ACCEPT_JSON;
import static com.jayway.restassured.RestAssured.*;
import static com.jayway.restassured.config.RedirectConfig.redirectConfig;
import static com.jayway.restassured.http.ContentType.JSON;
import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class OAuth2AuthorizationMethodTest {

    @BeforeClass
    public static void setUpOnce() throws Exception {
//        RestAssured.port = 7000; // Pass through external proxy (like http://nettool.sourceforge.net/) to debug the requests and responses.
        RestAssured.config = config().redirect(redirectConfig().followRedirects(false));
    }

    @Test
    public void given_unauthenticated_client__When_resource_is_accessed__Then_response_with_unauthorized_error() throws Exception {
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
    public void given_authorization_code_grant__When_client_access_resource__Then_get_it() throws Exception {
        final UserAgent user = new UserAgent();
        final ClientAgent client = new ClientAgent("client", "client-secret");

        user.authenticate();
        final String redirectLocationHeader = user.requestsAuthorizationCodeGrant(client.getClientState());

        client.extractAuthorizationCodeFrom(redirectLocationHeader);
        client.requestsTokenWithAuthorizationCode();
        client.getResource();
    }

    @Test
    public void given_implicit_grant__When_client_access_resource__Then_get_it() throws Exception {
        final UserAgent user = new UserAgent();
        final ClientAgent client = new ClientAgent("ignored", "ignored"); // id and secret are ignored because client never access to /oauth/token.

        user.authenticate();
        final String redirectLocationHeader = user.requestsImplicitGrant(client.getClientState());

        client.extractTokenFrom(redirectLocationHeader);
        client.getResource();
    }

    @Test
    public void given_resource_owner_password_credentials_grant__When_client_access_resource__Then_get_it() throws Exception {
        final ClientAgent client = new ClientAgent("client-resource-owner-password", "client-resource-owner-password-secret");

        client.requestsTokenWithUserCredentials("user1", "user1");
        client.getResource();
    }

    @Test
    public void given_client_credentials_grant__When_client_access_resource__Then_get_it() throws Exception {
        final ClientAgent client = new ClientAgent("client-credentials", "client-credentials-secret");

        client.requestsTokenWithClientCredential("read");
        client.getResource();
    }

    @Test
    public void given_a_refresh_token__When_use_it__Then_get_another_new_valid_token() throws Exception {
        final ClientAgent client = new ClientAgent("client-with-refresh-token", "client-with-refresh-token-secret");

        client.requestsTokenWithUserCredentials("user1", "user1");
        final String firstToken = client.getAccessToken();

        final String refreshToken = client.getRefreshToken();
        assertThat(refreshToken, not(isEmptyOrNullString()));

        client.requestsTokenWithRefreshToken();
        final String secondToken = client.getAccessToken();
        assertThat(secondToken, not(is(firstToken)));

        client.getResource();
    }
}
