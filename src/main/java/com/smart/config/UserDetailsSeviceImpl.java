package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.smart.dao.UserRepository;
import com.smart.entities.User;


@Service
public class UserDetailsSeviceImpl  implements UserDetailsService{

	@Autowired
	private UserRepository userRespository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User  user = userRespository.getUserByUserName(email);
	

		System.out.print(user);
		if(user==null) {
			throw new UsernameNotFoundException("could not found user !!");
		}
		 System.out.println("User email: " + user.getEmail());
		    System.out.println("Stored password hash: " + user.getPassword());
		CustomUserDetails customeUserDetails = new CustomUserDetails(user);
		return  customeUserDetails;
	}

}
