package com.autentia.app.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
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
                .antMatchers("/content/**").access("#oauth2.hasScope('read') or hasRole('ROLE_USER')")
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
            ;
//			clients.inMemory()
//                    .withClient("client")
//			 			.resourceIds(RESOURCE_1_ID)
//			 			.authorizedGrantTypes("authorization_code", "implicit")
//			 			.authorities("ROLE_CLIENT")
//			 			.scopes("read", "write")
//			 			.secret("secret")
//			 		    .and()
//			 		.withClient("client-with-redirect")
//			 			.resourceIds(RESOURCE_1_ID)
//			 			.authorizedGrantTypes("authorization_code", "implicit")
//			 			.authorities("ROLE_CLIENT")
//			 			.scopes("read", "write")
//			 			.secret("secret")
//			 			.redirectUris(tonrRedirectUri)
//			 		    .and()
//		 		    .withClient("my-client-with-registered-redirect")
//	 			        .resourceIds(RESOURCE_1_ID)
//	 			        .authorizedGrantTypes("authorization_code", "client_credentials")
//	 			        .authorities("ROLE_CLIENT")
//	 			        .scopes("read", "trust")
//	 			        .redirectUris("http://anywhere?key=value")
//		 		        .and()
//	 		        .withClient("my-trusted-client")
//			            .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
//			            .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
//			            .scopes("read", "write", "trust")
//			            .accessTokenValiditySeconds(60)
//		 		        .and()
//	 		        .withClient("my-trusted-client-with-secret")
//			            .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
//			            .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
//			            .scopes("read", "write", "trust")
//			            .secret("somesecret")
//	 		            .and()
//		            .withClient("my-less-trusted-client")
//			            .authorizedGrantTypes("authorization_code", "implicit")
//			            .authorities("ROLE_CLIENT")
//			            .scopes("read", "write", "trust")
//     		            .and()
//		            .withClient("my-less-trusted-autoapprove-client")
//		                .authorizedGrantTypes("implicit")
//		                .authorities("ROLE_CLIENT")
//		                .scopes("read", "write", "trust")
//		                .autoApprove(true);
			// @formatter:on
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
