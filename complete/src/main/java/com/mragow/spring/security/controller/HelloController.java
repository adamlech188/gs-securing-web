package com.mragow.spring.security.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/greeting")
public class HelloController {

	
	@GetMapping("/hello")
	public ResponseEntity<JsonNode> getNode() throws IOException {
		
	   ObjectMapper mapper = new ObjectMapper(); 
	   String greeting = "{\"greeting\": \"hello\"}";
	   JsonNode newNode = mapper.readTree(greeting);
	   
	   return ResponseEntity.ok(newNode);
	   
	}
}
