package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.Helper.Message;
import com.smart.dao.UserRepository;
import com.smart.entities.User;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;



@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	
	@Autowired
	private UserRepository userrepository;
	
	@GetMapping("/")
	public String home(Model model)
	{
		model.addAttribute("title","Home - smart Contace Manager");
		return "home";
	}
	
	@GetMapping("/signup")
	public String sigup(Model model) {
		model.addAttribute("title", "Register - smart contact Manager");
		model.addAttribute("user",new User());
		return "signup";
	}

	
	@PostMapping("/do_register")
	public String registerUser( @Valid @ModelAttribute("user") User user,BindingResult result1,@RequestParam(value="agreement",defaultValue="false") boolean agreement,Model model,HttpSession session) {
		
		try {
			if(!agreement) {
				System.out.println("you have not agreed the terms and conditions");
				throw new Exception("you have not agreed to  the term and coditions");
			}
			if(result1.hasErrors()) {
				System.out.println("Error"+result1.toString());
				model.addAttribute("user",user);
				return "signup";
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default_img");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			System.out.println("user"+user);
			User result = this.userrepository.save(user);
			model.addAttribute("user",result);
			model.addAttribute("user", new User());
			session.setAttribute("message",new Message("Succesfully Registered !!","alert-success"));
			return "signup";
		}catch(Exception ex) {
			
			model.addAttribute("user",user);
			session.setAttribute("message", new Message("something went wrong!! "+ex.getMessage(),"alert-danger"));
			return "signup";
		}
	
	}
	
	@GetMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title","login page");
		return "login";
	}
	

}
