# Java [Spring Security 5](https://spring.io/projects/spring-security) With Role-Based Access Control Using [Auth0](https://auth0.com/) 
This repository serves as the backend module to support role-based access control (RBAC) security using __[Auth0](https://auth0.com/)__.

Please clone the __[frontend module](https://github.com/junbetterway/auth0-rbac-angular-ts)__ which is readily integrated to this backend repository.

# What is Auth0?
__[Auth0](https://auth0.com/)__ helps anyone to do the following:

* Add authentication with multiple authentication sources, either social like Google, Facebook, Microsoft Account, LinkedIn, GitHub, Twitter, Box, Salesforce, among others, or enterprise identity systems like Windows Azure AD, Google Apps, Active Directory, ADFS or any SAML Identity Provider.
* Add authentication through more traditional username/password databases.
* Add support for linking different user accounts with the same user.
* Support for generating signed Json Web Tokens to call your APIs and flow the user identity securely.
* And many more ...

# Create a Free Auth0 Account
1. Go to __[Auth0](https://auth0.com/)__ and click __Sign Up__.
2. Use Google, GitHub or Microsoft Account to login.

# Customize [Auth0](https://auth0.com/) Configuration
After you have setup your Auth0 account and the above linked tutorials, make sure to udpate the __application.yml__.

```
server:
  port: 8888
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: <Auth0 Domain>
auth0:
  audience: <Auth0 API Identifier>
  authoritiesClaimName: permissions
```

# Make [Spring Security](https://spring.io/projects/spring-security) Configuration Aware To JWT claims
By default, OAuth2 standard defines the authorities as part of the claims under __scopes__ field. However, using RBAC enabled with __[Auth0](https://auth0.com/)__ tags the granted authorities as part of the claims under __permissions__ field which explains the value of __authoritiesClaimName__ under the __application.yml__ file.

Now, the remaining task is to make Spring Security aware of this since by default it looks for __scopes__ field for the user granted authorities. I have created a converter method which uses the value of __authoritiesClaimName__ under the __application.yml__ file.

```
    private Converter<Jwt, AbstractAuthenticationToken> getJwtAuthenticationConverter() {
    	
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter(); 
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(authoritiesClaimName);
        
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        	
        return jwtAuthenticationConverter;
        
    }
```

Then use this __getJwtAuthenticationConverter__ method on our __[SecurityConfig](https://github.com/junbetterway/auth0-rbac-spring-sec/blob/main/src/main/java/com/junbetterway/security/springrbacauthzero/config/SecurityConfig.java)__ class

```
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
```

# Run the Spring Boot Application Using Spring Tool Suite (STS)
1. Download STS version 3.4.* (or better) from the [Spring website](https://spring.io/tools). STS is a free Eclipse bundle with many features useful for Spring developers.
2. Right-click on the project or the main application class then select "Run As" > "Spring Boot App"

## Powered By
Contact me at [junbetterway](mailto:jkpminon12@yahoo.com)

Happy coding!!!
