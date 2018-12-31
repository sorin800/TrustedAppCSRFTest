package com.tsv.testCSRF.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tsv.testCSRF.model.User;
import com.tsv.testCSRF.repos.UserRepository;
import com.tsv.testCSRF.services.SecurityService;

@Controller
public class UserController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private BCryptPasswordEncoder encoder;

	@RequestMapping("/showReg")
	public String showRegistrationPage() {
		return "registerUser";
	}

	@RequestMapping(value = "/registerUser", method = RequestMethod.POST)
	public String register(@ModelAttribute("user") User user) {
		user.setPassword(encoder.encode(user.getPassword()));
		user.setTotalMoney(1000);
		userRepository.save(user);
		return "login";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@RequestParam("email") String email, @RequestParam("password") String password, Model model) {
		try {
			boolean loginResponse = securityService.login(email, password);
			if (loginResponse) {
				User user = userRepository.findByEmail(email);
				model.addAttribute("money", user.getTotalMoney());
				return "home";
			} else {
				model.addAttribute("msg", "Invalid username or password.Please try again!");
			}
		} catch (Exception e) {
			model.addAttribute("msg", "Invalid username or password.Please try again!");
			e.printStackTrace();
		}
		return "login";
	}

	@RequestMapping(value = "/showLogin", method = RequestMethod.GET)
	public String getLogin(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (!(auth instanceof AnonymousAuthenticationToken)) {
			User user = userRepository.findByEmail(auth.getName());
			model.addAttribute("money", user.getTotalMoney());
			return "home";
		}

		return "login";
	}

	@RequestMapping(value = "/send", method = RequestMethod.GET)
	@ResponseBody
	public Map sendMoney(@RequestParam String email, @RequestParam int money) {
		System.out.println("A intrat!");
		User user = userRepository.findByEmail(email);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User loggedInUser = userRepository.findByEmail(auth.getName());
		Map<Integer,String> returnedValues = new HashMap<>();
		returnedValues.put(loggedInUser.getTotalMoney(), "Something went wrong");
		if (money < 1) {
			returnedValues.put(loggedInUser.getTotalMoney(), "You can't send 0 money");
			return returnedValues;
		}
		if (user == null) {
			returnedValues.put(loggedInUser.getTotalMoney(), "This user doesn't exist");
			return returnedValues;
		}
		if (user != null && money >= 1) {
			if (!email.equals(auth.getName()) && money >= 1) {
				if (money > loggedInUser.getTotalMoney()) {
					returnedValues.put(loggedInUser.getTotalMoney(), "You can't send that much money");
					return returnedValues;
				} else {
					loggedInUser.setTotalMoney(loggedInUser.getTotalMoney()-money);
					user.setTotalMoney(user.getTotalMoney() + money);
					userRepository.save(user);
					userRepository.save(loggedInUser);
					returnedValues.put(loggedInUser.getTotalMoney(), "Money sent successfully");
					return returnedValues;
				}
			}
		}
		return returnedValues;
	}

}
