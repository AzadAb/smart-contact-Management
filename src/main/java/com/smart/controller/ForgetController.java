package com.smart.controller;

import java.util.Optional;
import java.util.Random;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.Helper.Message;
import com.smart.config.UserDetailsSeviceImpl;
import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.service.EmialService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgetController {

    private final UserDetailsSeviceImpl userDetailsSeviceImpl;
	
	
	@Autowired
	private EmialService emailservice;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder encoder;
	

    ForgetController(UserDetailsSeviceImpl userDetailsSeviceImpl) {
        this.userDetailsSeviceImpl = userDetailsSeviceImpl;
    }

	
	@GetMapping("/forget")
	public String openEmailForom(HttpSession session) {
		session.removeAttribute("message");
		return "forget_email_form";
	}
	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email,HttpSession session) {
		System.out.println(email);
		
		 Optional<User> optional = userRepository.findByEmail(email);
		
		 
		 System.out.println(optional);
		 if (!optional.isPresent()) {
			 session.removeAttribute("message");
		        session.setAttribute("message", new Message("We couldn't find any account with this email.", "alert-danger"));
		        return "forget_email_form";
		    }
		 
		 
		int otp = new Random().nextInt(99999);
		
		String subject = "OTP Verfication  From SCM";
		String message = "<div style='padding: 20px; font-family: Arial, sans-serif; background-color: #f3f3f3;'>"
		        + "<h2 style='color: #333;'>Hello,</h2>"
		        + "<p style='font-size: 16px;'>Your OTP for password reset is:</p>"
		        + "<div style='font-size: 28px; font-weight: bold; color: #007bff; margin: 20px 0;'>" + otp + "</div>"
		        + "<p style='font-size: 14px;'>Please do not share this code with anyone. It will expire shortly.</p>"
		        + "<br><p style='font-size: 13px; color: #999;'>– SCM Team</p>"
		        + "</div>";
		String to = email;
		 String from = "azadmda934@gmail.com";
		
	boolean b =	 this.emailservice.sendEmail(to, from, subject, message);
	if(b) {
		
		session.setAttribute("email",email);
		session.setAttribute("otp",otp);
		session.removeAttribute("message");
		session.setAttribute("message", new Message("Otp is successfully send to your email","alert-success"));
		
		return "verfiy_otp";
	
	}
	
	else {
		 session.setAttribute("message", new Message("Failed to send OTP. Try again.", "alert-danger"));
		return "forget_email_form";
		
	}
		
	}
	@PostMapping("/verify-otp")
	public String verfyotp(@RequestParam("otp") Integer otp,HttpSession session) {
		
	Integer orinalotp = (Integer) session.getAttribute("otp");
		
	if(orinalotp !=null  && orinalotp.equals(otp)) {
		
		session.removeAttribute("message");
		session.setAttribute("message",  new Message("otp has successfully verfied","alert-success"));
		return "success-otp";
	}
	else {
		session.removeAttribute("message");
		session.setAttribute("message", new Message("Invalid OTP! Please try again.", "alert-danger"));
		return "verfiy_otp";
	}
		
	}
	
	
@PostMapping("/reset-password")
public String resetPassword(@RequestParam("password") String password , @RequestParam("confirmPassword") String confirmpassword,HttpSession session) {
	
	if(!password.equals(confirmpassword)) {
		session.removeAttribute("message");
	session.setAttribute("message",new Message("confirm password and new password is not matched.","alert-danger"));
		return "success-otp";
	}
	
	String email = (String) session.getAttribute("email");
	
	User user = userRepository.getUserByUserName(email);
	
	user.setPassword(encoder.encode(password));
	userRepository.save(user);
	
	return "success-password";
}
	

}
