package com.anthunt.aws.network.controller;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.anthunt.aws.network.repository.ServiceRepository;
import com.anthunt.aws.network.service.Ec2Service;
import com.anthunt.aws.network.service.LoadBalancerService;
import com.anthunt.aws.network.session.SessionProfile;
import com.anthunt.aws.network.session.SessionProvider;
import com.anthunt.aws.network.utils.Logging;

@Controller
@RequestMapping("/")
public class UIController extends AbstractController {

	private static final Logger log = Logging.getLogger(UIController.class);
	
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
	public String getProfiles(HttpSession session, Model model) throws IOException {
			    
	    model.addAttribute("profiles", SessionProvider.getSessionProfile(session).getProfiles());
	    model.addAttribute("regions", SessionProvider.REGIONS);
	    
		return "views/profile/profiles";
	}
	
	@RequestMapping("profiles/edit")
	public String editProfile(HttpSession session, Model model) throws IOException {
		
		model.addAttribute("profiles", SessionProvider.getSessionProfile(session).getProfileContents());
		
		return "views/profile/editProfile";
	}
	
	@RequestMapping("dashboard")
	public String getDashboard( Model model
					          , HttpSession session) {	
		return "views/dashboard";
	}
	
	@RequestMapping("profiles/set/{profileName}/{regionId}")
	public String setProfileSession( Model model
			                       , HttpSession session
						           , @PathVariable("profileName") String profileName
						           , @PathVariable("regionId") String regionId) throws IOException {
		
		SessionProfile sessionProfile = SessionProvider.getSessionProfile(session);
		sessionProfile.setProfileName(profileName);
		sessionProfile.setRegionId(regionId);
		SessionProvider.setSessionProfile(session, sessionProfile);
		log.trace("called /profiles/set/{}/{}", profileName, regionId);
		
		return "redirect:/dashboard";
	}
	
	@RequestMapping("diagram/network")
	public String getDiagram( Model model
			                , HttpSession session) {
		
		ServiceRepository serviceRepository = SessionProvider.getSessionServiceRepository(session);
		
		model.addAttribute("instances", this.ec2Service.getInstances(serviceRepository));
		model.addAttribute("classic-loadbalancers", this.loadBalancerService.getClassicLoadBalancers(serviceRepository));
		model.addAttribute("loadbalancers", this.loadBalancerService.getLoadBalancers(serviceRepository));
		model.addAttribute("dbClusters", serviceRepository.getRdsClusterMap().values());
		model.addAttribute("dbInstances", serviceRepository.getRdsInstanceMap().values());
		
		return "views/diagram/network";
	}
	
}
