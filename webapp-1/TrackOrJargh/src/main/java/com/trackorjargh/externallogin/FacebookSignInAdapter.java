package com.trackorjargh.externallogin;

import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

import com.trackorjargh.component.UserComponent;
import com.trackorjargh.javaclass.User;
import com.trackorjargh.javarepository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;

@Service
public class FacebookSignInAdapter implements SignInAdapter {
	
	private UserRepository userRepository;
	private UserComponent userComponent;

	public FacebookSignInAdapter() {
	}

	public FacebookSignInAdapter(UserRepository userRepository, UserComponent userComponent) {
		this.userRepository = userRepository;
		this.userComponent = userComponent;
	}

	@Override
	public String signIn(String localUserId, Connection<?> connection, NativeWebRequest request) {
		User user = userRepository.findByNameIgnoreCase(connection.getDisplayName());
		
		List<GrantedAuthority> roles = new ArrayList<>();
		if(user != null) {
			userComponent.setLoggedUser(user);
			
			for (String role : user.getRoles()) {
				roles.add(new SimpleGrantedAuthority(role));
			}
		} else {
			roles.add(new SimpleGrantedAuthority("ROLE_USER"));
		}
		
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
				connection.getDisplayName(), null, roles));

		return null;
	}

}
