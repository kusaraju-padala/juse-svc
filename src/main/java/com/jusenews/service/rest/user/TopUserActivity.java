package com.jusenews.service.rest.user;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import com.jusenews.service.rest.verifyToken.VerifyToken;
import com.top.lib.beans.generic.InsertResponseBean;
import com.top.lib.beans.user.FeedBackBean;
import com.top.lib.post.user.PostFeedback;

import io.jsonwebtoken.Claims;


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
			Map<String, List<InputPart>> map = multipartFormDataInput.getFormDataMap();
			FeedBackBean feedback = new FeedBackBean();
			feedback.setScore(Integer.parseInt(new String(getBytesFromMultipartMap(map, "score"))));
			Claims claims = ResteasyProviderFactory.popContextData(Claims.class);
			feedback.setUserId((Integer) (claims.get("id")));
			if(null != getBytesFromMultipartMap(map, "feedback"))
				feedback.setFeedback(new String(getBytesFromMultipartMap(map, "feedback")));
			response.setKey(new PostFeedback().postFeedback(feedback));
			response.setCode(0);
			response.setMessage("Successfully updated feedback");
		}catch(Exception e) {
			e.printStackTrace();
			response.setCode(-1);
			response.setMessage("Something went wrong please try agian after sometime");
		}
		return Response.ok(response).build();	
	}
	
	private byte[] getBytesFromMultipartMap(Map<String, List<InputPart>> map, String key) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] bytes = null;
		try {
			List<InputPart> lstInputPart = map.get(key);
			if (null != lstInputPart) {
				for (InputPart inputPart : lstInputPart) {
					buffer.write(inputPart.getBody(InputStream.class, null));
				}
				bytes = buffer.toByteArray();
				buffer.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return bytes;
	}
	
}
