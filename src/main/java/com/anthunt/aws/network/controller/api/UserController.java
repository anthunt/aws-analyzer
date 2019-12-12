package com.anthunt.aws.network.controller.api;

import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anthunt.aws.network.utils.Logging;

@RestController
@RequestMapping("/api/user")
public class UserController extends AbstractAPIController {
	
	private static final Logger log = Logging.getLogger(UserController.class);

}
