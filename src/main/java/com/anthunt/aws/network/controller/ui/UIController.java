package com.anthunt.aws.network.controller.ui;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.anthunt.aws.network.repository.ServiceRepository;
import com.anthunt.aws.network.session.SessionProfile;
import com.anthunt.aws.network.session.SessionHandler;
import com.anthunt.aws.network.utils.Logging;

import software.amazon.awssdk.profiles.Profile;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.elasticloadbalancing.model.LoadBalancerDescription;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.LoadBalancer;
import software.amazon.awssdk.services.rds.model.DBCluster;
import software.amazon.awssdk.services.rds.model.DBInstance;

@Controller
@RequestMapping("/")
public class UIController extends AbstractController {

	private static final Logger log = Logging.getLogger(UIController.class);
			
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
		
		String pagePath = "views/profile/profiles";
		
		Map<String, Profile> profiles = SessionHandler.getSessionProfile(session).getProfiles();
		
		if(profiles.size() > 0) {
		    model.addAttribute("profiles", profiles);
		    model.addAttribute("regions", SessionHandler.REGIONS);
		} else {
			pagePath = "redirect:/profiles/edit";
		}
	    
		return pagePath;
	}
	
	@RequestMapping("profiles/edit")
	public String editProfile(HttpSession session, Model model) throws IOException {
		
		model.addAttribute("profiles", SessionHandler.getSessionProfile(session).getProfileContents());
		
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
		
		SessionProfile sessionProfile = SessionHandler.getSessionProfile(session);
		sessionProfile.setProfileName(profileName);
		sessionProfile.setRegionId(regionId);
		SessionHandler.setSessionProfile(session, sessionProfile);
		log.trace("called /profiles/set/{}/{}", profileName, regionId);
		
		return "redirect:/dashboard";
	}
	
	@RequestMapping("diagram/network")
	public String getDiagram( Model model
			                , HttpSession session) {
		
		ServiceRepository serviceRepository = SessionHandler.getSessionServiceRepository(session);
		
		model.addAttribute("instances", serviceRepository.getEc2InstanceMap().allValues(Instance.class));
		model.addAttribute("classic-loadbalancers", serviceRepository.getClassicLoadBalancerMap().allValues(LoadBalancerDescription.class));
		model.addAttribute("loadbalancers", serviceRepository.getLoadBalancerMap().allValues(LoadBalancer.class));
		model.addAttribute("dbClusters", serviceRepository.getRdsClusterMap().allValues(DBCluster.class));
		model.addAttribute("dbInstances", serviceRepository.getRdsInstanceMap().allValues(DBInstance.class));
		
		return "views/diagram/network";
	}
	
}
