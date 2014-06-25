package com.autentia.app.domain.web.rest.agent;

import com.jayway.restassured.filter.session.SessionFilter;
import com.jayway.restassured.response.Header;

public class Agent {

    public static final Header ACCEPT_JSON = new Header("Accept", "application/json");

    final SessionFilter sessionFilter = new SessionFilter();

    public SessionFilter getSessionFilter() {
        return sessionFilter;
    }
}
