package com.junbetterway.security.springrbacauthzero.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import com.junbetterway.security.springrbacauthzero.validator.JwtAudienceValidator;

@Order(SecurityProperties.IGNORED_ORDER)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
    @Value("${auth0.audience}")
    private String audience;
    
    @Value("${auth0.authoritiesClaimName}")
    private String authoritiesClaimName;
    
    @Override
    public void configure(HttpSecurity http) throws Exception {      
        http.authorizeRequests()
        	.antMatchers(HttpMethod.OPTIONS, "/api/**")
        	.permitAll()
        	.antMatchers("/api/**")
        	.authenticated()
	        .and().cors()
	        .and().oauth2ResourceServer()
	        	  .jwt()
	        	  .jwtAuthenticationConverter(getJwtAuthenticationConverter());
    }
    
    @Bean
    public JwtDecoder jwtDecoder(final OAuth2ResourceServerProperties properties) {
    	
        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromOidcIssuerLocation(
        		properties.getJwt().getIssuerUri());

        OAuth2TokenValidator<Jwt> withAudience = new JwtAudienceValidator(audience);
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(properties.getJwt().getIssuerUri());
        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(withIssuer, withAudience);

        jwtDecoder.setJwtValidator(validator);

        return jwtDecoder;
        
    }

    private Converter<Jwt, AbstractAuthenticationToken> getJwtAuthenticationConverter() {
    	
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter(); 
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(authoritiesClaimName);
        
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        	
        return jwtAuthenticationConverter;
        
    }
    
}
