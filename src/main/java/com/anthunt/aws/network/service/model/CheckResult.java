package com.anthunt.aws.network.service.model;
import java.util.ArrayList;
import java.util.List;

public class CheckResult {

	private boolean inSuccess = false;
	private boolean outSuccess = false;
	private List<CheckRule> allowRules;
	private List<CheckRule> denyRules;
	
	public CheckResult() {
		this.allowRules = new ArrayList<>();
		this.denyRules = new ArrayList<>();
	}
	
	public boolean isInSuccess() {
		return this.inSuccess;
	}
	
	public void setInSuccess(boolean inSuccess) {
		this.inSuccess = inSuccess;
	}
	

	public boolean isOutSuccess() {
		return this.outSuccess;
	}
	
	public void setOutSuccess(boolean outSuccess) {
		this.outSuccess = outSuccess;
	}
	
	public List<CheckRule> getAllowRules() {
		return this.allowRules;
	}
	
	public void setAllowRules(List<CheckRule> allowRules) {
		this.allowRules = allowRules;
	}
	
	public List<CheckRule> getDenyRules() {
		return denyRules;
	}
	
	public void setDenyRules(List<CheckRule> denyRules) {
		this.denyRules = denyRules;
	}
	
	public List<CheckRule> getAllRules() {
		List<CheckRule> checkRules = new ArrayList<CheckRule>();
		checkRules.addAll(this.allowRules);
		checkRules.addAll(this.denyRules);
		return checkRules;
	}
	
}
