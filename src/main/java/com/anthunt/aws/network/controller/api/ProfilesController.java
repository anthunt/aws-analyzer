package com.anthunt.aws.network.controller.api;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anthunt.aws.network.controller.api.model.ProfileStrings;
import com.anthunt.aws.network.controller.model.Response;
import com.anthunt.aws.network.repository.profile.model.ProfileContents;
import com.anthunt.aws.network.service.ProfileService;
import com.anthunt.aws.network.session.SessionProfile;
import com.anthunt.aws.network.session.SessionProvider;
import com.anthunt.aws.network.utils.Logging;

@RestController
@RequestMapping("/api/profiles")
public class ProfilesController extends AbstractAPIController {

	
	private static final Logger log = Logging.getLogger(ProfilesController.class);

	@Autowired
	private ProfileService profileService;
	
	@PostMapping("edit")
	public Response editCredentials(HttpSession session
								   , @RequestBody ProfileStrings profileStrings) throws IOException {
		
		log.trace("ProfileStrings : {}", profileStrings);
		SessionProfile sessionProfile = SessionProvider.getSessionProfile(session);
		ProfileContents profileContents = new ProfileContents(sessionProfile.getUserid());
		profileContents.setConfig(profileStrings.getConfig());
		profileContents.setCredentials(profileStrings.getCredentials());
		
		log.trace("ProfileContents : {}", profileContents);
		
		sessionProfile.setProfileFile(profileService.updateProfile(profileContents));
		
		SessionProvider.setSessionProfile(session, sessionProfile);
		
		return new Response();
	}
	
}
