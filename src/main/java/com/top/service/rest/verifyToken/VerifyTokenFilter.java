package com.top.service.rest.verifyToken;

import java.io.IOException;
import java.security.Key;

import javax.annotation.Priority;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@VerifyToken
@Priority(Priorities.AUTHENTICATION)
public class VerifyTokenFilter implements ContainerRequestFilter{
	
	public VerifyTokenFilter() {
		
	}
	
    private static Key key = new SecretKeySpec(System.getenv("SECRET_KEY").getBytes(),(SignatureAlgorithm.HS384).getJcaName());
 
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        try {
        	String token = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION).substring("Bearer".length()).trim();
            Jwts.parser().setSigningKey(key).parse(token);// build().parseClaimsJws(token);
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody();
            ResteasyProviderFactory.pushContext(Claims.class, claims);
        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Invalid Session, validate your token").build());
        }
    }

}
