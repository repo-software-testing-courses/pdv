package com.trackorjargh.externallogin;

import org.springframework.social.connect.Connection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Service;

import com.trackorjargh.component.UserComponent;
import com.trackorjargh.javaclass.User;
import com.trackorjargh.javarepository.UserRepository;

@Service
public class FacebookConnectionSignup implements ConnectionSignUp {
 
    @Autowired
    private UserRepository userRepository;
    
	@Autowired
	private UserComponent userComponent;
 
    @Override
    public String execute(Connection<?> connection) {
        User user = new User(connection.getDisplayName(), connection.getKey().toString(), "", "/img/default-user.png", true, "ROLE_USER");
        userRepository.save(user);
        userComponent.setLoggedUser(user);
        
        return user.getName();
    }
}

