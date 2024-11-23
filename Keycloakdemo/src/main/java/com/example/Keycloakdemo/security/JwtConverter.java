package com.example.Keycloakdemo.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    // used to extract authorities(roles, permissions) from the Jwt token inform of claims
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    private final JwtConverterProperties properties;


    public JwtConverter(JwtConverterProperties properties){
        this.properties = properties;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt){
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractResourceRoles(jwt).stream()).collect(Collectors.toSet());
        // convert jwt tokens to granted Authorities make sure they are unique using the sets
        return new JwtAuthenticationToken(jwt, authorities, getPrincipalClaimName(jwt));

        // returns a new JwtAuthentication token for the user
    }

    private String getPrincipalClaimName(Jwt jwt) {
        // this extracts the user in subject to the token
        String claimName= JwtClaimNames.SUB;
        if (properties.getPrincipalAttribute() != null){
            claimName = properties.getPrincipalAttribute();
        }
        return jwt.getClaim(claimName);
    }

    private  Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        Map<String, Object> resource;
        Collection<String> resourceRoles;

        if (resourceAccess == null
        || (resource = (Map<String, Object>) resourceAccess.get(properties.getResourceId())) == null
        ||  (resourceRoles = (Collection<String>) resource.get("roles")) == null) {
            return  Set.of();
        }

        return resourceRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }
}
