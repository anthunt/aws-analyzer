package com.anthunt.aws.network.controller;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.anthunt.aws.network.repository.ServiceRepository;
import com.anthunt.aws.network.service.Ec2Service;
import com.anthunt.aws.network.service.LoadBalancerService;
import com.anthunt.aws.network.service.ProfileService;
import com.anthunt.aws.network.session.SessionProfile;
import com.anthunt.aws.network.session.SessionProvider;

@Controller
@RequestMapping("/")
public class UIController extends AbstractController {

	private static final Logger log = LoggerFactory.getLogger(UIController.class);

	@Autowired
	private ProfileService profileService;
	
	@Autowired
	private Ec2Service ec2Service;
	
	@Autowired
	private LoadBalancerService loadBalancerService;
	
	@RequestMapping("")
	public String main() {
		return "redirect:/profiles";
	}
	
	@RequestMapping("login")
	public String login() {
		return "login";
	}
	
	@RequestMapping("profiles")
	public String getProfiles(Model model) {
			    
	    model.addAttribute("profileNames", SessionProvider.PROFILES);
	    model.addAttribute("regions", SessionProvider.REGIONS);
	    
		return "views/profile/profiles";
	}
	
	@RequestMapping("profiles/set")
	public String setProfile(Model model
			                , @PathVariable("profileName") Optional<String> profileName) throws IOException {
		
		model.addAttribute("profiles", this.profileService.getCredentialFileContent());
		
		return "views/profile/setProfile";
	}
	
	@RequestMapping("dashboard")
	public String getDashboard( Model model
					          , HttpSession session) {	
		return "views/dashboard";
	}
	
	@RequestMapping("setProfile/{profileName}/{regionId}")
	public String setProfileSession( Model model
			                       , HttpSession session
						           , @PathVariable("profileName") String profileName
						           , @PathVariable("regionId") String regionId) {
		
		SessionProfile sessionProfile = SessionProvider.getSessionProfile(session);
		sessionProfile.setProfileName(profileName);
		sessionProfile.setRegionId(regionId);
		SessionProvider.setSessionProfile(session, sessionProfile);
		
		return "redirect:/dashboard";
	}
	
	@RequestMapping("diagram")
	public String getDiagram( Model model
			                , HttpSession session) {
		
		ServiceRepository serviceRepository = SessionProvider.getSessionServiceRepository(session);
		
		model.addAttribute("instances", this.ec2Service.getInstances(serviceRepository));
		model.addAttribute("classic-loadbalancers", this.loadBalancerService.getClassicLoadBalancers(serviceRepository));
		model.addAttribute("loadbalancers", this.loadBalancerService.getLoadBalancers(serviceRepository));
		
		return "views/diagram";
	}
	
	public static String getUserName() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        String userName = null;
        if (authentication != null) {

                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                userName = userDetails.getUsername();

        }
        return userName;
    }
	
}
