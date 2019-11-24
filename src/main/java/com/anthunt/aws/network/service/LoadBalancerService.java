package com.anthunt.aws.network.service;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.anthunt.aws.network.repository.ServiceRepository;
import com.anthunt.aws.network.repository.model.ServiceMap;
import com.anthunt.aws.network.service.checker.LoadBalancerNetwork;
import com.anthunt.aws.network.service.model.checker.CheckResult;
import com.anthunt.aws.network.service.model.checker.CheckResults;
import com.anthunt.aws.network.service.model.checker.CheckRule;
import com.anthunt.aws.network.service.model.checker.CheckType;
import com.anthunt.aws.network.service.model.checker.DirectionType;
import com.anthunt.aws.network.service.model.checker.SecurityGroupCheckRule;
import com.anthunt.aws.network.service.model.diagram.DiagramData;
import com.anthunt.aws.network.service.model.diagram.DiagramEdge;
import com.anthunt.aws.network.service.model.diagram.DiagramNode;
import com.anthunt.aws.network.service.model.diagram.DiagramResult;
import com.anthunt.aws.network.service.model.diagram.NodeType;
import com.anthunt.aws.network.session.SessionProfile;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Vpc;
import software.amazon.awssdk.services.elasticloadbalancing.ElasticLoadBalancingClient;
import software.amazon.awssdk.services.elasticloadbalancing.model.LoadBalancerDescription;
import software.amazon.awssdk.services.elasticloadbalancingv2.ElasticLoadBalancingV2Client;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.Action;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.DescribeListenersRequest;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.DescribeListenersResponse;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.DescribeRulesRequest;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.DescribeRulesResponse;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.DescribeTargetHealthRequest;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.DescribeTargetHealthResponse;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.FixedResponseActionConfig;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.Listener;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.LoadBalancer;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.LoadBalancerTypeEnum;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.RedirectActionConfig;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.Rule;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.RuleCondition;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetDescription;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetGroup;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetHealthDescription;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetHealthStateEnum;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetTypeEnum;

@Service
public class LoadBalancerService extends AbstractNetworkService {

	private ElasticLoadBalancingClient getElasticLoadBalancingClient(SessionProfile sessionProfile) {
		return this.getElasticLoadBalancingClient(sessionProfile.getProfileName(), sessionProfile.getRegionId());
	}
	
	private ElasticLoadBalancingClient getElasticLoadBalancingClient(String profileName, String regionId) {
		return ElasticLoadBalancingClient.builder()
			   .credentialsProvider(ProfileCredentialsProvider.create(profileName))
			   .region(Region.of(regionId))
			   .build();
	}
	
	private ElasticLoadBalancingV2Client getElasticLoadBalancingV2Client(SessionProfile sessionProfile) {
		return this.getElasticLoadBalancingV2Client(sessionProfile.getProfileName(), sessionProfile.getRegionId());
	}
	
	private ElasticLoadBalancingV2Client getElasticLoadBalancingV2Client(String profileName, String regionId) {
		return ElasticLoadBalancingV2Client.builder()
				   .credentialsProvider(ProfileCredentialsProvider.create(profileName))
				   .region(Region.of(regionId))
				   .build();
	}
	
	public ServiceMap<LoadBalancerDescription> getClassicLoadBalancers(SessionProfile sessionProfile) {
		ServiceMap<LoadBalancerDescription> classicLoadBalancerMap = new ServiceMap<>();
		ElasticLoadBalancingClient elasticLoadBalancingClient = this.getElasticLoadBalancingClient(sessionProfile);
		for(LoadBalancerDescription loadBalancerDescription : elasticLoadBalancingClient.describeLoadBalancers().loadBalancerDescriptions()) {
			classicLoadBalancerMap.put(loadBalancerDescription.loadBalancerName(), loadBalancerDescription);
		}
		return classicLoadBalancerMap;
	}
	
	public ServiceMap<LoadBalancer> getLoadBalancers(SessionProfile sessionProfile) {
		ServiceMap<LoadBalancer> loadBalancerMap = new ServiceMap<>();
		ElasticLoadBalancingV2Client elasticLoadBalancingV2Client = this.getElasticLoadBalancingV2Client(sessionProfile);
		for(LoadBalancer loadBalancer : elasticLoadBalancingV2Client.describeLoadBalancers().loadBalancers()) {
			loadBalancerMap.put(loadBalancer.loadBalancerArn(), loadBalancer);
		}
		return loadBalancerMap;
	}
	
	public ServiceMap<List<Listener>> getLoadBalancerListeners(SessionProfile sessionProfile, Collection<LoadBalancer> loadBalancers) {
		ServiceMap<List<Listener>> loadBalancerListenerMap = new ServiceMap<>();
		ElasticLoadBalancingV2Client elasticLoadBalancingV2Client = this.getElasticLoadBalancingV2Client(sessionProfile);
		for(LoadBalancer loadBalancer : loadBalancers) {
			DescribeListenersResponse describeListenersResponse = elasticLoadBalancingV2Client.describeListeners(
					DescribeListenersRequest.builder()
						.loadBalancerArn(loadBalancer.loadBalancerArn())
						.build()
			);
			loadBalancerListenerMap.put(loadBalancer.loadBalancerArn(), describeListenersResponse.listeners());
		}
		
		return loadBalancerListenerMap;
	}
	
	public ServiceMap<List<Rule>> getLoadBalancerRules(SessionProfile sessionProfile, Collection<List<Listener>> listenerss) {
		ServiceMap<List<Rule>> loadBalancerRuleMap = new ServiceMap<>();
		ElasticLoadBalancingV2Client elasticLoadBalancingV2Client = this.getElasticLoadBalancingV2Client(sessionProfile);		
		for(List<Listener> listeners : listenerss) {
			for(Listener listener : listeners) {
				DescribeRulesResponse describeRulesResponse = elasticLoadBalancingV2Client.describeRules(
						DescribeRulesRequest.builder()
											.listenerArn(listener.listenerArn())
											.build()
				);
				loadBalancerRuleMap.put(listener.listenerArn(), describeRulesResponse.rules());
			}
		}
		return loadBalancerRuleMap;
	}
	
	public ServiceMap<TargetGroup> getTargetGroups(SessionProfile sessionProfile) {
		ServiceMap<TargetGroup> targetGroupMap = new ServiceMap<>();
		ElasticLoadBalancingV2Client elasticLoadBalancingV2Client = this.getElasticLoadBalancingV2Client(sessionProfile);
		for(TargetGroup targetGroup : elasticLoadBalancingV2Client.describeTargetGroups().targetGroups()) {
			targetGroupMap.put(targetGroup.targetGroupArn(), targetGroup);
		}
		return targetGroupMap;
	}
	
	public ServiceMap<List<TargetHealthDescription>> getTargetHealthDescriptions(SessionProfile sessionProfile, Collection<TargetGroup> targetGroups) {
		ServiceMap<List<TargetHealthDescription>> targetHealthDescriptionsMap = new ServiceMap<>();
		ElasticLoadBalancingV2Client elasticLoadBalancingV2Client = this.getElasticLoadBalancingV2Client(sessionProfile);		
		for(TargetGroup targetGroup : targetGroups) {
			DescribeTargetHealthResponse describeTargetHealthResponse = elasticLoadBalancingV2Client.describeTargetHealth(
					DescribeTargetHealthRequest.builder()
						.targetGroupArn(targetGroup.targetGroupArn())
						.build()
			);
			targetHealthDescriptionsMap.put(targetGroup.targetGroupArn(), describeTargetHealthResponse.targetHealthDescriptions());
		}
		return targetHealthDescriptionsMap;
	}
	
	public Collection<LoadBalancerDescription> getClassicLoadBalancers(ServiceRepository serviceRepository) {		
		return serviceRepository.getClassicLoadBalancerMap().values();
	}
	
	public Collection<LoadBalancer> getLoadBalancers(ServiceRepository serviceRepository) {
		return serviceRepository.getLoadBalancerMap().values();
	}

	public DiagramResult getNetwork(ServiceRepository serviceRepository, String loadBalancerArn, String targetIp) {
				
		LoadBalancerNetwork loadBalancerNetwork = new LoadBalancerNetwork(loadBalancerArn, serviceRepository);
		CheckResults<LoadBalancer> checkResults = loadBalancerNetwork.checkCommunication(targetIp);
		LoadBalancer loadBalancer = checkResults.getResource();
		
		Vpc vpc = serviceRepository.getVpcMap().get(loadBalancer.vpcId());
		
		DiagramResult diagramResult = new DiagramResult(vpc.vpcId(), targetIp == null);
		
		diagramResult.addNode(
				new DiagramData<DiagramNode>(
						new DiagramNode(AWS, "")
				).addClass(NodeType.AWS)
		);
		
		diagramResult.addNode(
				new DiagramData<DiagramNode>(
						new DiagramNode(vpc.vpcId(), vpc.vpcId(), AWS)
				).addClass(NodeType.VPC)
		);
		
		String serverId = checkResults.getCidr();
		if(serverId != null) {
			diagramResult.addNode(new DiagramData<DiagramNode>(new DiagramNode(serverId, serverId)).addClass(NodeType.SERVER));
		}
		
		List<String> routeTableIds = this.setRouteTable(serviceRepository, serverId, checkResults.get(CheckType.ROUTE_TABLE), diagramResult);
		for(String routeTableId : routeTableIds) {
			String networkAclId = this.setNetworkAcl(routeTableId, checkResults.get(CheckType.NETWORK_ACL), diagramResult);
			this.setSecurityGroup(networkAclId, loadBalancer, checkResults.get(CheckType.SECURITY_GROUP), diagramResult);
		}
		
		diagramResult.addNode(
				new DiagramData<DiagramNode>(
						new DiagramNode(loadBalancer.loadBalancerArn(), loadBalancer, vpc.vpcId())
				).addClass(NodeType.getLoadBalancerType(loadBalancer.type()))
		);
		
		this.setLoadBalancer(serviceRepository, loadBalancer, diagramResult);
		
		return diagramResult;
	}
	
	private void setSecurityGroup(String networkAclId, LoadBalancer loadBalancer, CheckResult securityGroupCheckResult, DiagramResult diagramResult) {
		if(loadBalancer.type() == LoadBalancerTypeEnum.NETWORK) {
			DiagramEdge serverEdge = new DiagramEdge(networkAclId, loadBalancer.loadBalancerArn());
			serverEdge.setLabel("");
			serverEdge.setBoth(true);
			diagramResult.addEdge(new DiagramData<DiagramEdge>(serverEdge));
			
		} else {

			for(CheckRule checkRule : securityGroupCheckResult.getAllRules()) {
				if(checkRule instanceof SecurityGroupCheckRule) {
					SecurityGroupCheckRule securityGroupCheckRule = (SecurityGroupCheckRule) checkRule;
					
					diagramResult.addNode(
							new DiagramData<DiagramNode>(
									new DiagramNode(securityGroupCheckRule.getId(), securityGroupCheckRule.getSecurityGroup(), diagramResult.getVpcId())
							).addClass(NodeType.SECURITY_GROUP)
					);
					
					String port = "";
					if("-1".equals(securityGroupCheckRule.getPrototol())) {
						port = "All Traffic";
					} else if(securityGroupCheckRule.getFromPort() == -1 && securityGroupCheckRule.getToPort() == -1){
						port = securityGroupCheckRule.getPrototol();
					} else if(securityGroupCheckRule.getFromPort() == securityGroupCheckRule.getToPort()) {
						port = securityGroupCheckRule.getPrototol() + ":" + securityGroupCheckRule.getToPort();
					} else {
						port = securityGroupCheckRule.getPrototol() + ":" + securityGroupCheckRule.getFromPort() + "-" + securityGroupCheckRule.getToPort();
					}
					
					DiagramEdge diagramEdge = new DiagramEdge(networkAclId, securityGroupCheckRule.getId());
					DiagramEdge serverEdge = new DiagramEdge(securityGroupCheckRule.getId(), loadBalancer.loadBalancerArn());
					serverEdge.setLabel(port);
					diagramEdge.setLabel(securityGroupCheckRule.getCidr() + "\n" + port);
					if(securityGroupCheckRule.getDirectionType() == DirectionType.INGRESS) {
						diagramEdge.setIn(true);
						serverEdge.setIn(true);
					} else {
						diagramEdge.setOut(true);
						serverEdge.setOut(true);
					}
					diagramResult.addEdge(new DiagramData<DiagramEdge>(diagramEdge));
					diagramResult.addEdge(new DiagramData<DiagramEdge>(serverEdge));
					
				}
			}
			
			if(securityGroupCheckResult.getAllRules().size() < 1) {
				DiagramEdge serverEdge = new DiagramEdge(networkAclId, loadBalancer.loadBalancerArn());
				serverEdge.setLabel("Not allow in SecurityGroup");
				serverEdge.setBoth(false);
				diagramResult.addEdge(new DiagramData<DiagramEdge>(serverEdge));
			}
		}
		
	}

	private void setLoadBalancer(ServiceRepository serviceRepository, LoadBalancer loadBalancer, DiagramResult diagramResult) {

		List<Listener> listeners = serviceRepository.getLoadBalancerListenersMap().get(loadBalancer.loadBalancerArn());
		for (Listener listener : listeners) {
			
			List<Rule> rules = serviceRepository.getLoadBalancerRulesMap().get(listener.listenerArn());
			for (Rule rule : rules) {
				
				List<Action> actions = rule.actions();
				
				StringBuilder hostString = new StringBuilder();
				StringBuilder pathString = new StringBuilder();
				List<RuleCondition> ruleConditions = rule.conditions();
				for(RuleCondition ruleCondition : ruleConditions) {
					if(ruleCondition.hostHeaderConfig() != null) {
						for(String host : ruleCondition.hostHeaderConfig().values()) {
							if(hostString.length() > 0) {
								hostString.append("\n");
							}
							hostString.append(host);
						}
					}
					
					if(ruleCondition.pathPatternConfig() != null) {
						for(String path : ruleCondition.pathPatternConfig().values()) {
							if(pathString.length() > 0) {
								pathString.append("\n");
							}
							pathString.append(path);
						}
					}
				}
				
				for(Action action : actions) {
					
					switch(action.type()) {
					case FORWARD :
						TargetGroup targetGroup = serviceRepository.getTargetGroupMap().get(action.targetGroupArn());
						
						diagramResult.addNode(
								new DiagramData<DiagramNode>(
										new DiagramNode(targetGroup.targetGroupArn(), targetGroup, diagramResult.getVpcId())
								).addClass(NodeType.TARGET_GROUP)
						);
						DiagramEdge diagramTargetEdge = new DiagramEdge(loadBalancer.loadBalancerArn(), targetGroup.targetGroupArn());
						diagramTargetEdge.setLabel(
								(ruleConditions.size() == 0 ? "Default " : "")
								+ (hostString.length() > 0 ? (hostString.toString() + "->") : "")
								+ (pathString.length() > 0 ? (pathString.toString() + "->") : "")
								+ listener.protocolAsString() + ":" + listener.port()
						);
						diagramTargetEdge.setBoth(true);
						diagramResult.addEdge(new DiagramData<DiagramEdge>(diagramTargetEdge));
						
						List<TargetHealthDescription> targetHealthDescriptions = serviceRepository.getTargetHealthDescriptionsMap().get(targetGroup.targetGroupArn());
						
						for (TargetHealthDescription targetHealthDescription : targetHealthDescriptions) {
							TargetDescription targetDescription = targetHealthDescription.target();

							switch(targetGroup.targetType()) {
							case INSTANCE :
								Instance instance = serviceRepository.getEc2InstanceMap().get(targetDescription.id()); 
								diagramResult.addNode(
										new DiagramData<DiagramNode>(
												new DiagramNode(targetDescription.id(), instance, diagramResult.getVpcId())
										).addClass(NodeType.EC2_INSTANCE)
								);
								break;
							case IP :
								diagramResult.addNode(
										new DiagramData<DiagramNode>(
												new DiagramNode(targetDescription.id(), targetDescription.id(), diagramResult.getVpcId())
										).addClass(NodeType.SERVER)
								);
								break;
							case LAMBDA :
								diagramResult.addNode(
										new DiagramData<DiagramNode>(
												new DiagramNode(targetDescription.id(), targetDescription.id(), diagramResult.getVpcId())
										).addClass(NodeType.LAMBDA)
								);
								break;
							default :
								break;
							}
							
							if(targetGroup.targetType() != TargetTypeEnum.UNKNOWN_TO_SDK_VERSION) {
								DiagramEdge diagramEdge = new DiagramEdge(targetGroup.targetGroupArn(), targetDescription.id());
								diagramEdge.setLabel(listener.protocolAsString() + ":" + listener.port() + "->" + targetDescription.port());
								diagramEdge.setBoth(targetHealthDescription.targetHealth().state() == TargetHealthStateEnum.HEALTHY);
								diagramResult.addEdge(new DiagramData<DiagramEdge>(diagramEdge));
							}
						}						
							
						break;
					case REDIRECT :
						RedirectActionConfig redirectActionConfig = action.redirectConfig();
						diagramResult.addNode(new DiagramData<DiagramNode>(
									new DiagramNode(Integer.toString(redirectActionConfig.hashCode()), new StringBuilder()
																											.append(redirectActionConfig.protocol())
																											.append("://")
																											.append(redirectActionConfig.host())
																											.append(":")
																											.append(redirectActionConfig.port())
																											.append("?")
																											.append(redirectActionConfig.query())
																											.append(" - ")
																											.append(redirectActionConfig.statusCodeAsString()).toString(), diagramResult.getVpcId())
								).addClass(NodeType.INTERNET)
						);
						DiagramEdge diagramEdge = new DiagramEdge(loadBalancer.loadBalancerArn(), Integer.toString(redirectActionConfig.hashCode()));
						diagramEdge.setLabel(
								new StringBuilder()
								.append(listener.protocolAsString())
								.append(":")
								.append(listener.port()).toString()
						);
						diagramEdge.setBoth(true);
						diagramResult.addEdge(new DiagramData<DiagramEdge>(diagramEdge));
						break;
					case FIXED_RESPONSE :
						FixedResponseActionConfig fixedResponseActionConfig = action.fixedResponseConfig();
						diagramResult.addNode(
								new DiagramData<DiagramNode>(
										new DiagramNode(Integer.toString(fixedResponseActionConfig.hashCode()), fixedResponseActionConfig.contentType(), diagramResult.getVpcId())
								).addClass(NodeType.INTERNET)
						);
						DiagramEdge diagramFixedEdge = new DiagramEdge(loadBalancer.loadBalancerArn(), Integer.toString(fixedResponseActionConfig.hashCode()));
						diagramFixedEdge.setLabel(
								new StringBuilder()
								.append(listener.protocolAsString())
								.append(":")
								.append(listener.port()).toString()
						);
						diagramFixedEdge.setBoth(true);
						diagramResult.addEdge(new DiagramData<DiagramEdge>(diagramFixedEdge));
						break;
					case AUTHENTICATE_COGNITO :
						action.authenticateCognitoConfig();
						break;
					case AUTHENTICATE_OIDC :
						action.authenticateOidcConfig();
						break;
					default :
						break;
					}
				}
			}

		}
	}

}
