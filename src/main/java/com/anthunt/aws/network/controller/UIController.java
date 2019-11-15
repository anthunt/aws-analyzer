package com.anthunt.aws.network.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.anthunt.aws.network.repository.ServiceRepository;
import com.anthunt.aws.network.service.Ec2Service;
import com.anthunt.aws.network.service.LoadBalancerService;
import com.anthunt.aws.network.session.SessionProfile;

import software.amazon.awssdk.profiles.Profile;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.regions.Region;

@Controller
@RequestMapping("/")
public class UIController extends AbstractController {

	@Autowired
	private Ec2Service ec2Service;
	
	@Autowired
	private LoadBalancerService loadBalancerService;
	
	public static List<String> PROFILES;
	public static List<Region> REGIONS;
	
	static {
		PROFILES = new ArrayList<String>();		
		ProfileFile profileFile = ProfileFile.defaultProfileFile();
	    Map<String, Profile> profileMap = profileFile.profiles();
	    Set<String> keys = profileMap.keySet();
	    Iterator<String> iKey = keys.iterator();
	    while(iKey.hasNext()) {
	    	PROFILES.add(iKey.next());
	    }
	    REGIONS = Region.regions();
	}
	
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
			    
	    model.addAttribute("profileNames", PROFILES);
	    model.addAttribute("regions", REGIONS);
	    
		return "views/profiles";
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
		
		SessionProfile sessionProfile = this.getSessionProfile(session);
		sessionProfile.setProfileName(profileName);
		sessionProfile.setRegionId(regionId);
		setSessionProfile(session, sessionProfile);
		
		return "redirect:/dashboard";
	}
	
	@RequestMapping("diagram")
	public String getDiagram( Model model
			                , HttpSession session) {
		ServiceRepository serviceRepository = getSessionServiceRepository(session);
		
		model.addAttribute("instances", this.ec2Service.getInstances(serviceRepository));
		model.addAttribute("classic-loadbalancers", this.loadBalancerService.getClassicLoadBalancers(serviceRepository));
		model.addAttribute("loadbalancers", this.loadBalancerService.getLoadBalancers(serviceRepository));
		
		return "views/diagram";
	}
	
}
