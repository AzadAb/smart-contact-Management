package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.dao.ContactRespository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

@RestController
public class SearchController {

	@Autowired
	private UserRepository userrespository;

	@Autowired
	private ContactRespository contactrespository;

	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal) {
		System.out.println(query);
		User user = userrespository.getUserByUserName(principal.getName());
		List<Contact> result = contactrespository.findByNameContainingAndUser(query, user);

		return ResponseEntity.ok(result);
	}

}
