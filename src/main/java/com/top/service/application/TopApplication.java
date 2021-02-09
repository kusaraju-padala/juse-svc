package com.top.service.application;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.jboss.resteasy.plugins.interceptors.CorsFilter;

import com.top.service.rest.login.TopLoginService;
import com.top.service.rest.post.TopService;
import com.top.service.rest.post.test.TestService;
import com.top.service.rest.user.TopUserActivity;
import com.top.service.rest.verifyToken.VerifyTokenFilter;

public class TopApplication extends Application {
	
	private Set<Object> singletons = new HashSet<Object>();
	
	public TopApplication() {
		
		CorsFilter corsFilter = new CorsFilter();
		
		corsFilter.getAllowedOrigins().add("*");
        corsFilter.setAllowedMethods("OPTIONS, GET, POST, DELETE, PUT, PATCH");
		
        singletons.add(corsFilter);
		singletons.add(new TopService());
		singletons.add(new TopLoginService());
		singletons.add(new VerifyTokenFilter());
		singletons.add(new TestService());
		singletons.add(new TopUserActivity());
		
	}
	
	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
	

}
