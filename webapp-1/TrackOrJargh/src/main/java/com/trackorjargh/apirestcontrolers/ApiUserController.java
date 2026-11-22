package com.trackorjargh.apirestcontrolers;

import java.util.LinkedList;
import java.util.List;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.trackorjargh.commoncode.CommonCodeUser;
import com.trackorjargh.component.UserComponent;
import com.trackorjargh.javaclass.DeleteElementsOfBBDD;
import com.trackorjargh.javaclass.ForgotPassword;
import com.trackorjargh.javaclass.User;
import com.trackorjargh.javarepository.ForgotPasswordRepository;
import com.trackorjargh.javarepository.UserRepository;

@RestController
@RequestMapping("/api")
public class ApiUserController {
	private final UserRepository userRepository;
	private final UserComponent userComponent;
	private final CommonCodeUser commonCodeUser;
	private final ForgotPasswordRepository forgotPasswordRepository;
	private final DeleteElementsOfBBDD deleteElementofBBDD;

	@Autowired
	public ApiUserController(UserRepository userRepository, CommonCodeUser commonCodeUser,
			DeleteElementsOfBBDD deleteElementofBBDD, ForgotPasswordRepository forgotPasswordRepository,
			UserComponent userComponent) {
		this.userRepository = userRepository;
		this.commonCodeUser = commonCodeUser;
		this.deleteElementofBBDD = deleteElementofBBDD;
		this.forgotPasswordRepository = forgotPasswordRepository;
		this.userComponent = userComponent;
	}
	
	@RequestMapping(value = "/usuarios/administracion", method = RequestMethod.GET)
	@JsonView(User.NameUserInfo.class)
	public ResponseEntity<List<User>> getUsers() {
		return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
	}

	@RequestMapping(value = "/usuarios/{name}", method = RequestMethod.GET)
	@JsonView(User.BasicInformation.class)
	public ResponseEntity<User> getUser(@PathVariable String name) {
		User user = userRepository.findByNameIgnoreCase(name);
		if (user == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(user, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/usuarios", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@JsonView(User.BasicInformation.class)
	public ResponseEntity<User> addUser(HttpServletRequest request, @RequestBody User user) {
		if (userRepository.findByNameIgnoreCase(user.getName()) == null
				&& userRepository.findByEmail(user.getEmail()) == null) {
			
			return new ResponseEntity<>(commonCodeUser.newUserApi(user, request), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.IM_USED);
		}
	} 

	@RequestMapping(value = "/usuarios/{name}", method = RequestMethod.PUT)
	@JsonView(User.BasicInformation.class)
	public ResponseEntity<User> editUser(@PathVariable String name, @RequestBody User user) {
		if (userRepository.findByNameIgnoreCase(name) == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			User editedUser = userRepository.findByNameIgnoreCase(name);
			User logedUser = userComponent.getLoggedUser();
			
			if (logedUser.getRoles().contains("ROLE_ADMIN")) {
				return new ResponseEntity<>(commonCodeUser.editUser(editedUser, user.getEmail(), user.getPassword(),
						user.getRoles(), user.getImage(), true), HttpStatus.OK);
			} else if (name.equals(logedUser.getName())) {
				return new ResponseEntity<>(commonCodeUser.editUser(editedUser, user.getEmail(), user.getPassword(),
						new LinkedList<>(), user.getImage(), true), HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		}
	}

	@RequestMapping(value = "/usuarios/{name}", method = RequestMethod.DELETE)
	@JsonView(User.BasicInformation.class)
	public ResponseEntity<User> deleteUser(@PathVariable("name") String name) {
		User user = userRepository.findByNameIgnoreCase(name);
		if (user == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			deleteElementofBBDD.deleteUser(user);
			return new ResponseEntity<>(user, HttpStatus.OK);
		}
	}

	public interface basicInfoForgotPassword extends ForgotPassword.BasicInformation, User.NameUserInfo {
	}

	@RequestMapping(value = "/restablecer/{alphanumericCode}/", method = RequestMethod.GET)
	@JsonView(basicInfoForgotPassword.class)
	public ResponseEntity<ForgotPassword> forgotPassword(@PathVariable("alphanumericCode") String alphanumericCode) {
		ForgotPassword forgotPass = forgotPasswordRepository.findBySecretAlphanumeric(alphanumericCode);

		if (forgotPass == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(forgotPass, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/restablecer", method = RequestMethod.PUT)
	@JsonView(basicInfoForgotPassword.class)
	public ResponseEntity<Boolean> forgotPasswordChange(HttpServletRequest request, @RequestBody User userEmail) {
		User user = userRepository.findByEmail(userEmail.getEmail());

		if (user == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(commonCodeUser.forgotPass(user, request), HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/restablecer/{alphanumericCode}/", method = RequestMethod.POST)
	@JsonView(basicInfoForgotPassword.class)
	public ResponseEntity<ForgotPassword> forgotPasswordDelete(
			@PathVariable("alphanumericCode") String alphanumericCode, @RequestBody User user) {
		ForgotPassword forgotPass = forgotPasswordRepository.findBySecretAlphanumeric(alphanumericCode);

		if (forgotPass == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			User userOld = forgotPass.getUser();
			userOld.setPasswordCodificate(user.getPassword());

			forgotPasswordRepository.delete(forgotPass);
			userRepository.save(userOld);

			return new ResponseEntity<>(forgotPass, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/activar/{name}", method = RequestMethod.GET)
	@JsonView(User.ActiveInformation.class)
	public ResponseEntity<User> activatedUser(@PathVariable("name") String name) {
		User user = userRepository.findByNameIgnoreCase(name);

		if (user == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(commonCodeUser.activatedUser(user), HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/comprobarusuario/{name}/", method = RequestMethod.GET)
	public boolean checkUser(@PathVariable String name) {
		User user = userRepository.findByNameIgnoreCase(name);

		if (user != null) {
			return true;
		} else {
			return false;
		}
	}

	@RequestMapping(value = "/comprobaremail/{email}/", method = RequestMethod.GET)
	public boolean checkEmail(@PathVariable String email) {
		User user = userRepository.findByEmail(email);

		if (user != null) {
			return true;
		} else {
			return false;
		}
	}
}
