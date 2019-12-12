package com.anthunt.aws.network.controller.ui;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.anthunt.aws.network.controller.ui.model.JoinUser;
import com.anthunt.aws.network.service.user.UserService;
import com.anthunt.aws.network.utils.Logging;

@Controller
@RequestMapping("user")
public class UserUIController {

	@Autowired
	private UserService userService;
	
	private static final Logger log = Logging.getLogger(UserUIController.class);

	@GetMapping("join")
	public String join() {
		return "views/user/join";
	}
	
	@RequestMapping("join/set")
	public String join(JoinUser user) {
		
		log.info("Join User : {}", user);
		
		userService.joinUser(user);
		
		return "redirect:/login";
	}
	
}
