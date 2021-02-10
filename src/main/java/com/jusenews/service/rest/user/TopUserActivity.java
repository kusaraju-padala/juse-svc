package com.jusenews.service.rest.user;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import com.jusenews.service.rest.verifyToken.VerifyToken;
import com.top.lib.beans.generic.InsertResponseBean;
import com.top.lib.post.user.UserActivity;

import io.jsonwebtoken.Claims;


@Path("/user")
public class TopUserActivity {
	
	@POST
	@VerifyToken
	@Path("/follow/{followee}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response follow(@PathParam("followee") Integer followeeid){
		InsertResponseBean response = new InsertResponseBean();
		try {
			Claims claims = ResteasyProviderFactory.popContextData(Claims.class);
			response.setKey(new UserActivity().follow((Integer)claims.get("id"),followeeid));
			response.setCode(0);
			response.setMessage("Successfully added interests");
		}catch(Exception e) {
			e.printStackTrace();
			response.setCode(-1);
			response.setMessage("Something went wrong please try agian after sometime");
		}
		return Response.ok(response).build();	
	}
}
