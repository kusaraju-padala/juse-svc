package com.jusenews.service.rest.login;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import com.google.gson.Gson;
import com.jusenews.service.rest.verifyToken.VerifyToken;
import com.top.lib.beans.generic.InsertResponseBean;
import com.top.lib.beans.user.CheckUserBean;
import com.top.lib.beans.user.InputInterestsBean;
import com.top.lib.beans.user.UserBean;
import com.top.lib.beans.user.UserUnameBean;
import com.top.lib.post.user.AddInterests;
import com.top.lib.post.user.AddUser;
import com.top.lib.post.user.CheckUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Path("/init")
public class TopLoginService {
	
	private static Key key = new SecretKeySpec(System.getenv("SECRET_KEY").getBytes(),(SignatureAlgorithm.HS384).getJcaName());

	private static final Logger logger = LogManager.getLogger(TopLoginService.class);
	
	@POST
	@VerifyToken
	@Path("/addinterests")
	@Consumes("application/json")
	@Produces("application/json")
	public Response addInterests(String rawinterests){
		InsertResponseBean response = new InsertResponseBean();
		try {
			Claims claims = ResteasyProviderFactory.popContextData(Claims.class);
			InputInterestsBean iim = new Gson().fromJson(rawinterests, InputInterestsBean.class);
			iim.setUserid((Integer)(claims.get("id")));
			response.setKey(new AddInterests().addInterests(iim));
			response.setCode(0);
			response.setMessage("Successfully added interests");
		}catch(Exception e) {
			e.printStackTrace();
			response.setCode(-1);
			response.setMessage("Something went wrong please try agian after sometime");
		}
		return Response.ok(response).build();	
	}
	
	@GET
	@VerifyToken
	@Path("/getinterests")
	@Consumes("application/json")
	@Produces("application/json")
	public Response getInterests() {
		InsertResponseBean response = new InsertResponseBean();
		try {
			String interests = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("interests.json")))
					.lines().collect(Collectors.joining(System.lineSeparator()));
			
			//UserInterestsModel uis = new Gson().fromJson(interests, UserInterestsModel.class);
			
			return Response.ok(interests).build();
		} catch (Exception e) {
			e.printStackTrace();
			response.setCode(-1);
			response.setMessage("Something went wrong please try agian after sometime");
			return Response.serverError().entity(response).build();
		}
	}
	
	
	
	@POST
	@VerifyToken
	@Path("/updateuname")
	@Consumes("application/json")
	@Produces("application/json")
	public Response updateUname(String rawunameString) {
		InsertResponseBean response = new InsertResponseBean();
		System.out.println("Debug Unique Name===========>"+rawunameString);
		logger.debug("Debug Unique Name===========>"+rawunameString);
		try {
			UserUnameBean uuname = new Gson().fromJson(rawunameString, UserUnameBean.class);
			Claims claims = ResteasyProviderFactory.popContextData(Claims.class);
			uuname.setUid((Integer)claims.get("id"));
			response.setKey(new CheckUser().updateUname(uuname));
			response.setCode(0);
			response.setMessage("Updated user succesfully");
		}
		catch(Exception e) {
			e.printStackTrace();
			response.setCode(-1);
		}
		return Response.ok(response).build();
	}
	
	
	@POST
	@Path("/checkuser")
	@Consumes("application/json")
	@Produces("application/json")
	public Response adduser(String rawUser){
		InsertResponseBean response = new InsertResponseBean();
		System.out.println(rawUser);
		logger.debug("User String :"+rawUser);
		try {
			UserBean user = new Gson().fromJson(rawUser,UserBean.class);
			
			CheckUserBean checkUserBean = new CheckUser().getUserFromMailId(user.getUid());
			if(null != checkUserBean) {
				response.setToken(issueToken(getClaims(user,checkUserBean.getUid())));
				if(null!=checkUserBean.getUname()) {
					if(null == checkUserBean.getInterests()) {
						response.setCode(2);
						response.setMessage("EXISTING-USER-UNAME-NOINTERESTS");
					}else {
						response.setCode(3);
						response.setMessage("COMPLETE-USER");
					}
				}
				else {
					response.setCode(1);
					response.setMessage("EXSISTING-USER-NOUNAME");
				}
			}
			else {
				Long userid = new AddUser().addUser(user);
				response.setKey(userid);
				response.setToken(issueToken(getClaims(user,userid)));
				response.setCode(0);
				response.setMessage("INIT-USER");
			}
		}catch(Exception e) {
			e.printStackTrace();
			response.setCode(-1);
			response.setMessage("Something went wrong please try agian after sometime");
		}
		return Response.ok(response).build();	
	}
	
	
	public Map<String,Object> getClaims(UserBean user,Long userid){
		Map<String,Object> claims = new HashMap<String, Object>();
		claims.put("id", userid);
		return claims;
	}
	
	public String issueToken(Map<String,Object> claims) {
		return Jwts.builder()
				.setClaims(claims)
				.setExpiration(new Date(System.currentTimeMillis()+31104000000L))
				.signWith(SignatureAlgorithm.HS384, key).compact();
	}

}
