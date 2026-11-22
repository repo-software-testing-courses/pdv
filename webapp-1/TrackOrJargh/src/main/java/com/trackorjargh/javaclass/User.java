package com.trackorjargh.javaclass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(BasicInformation.class)
	private Long id;
	
	public interface BasicInformation {}
	public interface NameUserInfo {}
	public interface ActiveInformation {}
	
	@JsonView({BasicInformation.class, NameUserInfo.class})
	private String name;
	private String password;
	@JsonView(BasicInformation.class)
	private String email;
	@JsonView(BasicInformation.class)
	private String image;
	@JsonView({BasicInformation.class, ActiveInformation.class})
	private boolean activatedUser;
	
	@ElementCollection(fetch = FetchType.EAGER)
	@JsonView(BasicInformation.class)
	private List<String> roles;
	
	@OneToMany(mappedBy="user")
	private List<Lists> lists = new LinkedList<>();

	public User() {
	}

	public User(String name, String password, String email, String image, boolean activatedUser, String... roles) {
		this.name = name;
		this.password = new BCryptPasswordEncoder().encode(password);
		this.email = email;
		this.image = image;
		this.activatedUser = activatedUser;
		this.roles = new ArrayList<>(Arrays.asList(roles));
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = new BCryptPasswordEncoder().encode(password);
	}
	
	public void setPasswordCodificate(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<Lists> getLists() {
		return lists;
	}

	public void setLists(List<Lists> lists) {
		this.lists = lists;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public boolean isActivatedUser() {
		return activatedUser;
	}

	public void setActivatedUser(boolean activatedUser) {
		this.activatedUser = activatedUser;
	}
}
