package com.jusenews.service.rest.user;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.jusenews.service.rest.verifyToken.VerifyToken;
import com.top.lib.beans.generic.InsertResponseBean;


@Path("/user")
public class TopUserActivity {
	
	@POST
	@VerifyToken
	@Path("/feedback")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("application/json")
	public Response giveFeedback(MultipartFormDataInput multipartFormDataInput, @Context HttpHeaders headers){
		InsertResponseBean response = new InsertResponseBean();
		try {
			
			response.setKey(1L);
			response.setCode(0);
			response.setMessage("Successfully updated feedback");
		}catch(Exception e) {
			e.printStackTrace();
			response.setCode(-1);
			response.setMessage("Something went wrong please try agian after sometime");
		}
		return Response.ok(response).build();	
	}
	
}
