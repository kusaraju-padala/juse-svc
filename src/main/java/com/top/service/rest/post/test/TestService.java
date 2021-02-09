package com.top.service.rest.post.test;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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

import com.top.lib.post.test.TestKG;

@Path("/toptest")
public class TestService {

	@GET
	@Path("/test")
	public Response getUser(String json) {
		String u = "<h3>Keep going</h3>";
		return Response.ok(u).build();
	}
	
	@GET
	@Path("/testfire")
	public Response getUserFire(String json) throws Exception {
		try {
			//new UserDetails().donothing();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.ok("Did something").build();
	}
	
//	@POST
//	@Path("/testuff")
//	@Consumes(MediaType.MULTIPART_FORM_DATA)
//	@Produces("application/json; charset=UTF-8")
//	public Response addPost(MultipartFormDataInput multipartFormDataInput,@Context HttpHeaders headers) throws Exception{
//		Map<String, List<InputPart>> map = multipartFormDataInput.getFormDataMap();
//		byte[] imageBytes = getBytesFromMultipartMap(map,"utfstring");
//		String s = new String(imageBytes, StandardCharsets.UTF_8);
//		TestBean tbean = new TestBean();
//		tbean.setContent(s);
//		tbean.setId(100001);
//		Long id = new TestKG().testThis(tbean);
//		
//		return Response.ok(id+" = "+s).build();
//	}
	
	@POST
	@Path("/testuff")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("application/json; charset=UTF-8")
	public Response addPost(MultipartFormDataInput multipartFormDataInput,@Context HttpHeaders headers) throws Exception{
		Map<String, List<InputPart>> map = multipartFormDataInput.getFormDataMap();
		byte[] imageBytes = getBytesFromMultipartMap(map,"image");
		TestKG tkg = new TestKG();
		return Response.ok(tkg.testimagesizing(imageBytes)).build();
	}
	
	private byte[] getBytesFromMultipartMap(Map<String, List<InputPart>> map, String key) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] bytes = null;
		try {
			List<InputPart> lstInputPart = map.get(key);
	        if(null != lstInputPart) {
	        	 for(InputPart inputPart : lstInputPart){
						buffer.write(inputPart.getBody(InputStream.class,null));
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
