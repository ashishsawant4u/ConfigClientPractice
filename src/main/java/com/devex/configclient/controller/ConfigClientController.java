package com.devex.configclient.controller;

import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/configclient")
public class ConfigClientController 
{
	@Value("${config.payment.api.key}")
	String paymentApiKey;
	
	RestTemplate restTemplate;
	
	public ConfigClientController(RestTemplateBuilder restTemplateBuilder) 
	{
		this.restTemplate = restTemplateBuilder.build();
	}
	
	@GetMapping("/get")
	public ResponseEntity<String> getConfig()
	{		
		return new ResponseEntity<>("config.payment.api.key="+paymentApiKey,HttpStatus.OK);
	}
	
	@GetMapping("/getbyrest")
	public ResponseEntity<String> getConfigByRest()
	{
		String configServer = "http://localhost:8053";
		String applicationName = "microservice2";
		String profile = "uat";
		String lable = "config2.0";	
		String expectedConfig = "config.ms2.feature-access-key";
		String URL = configServer+"/"+applicationName+"/"+profile+"/"+lable;
		
		ResponseEntity<Environment> response = restTemplate.exchange(URL, HttpMethod.GET, null,Environment.class);
		
		Environment result = response.getBody();
		
		log.info("result ==> "+result);
		
		String expectedConfigValue = result.getPropertySources().stream().filter(ps -> ps.getName().contains(applicationName+"-"+profile))
											.findFirst().get()
											.getSource().get(expectedConfig).toString();
		
		
		return new ResponseEntity<>(expectedConfig+"="+expectedConfigValue,HttpStatus.OK);
	}
}
