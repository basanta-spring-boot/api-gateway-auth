package com.javatechie.apigateway;

import com.javatechie.apigateway.config.RedisHashComponent;
import com.javatechie.apigateway.dto.ApiKey;
import com.javatechie.apigateway.utils.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class ApiGatewayApplication {

	@Autowired
	RedisHashComponent redisHashComponent;

	@PostConstruct
	public void initData() {
		List<ApiKey> apiKeys = new ArrayList<>();
		apiKeys.add(new ApiKey("343C-ED0B-4137-B27E", Stream.of(AppConstant.STUDENT_SERVICE_KEY,
				AppConstant.COURSE_SERVICE_KEY).collect(Collectors.toList())));
		apiKeys.add(new ApiKey("FA48-EF0C-427E-8CCF", Stream.of(AppConstant.COURSE_SERVICE_KEY)
				.collect(Collectors.toList())));

		List<Object> list = redisHashComponent.hVals(AppConstant.RECORD_KEY);
		if(list.isEmpty()){
			apiKeys.forEach(k -> redisHashComponent.hSet(AppConstant.RECORD_KEY,k.getKey() , k));
		}
	}


	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(AppConstant.STUDENT_SERVICE_KEY,
						r -> r.path("/api/students/**")
								.filters(f -> f.stripPrefix(2)).uri("http://localhost:8081"))
				.route(AppConstant.COURSE_SERVICE_KEY,
						r -> r.path("/api/courses/**")
								.filters(f -> f.stripPrefix(2)).uri("http://localhost:8082"))
				.build();
	}


	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}
