package com.jusenews.service.rest.admin;

import java.io.InputStream;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;
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

import com.top.lib.beans.admin.AdminLoginBean;
import com.top.lib.beans.admin.AdminLoginResponseBean;
import com.top.lib.beans.generic.InsertResponseBean;
import com.top.lib.post.admin.AdminUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Path("/admin")
public class AdminService {
	
	private static Key key = new SecretKeySpec(System.getenv("SECRET_KEY").getBytes(),(SignatureAlgorithm.HS384).getJcaName());
	
	@POST
	@Path("/login")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("application/json")
	public Response addPost(MultipartFormDataInput multipartFormDataInput, @Context HttpHeaders headers) {
		InsertResponseBean response = new InsertResponseBean();
		try {

			Map<String, List<InputPart>> map = multipartFormDataInput.getFormDataMap();
			AdminLoginBean admin = new AdminLoginBean();
			admin.setUserId(new String(getBytesFromMultipartMap(map, "userid")));
			admin.setPassword(new String(getBytesFromMultipartMap(map, "password")));
			
			response.setToken(issueToken(getClaims(new AdminUtils().loginAdmin(admin))));
			response.setCode(0);
			response.setMessage("Login valid");
		} catch (Exception e) {
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
	public Map<String,Object> getClaims(AdminLoginResponseBean admin){
		Map<String,Object> claims = new HashMap<String, Object>();
		claims.put("id", admin.getKey());
		claims.put("type","admin");
		claims.put("accesslevel",admin.getAccessLevel());
		claims.put("name",admin.getName());
		return claims;
	}
	
	public String issueToken(Map<String,Object> claims) {
		return Jwts.builder()
				.setClaims(claims)
				.setExpiration(new Date(System.currentTimeMillis()+31104000000L))
				.signWith(SignatureAlgorithm.HS384, key).compact();
	}
}
