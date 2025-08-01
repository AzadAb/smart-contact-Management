package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.Helper.Message;
import com.smart.dao.ContactRespository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	
	
	@Autowired
	private BCryptPasswordEncoder encoder;

	@Autowired
	private UserRepository userRepository;
	private User userByUserName;

	@Autowired
	private ContactRespository contactrepository;

	@ModelAttribute
	public void addCommanData(Model model, Principal principal) {
		String userName = principal.getName();
//		System.out.println("userName:" + userName);
		User user = userRepository.getUserByUserName(userName);

//		System.out.println("user" + user);

		model.addAttribute("user", user);

	}

	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal, HttpSession session) {
		session.removeAttribute("message");

		return "normal/user_dashboard";
	}

	@GetMapping("/add_contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principle, HttpSession session) {

		try {

			String name = principle.getName();
			User user = userRepository.getUserByUserName(name);

			if (file.isEmpty()) {
				contact.setImage("demo.png");
			} else {
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Imgae is uploaded");
			}

			contact.setUser(user);
			user.getContacts().add(contact);
			userRepository.save(user);

			System.out.println("added to data base");
			session.setAttribute("message", new Message("your contact is added !! Add more..", "success"));

		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
			session.setAttribute("message", new Message("Something went Wrong !! Try again..", "danger"));
			return "normal/add_contact_form";
		}
		return "normal/add_contact_form";
	}

	@GetMapping("/show_contact/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal) {

		if (page < 0) {
			return "redirect:/user/show_contact/0";
		}

		String username = principal.getName();
		User user = userRepository.getUserByUserName(username);

		Pageable pageable = PageRequest.of(page, 3);
		Page<Contact> contacts = contactrepository.findContactsByUser(user.getId(), pageable);

		if (contacts.getContent().isEmpty() && page > 0) {
			return "redirect:/user/show_contact/" + (page - 1);
		}

		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/show_contact";
	}

	@RequestMapping("/contacts/{cId}")
	public String showContactDetails(@PathVariable("cId") Integer cid, Model model, Principal principal) {
		System.out.println("Cid:" + cid);

		Optional<Contact> con = contactrepository.findById(cid);

		String username = principal.getName();
		User user = userRepository.getUserByUserName(username);

		Contact contact = con.get();
		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
		}

		return "normal/contact_detail";
	}

	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId, Model model, HttpSession session,Principal principal) {
		Contact contact = contactrepository.findById(cId).get();
//		contact.setUser(null);
//
//		contactrepository.delete(contact);
		
	User user = userRepository.getUserByUserName(principal.getName());
	user.getContacts().remove(contact);
	userRepository.save(user);

		session.setAttribute("message", new Message("Contact deleted succesfully...", "success"));

		return "redirect:/user/show_contact/0";

	}

	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid, Model model) {

		model.addAttribute("title", "Update Contact");

		Contact contact = contactrepository.findById(cid).get();

		model.addAttribute("contact", contact);

		return "normal/update_form";
	}

	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model m, HttpSession session, Principal principal) {

		try {

			Contact oldcontact = contactrepository.findById(contact.getcId()).get();

			if (!file.isEmpty()) {

				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile, oldcontact.getImage());
				file1.delete();

				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
			} else {
				contact.setImage(oldcontact.getImage());
			}
			User user = userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			contactrepository.save(contact);
			session.setAttribute("message", new Message("Your Contact is updated", "success"));

		} catch (Exception ex) {

		}
		return "redirect:/user/contacts/"+contact.getcId();

	}

	@GetMapping("/profile")
	public String viewProfile(Model m,Principal principal) {
		m.addAttribute("title","Profile page");
		
		String name = principal.getName();
		User user = userRepository.getUserByUserName(name);
		
		m.addAttribute("user",user);
		return "normal/profile";
		
	}
	
	@GetMapping("/setting")
	public String openSetting() {
		return "normal/settings";
	}
	
	
	@PostMapping("/change-password")
	public String changeapassword(@RequestParam("oldpassword") String oldpassword,@RequestParam("newpassword") String newpassword,Principal principal,HttpSession session) {
		
		String name = principal.getName();
		User username = userRepository.getUserByUserName(name);
		System.out.println("heollo");
		System.out.println(username.getPassword());
		
		if(encoder.matches(oldpassword, username.getPassword())) {
		username.setPassword(encoder.encode(newpassword));
		  userRepository.save(username);
		session.setAttribute("message",new Message("Your Password is successfully changed..!!","success"));
		
	return "redirect:/user/setting";
		}
		else{
			session.setAttribute("message",new Message("Please Enter the Correct old password","danger"));
		return "redirect:/user/setting";
		}
		
		
	}
	
	
	
}
