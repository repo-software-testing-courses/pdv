package com.trackorjargh.javacontrollers;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.trackorjargh.commoncode.CommonCodeImages;
import com.trackorjargh.commoncode.CommonCodeUser;
import com.trackorjargh.component.UserComponent;
import com.trackorjargh.javaclass.ForgotPassword;
import com.trackorjargh.javaclass.GenerateURLPage;
import com.trackorjargh.javaclass.Lists;
import com.trackorjargh.javaclass.PreparateListsShow;
import com.trackorjargh.javaclass.RandomGenerate;
import com.trackorjargh.javaclass.User;
import com.trackorjargh.javarepository.ForgotPasswordRepository;
import com.trackorjargh.javarepository.ListsRepository;
import com.trackorjargh.javarepository.UserRepository;
import com.trackorjargh.mail.MailComponent;

@Controller
public class UserController {
	private final UserRepository userRepository;
	private final UserComponent userComponent;
	private final ListsRepository listsRepository;
	private final CommonCodeImages commonCodeImages;
	private final CommonCodeUser commonCodeUser;
	private final ForgotPasswordRepository forgotPasswordRepository;
	private final MailComponent mailComponent;
	
	@Autowired
	public UserController(UserRepository userRepository, UserComponent userComponent, ListsRepository listsRepository,
			CommonCodeImages commonCodeImages, CommonCodeUser commonCodeUser,
			ForgotPasswordRepository forgotPasswordRepository, MailComponent mailComponent) {
		this.userRepository = userRepository;
		this.userComponent = userComponent;
		this.listsRepository = listsRepository;
		this.commonCodeImages = commonCodeImages;
		this.commonCodeUser = commonCodeUser;
		this.forgotPasswordRepository = forgotPasswordRepository;
		this.mailComponent = mailComponent;
	}

	@RequestMapping("/miperfil")
	public String serveUserProfile(Model model, @RequestParam Optional<String> emailUser,
			@RequestParam Optional<String> passUser, @RequestParam Optional<Boolean> sent,
			@RequestParam Optional<MultipartFile> imageShow) {
		if (sent.isPresent()) {
			if (emailUser.isPresent()) {
				userComponent.getLoggedUser().setEmail(emailUser.get());
			}
			if (!passUser.get().equals("")) {
				userComponent.getLoggedUser().setPassword(passUser.get());
			}
			if (imageShow.isPresent() && imageShow.get().getSize() != 0) {
				String image = commonCodeImages.uploadImage("userImage", imageShow.get());
				userComponent.getLoggedUser().setImage(image);
			}
			userRepository.save(userComponent.getLoggedUser());
		}

		List<PreparateListsShow> listsUser = new LinkedList<>();
		for (Lists list : listsRepository.findByUser(userComponent.getLoggedUser()))
			listsUser.add(new PreparateListsShow(list.getName(), list.getFilms(), list.getBooks(), list.getShows()));

		model.addAttribute("listsUser", listsUser);

		if (listsUser.size() > 0)
			model.addAttribute("listUserTrue", true);

		if (userComponent.getLoggedUser().getRoles().size() == 3) {
			model.addAttribute("isAdmin", true);
		} else {
			if (userComponent.getLoggedUser().getRoles().size() == 2) {
				model.addAttribute("isModerator", true);
			}
		}

		model.addAttribute("myProfile", true);
		return "userProfile";
	}
	
	// create empty list
	@RequestMapping("/listaNueva")
	public String modProfile(Model model, @RequestParam String listName) {
		commonCodeUser.addEmptyListInUser(listName);

		return "redirect:/miperfil";
	}
	
	@RequestMapping("/activarusuario/{name}")
	public String activatedUser(Model model, @PathVariable String name) {
		User user = userRepository.findByNameIgnoreCase(name);

		if (user != null) {
			if (user.isActivatedUser()) {
				model.addAttribute("errorActivatedUser", true);
			} else {
				user.setActivatedUser(true);
				userRepository.save(user);

				model.addAttribute("viewUser", true);
				model.addAttribute("activatedUser", true);
			}
		}

		return "login";
	}
	
	@RequestMapping("/registrar")
	public String serveRegistrer(Model model, RedirectAttributes redir, HttpServletRequest request,
			@RequestParam String name, @RequestParam String email, @RequestParam String pass) {

		if (!name.equals("")) {
			commonCodeUser.newUser(false, request, name, pass, email, "/img/default-user.png", false, "ROLE_USER");
			redir.addFlashAttribute("registered", true);
		}

		return "redirect:/login";
	}

	@RequestMapping("/cambiarcontra/{alphanumericCode}")
	public String changePass(Model model, HttpServletRequest request, @PathVariable String alphanumericCode,
			@RequestParam Optional<String> pass) {
		ForgotPassword forgotPass = forgotPasswordRepository.findBySecretAlphanumeric(alphanumericCode);

		if (forgotPass == null) {
			model.addAttribute("wrongCode", true);

			return "recoverPass";
		}

		User user = forgotPass.getUser();

		if (pass.isPresent()) {
			user.setPassword(pass.get());
			userRepository.save(user);
			forgotPasswordRepository.delete(forgotPass);

			model.addAttribute("changedPass", true);
			model.addAttribute("viewUser", true);
			model.addAttribute("name", user.getName());

			return "login";
		} else {
			model.addAttribute("user", user);

			return "changePass";
		}
	}

	@RequestMapping("/olvidocontra")
	public String forgetPass(Model model, HttpServletRequest request, @RequestParam Optional<String> email) {
		if (email.isPresent()) {
			User user = userRepository.findByEmail(email.get());

			if (user != null) {
				ForgotPassword forgotPass = forgotPasswordRepository.findByUser(user);
				if (forgotPass != null) {
					model.addAttribute("sentEmail", true);

					return "recoverPass";
				}

				RandomGenerate generateRandomString = new RandomGenerate();
				ForgotPassword newForgotPass = new ForgotPassword(generateRandomString.getRandomString(12));
				newForgotPass.setUser(user);
				forgotPasswordRepository.save(newForgotPass);

				GenerateURLPage url = new GenerateURLPage(request);
				mailComponent.sendChangePassEmail(user, url.generateURLChangePass(newForgotPass));

				model.addAttribute("sentChangePass", true);
				return "login";
			} else {
				model.addAttribute("wrongEmail", true);
				return "recoverPass";
			}
		} else {
			model.addAttribute("changePass", true);
			return "recoverPass";
		}
	}
}
