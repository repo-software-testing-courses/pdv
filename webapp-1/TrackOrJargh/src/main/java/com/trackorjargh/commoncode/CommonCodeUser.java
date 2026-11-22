package com.trackorjargh.commoncode;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trackorjargh.component.UserComponent;
import com.trackorjargh.javaclass.ForgotPassword;
import com.trackorjargh.javaclass.GenerateURLPage;
import com.trackorjargh.javaclass.Lists;
import com.trackorjargh.javaclass.RandomGenerate;
import com.trackorjargh.javaclass.User;
import com.trackorjargh.javarepository.ForgotPasswordRepository;
import com.trackorjargh.javarepository.ListsRepository;
import com.trackorjargh.javarepository.UserRepository;
import com.trackorjargh.mail.MailComponent;

@Service
public class CommonCodeUser {
	private final UserRepository userRepository;
	private final ListsRepository listsRepository;
	private final MailComponent mailComponent;
	private final UserComponent userComponent;
	private final ForgotPasswordRepository forgotPasswordRepository;

	@Autowired
	public CommonCodeUser(UserRepository userRepository, ListsRepository listsRepository, MailComponent mailComponent,
			UserComponent userComponent, ForgotPasswordRepository forgotPasswordRepository) {
		this.userRepository = userRepository;
		this.listsRepository = listsRepository;
		this.mailComponent = mailComponent;
		this.userComponent = userComponent;
		this.forgotPasswordRepository = forgotPasswordRepository;
	}

	public Lists addEmptyListInUser(String name) {
		if (listsRepository.findByUserAndName(userComponent.getLoggedUser(), name) == null) {
			Lists listUser = new Lists(name);
			listUser.setUser(userComponent.getLoggedUser());
			listsRepository.save(listUser);

			return listUser;
		} else {
			return new Lists("hola");
		}
	}

	public User activatedUser(User user) {
		user.setActivatedUser(true);
		userRepository.save(user);
		return user;
	}
	
	public boolean forgotPass(User user, HttpServletRequest request) {
		ForgotPassword forgotPass = forgotPasswordRepository.findByUser(user);
		if (forgotPass != null) {
			return false;
		}

		RandomGenerate generateRandomString = new RandomGenerate();
		ForgotPassword newForgotPass = new ForgotPassword(generateRandomString.getRandomString(12));
		newForgotPass.setUser(user);
		forgotPasswordRepository.save(newForgotPass);
		mailComponent.sendChangePassEmail(user, request.getHeader("Origin") + "/new/cambiarcontra/" + newForgotPass.getSecretAlphanumeric());
		
		return true;
	}

	public User editUser(User user, String email, String password, List<String> roles, String imageUser, boolean api) {
		if (email != null) {
			user.setEmail(email);
		}
		if (password != null) {
			if (api) {
				user.setPasswordCodificate(password);
			} else {
				user.setPassword(password);
			}
		}
		if (roles != null && !roles.isEmpty()) {
			user.setRoles(roles);
		}
		if (imageUser != null) {
			user.setImage(imageUser);
		}
		userRepository.save(user);
		return user;
	}

	public User newUserApi(User user, HttpServletRequest request) {
		return newUser(true, request, user.getName(), user.getPassword(), user.getEmail(), user.getImage(),
				user.isActivatedUser(), "ROLE_USER");
	}

	public User newUser(boolean api, HttpServletRequest request, String name, String pass, String email, String image,
			boolean activate, String... role) {
		if (image != null) {
			if (image.equals("")) {
				image = "/img/default-user.png";
			}
		} else {
			image = "/img/default-user.png";
		}

		User newUser = new User(name, pass, email, image, activate, role);
		if (api) {
			newUser.setPasswordCodificate(pass);
		}

		if (!activate) {
			GenerateURLPage url = new GenerateURLPage(request);
			if (api) {
				String urlActivated = request.getHeader("Origin") + "/new/activarusuario/" + name;
				mailComponent.sendVerificationEmail(newUser, urlActivated);
			} else {
				mailComponent.sendVerificationEmail(newUser, url.generateURLActivateAccount(newUser));
			}
		}

		userRepository.save(newUser);
		return newUser;
	}
}
