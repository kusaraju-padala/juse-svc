package com.jusenews.service.application;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.jboss.resteasy.plugins.interceptors.CorsFilter;

import com.jusenews.service.rest.login.TopLoginService;
import com.jusenews.service.rest.post.TopService;
import com.jusenews.service.rest.post.test.TestService;
import com.jusenews.service.rest.user.TopUserActivity;
import com.jusenews.service.rest.verifyToken.VerifyTokenFilter;

@ApplicationPath("/")
public class TopApplication extends Application {

	private Set<Object> singletons = new HashSet<Object>();

	@Override
	public Set<Object> getSingletons() {
		CorsFilter corsFilter = new CorsFilter();

		corsFilter.getAllowedOrigins().add("*");
		corsFilter.setAllowedMethods("OPTIONS, GET, POST, DELETE, PUT, PATCH");

		corsFilter.setAllowCredentials(true);
		corsFilter.setCorsMaxAge(200);
		singletons.add(corsFilter);
		singletons.add(new TopService());
		singletons.add(new TopLoginService());
		singletons.add(new VerifyTokenFilter());
		singletons.add(new TestService());
		singletons.add(new TopUserActivity());
		return singletons;
	}

}
