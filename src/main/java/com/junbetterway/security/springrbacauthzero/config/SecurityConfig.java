package com.junbetterway.security.springrbacauthzero.config;

import java.util.Arrays;
import java.util.List;

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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

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
    public CorsFilter corsFilter() {
        
        var source = new UrlBasedCorsConfigurationSource();
        var corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:54651"));
        corsConfiguration.setAllowedMethods(Arrays.asList(CorsConfiguration.ALL));
        corsConfiguration.setAllowedHeaders(Arrays.asList(CorsConfiguration.ALL));

        source.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(source);

    }
    
    @Bean
    public JwtDecoder jwtDecoder(final OAuth2ResourceServerProperties properties) {
    	
        var jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromOidcIssuerLocation(properties.getJwt().getIssuerUri());
        var withAudience = new JwtAudienceValidator(audience);
        var withIssuer = JwtValidators.createDefaultWithIssuer(properties.getJwt().getIssuerUri());
        var validator = new DelegatingOAuth2TokenValidator<>(withIssuer, withAudience);

        jwtDecoder.setJwtValidator(validator);

        return jwtDecoder;
        
    }

    private Converter<Jwt, AbstractAuthenticationToken> getJwtAuthenticationConverter() {
    	
        var jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter(); 
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(authoritiesClaimName);
        
        var jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        	
        return jwtAuthenticationConverter;
        
    }
    
}
