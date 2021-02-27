package com.junbetterway.security.springrbacauthzero.validator;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * 
 * This validates the JWT if intended for the registered 
 * API by checking the audience claim within the JWT
 * 
 * @author junbetterway
 *
 */
@Log4j2
@RequiredArgsConstructor
public class JwtAudienceValidator implements OAuth2TokenValidator<Jwt> {

	private final String audience;
	
	@Override
	public OAuth2TokenValidatorResult validate(final Jwt jwt) {
		
        OAuth2Error error = new OAuth2Error("invalid_token", "The required audience is invalid!", null);
        
        if (!jwt.getAudience().contains(audience)) {
        	log.error("The required audience is invalid. The JWT aud? {}", jwt.getAudience());
            return OAuth2TokenValidatorResult.failure(error);
        }

        return OAuth2TokenValidatorResult.success();
        
	}

}
