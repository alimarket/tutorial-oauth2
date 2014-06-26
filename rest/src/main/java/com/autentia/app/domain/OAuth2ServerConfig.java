package com.autentia.app.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.DefaultUserApprovalHandler;

@Configuration
public class OAuth2ServerConfig {

    private static final String RESOURCE_1_ID = "resource-1";

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.resourceId(RESOURCE_1_ID);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            // @formatter:off
			http
				.requestMatchers().antMatchers("/content/**")
			    .and()
			.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/content/**").access("#oauth2.hasScope('write')")
                .antMatchers(HttpMethod.PUT, "/content/**").access("#oauth2.hasScope('write')")
                .antMatchers(HttpMethod.PATCH, "/content/**").access("#oauth2.hasScope('write')")
                .antMatchers(HttpMethod.DELETE, "/content/**").access("#oauth2.hasScope('write')")
                .antMatchers(HttpMethod.GET, "/content/**").access("#oauth2.hasScope('read') or hasRole('ROLE_USER')")
            ;
			// @formatter:on
        }
    }

    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        @Autowired
        private AuthenticationManager authenticationManager;

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            // @formatter:off
			clients.inMemory()
                    .withClient("client")
			 			.resourceIds(RESOURCE_1_ID)
			 			.authorizedGrantTypes("authorization_code")
			 			.authorities("ROLE_CLIENT")
			 			.scopes("read", "write")
			 			.secret("client-secret") // Needed to access /oauth/token with basic authentication.
                        .and()
                    .withClient("client-implicit")
			 			.resourceIds(RESOURCE_1_ID)
			 			.authorizedGrantTypes("implicit")
			 			.authorities("ROLE_CLIENT")
			 			.scopes("read", "write")
                        .redirectUris("http://registered-to-anywhere") // Checked by authorization server.
                        // secret not needed because client never access /oauth/token
                        .and()
                    .withClient("client-resource-owner-password")
			 			.resourceIds(RESOURCE_1_ID)
			 			.authorizedGrantTypes("password")
			 			.authorities("ROLE_CLIENT")
			 			.scopes("read", "write")
                        .secret("client-resource-owner-password-secret")
                        .and()
                    .withClient("client-credentials")
			 			.resourceIds(RESOURCE_1_ID)
			 			.authorizedGrantTypes("client_credentials")
			 			.authorities("ROLE_CLIENT")
			 			.scopes("read", "write")
                        .secret("client-credentials-secret")
                        .and()
                    .withClient("client-with-refresh-token")
			 			.resourceIds(RESOURCE_1_ID)
			 			.authorizedGrantTypes("authorization_code", "password", "refresh_token")
			 			.authorities("ROLE_CLIENT")
			 			.scopes("read", "write")
                        .secret("client-with-refresh-token-secret")
            ;
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints
                    // Set an AuthenticationManager to support password grant type!
                    .authenticationManager(authenticationManager)

                    // For testing purpose we don't want store the approvals, so each time the approval will be checked.
                    .userApprovalHandler(new DefaultUserApprovalHandler())
            ;
        }
    }

}
