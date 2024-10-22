package com.anthunt.aws.network.service.aws;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import com.anthunt.aws.network.repository.ServiceRepository;
import com.anthunt.aws.network.repository.aws.AwsData;
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
import com.anthunt.aws.network.utils.Logging;
import com.anthunt.aws.network.utils.Utils;

import software.amazon.awssdk.services.ec2.model.GroupIdentifier;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.SecurityGroup;
import software.amazon.awssdk.services.ec2.model.Vpc;
import software.amazon.awssdk.services.elasticloadbalancing.ElasticLoadBalancingAsyncClient;
import software.amazon.awssdk.services.elasticloadbalancing.model.LoadBalancerDescription;
import software.amazon.awssdk.services.elasticloadbalancingv2.ElasticLoadBalancingV2AsyncClient;
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

@Service
public class LoadBalancerService extends AbstractNetworkService {

	private static final Logger log = Logging.getLogger(LoadBalancerService.class);
			
	private ElasticLoadBalancingAsyncClient getElasticLoadBalancingClient(SessionProfile sessionProfile) {
		return ElasticLoadBalancingAsyncClient.builder()
			   .credentialsProvider(sessionProfile.getProfileCredentialsProvider())
			   .region(sessionProfile.getRegion())
			   .build();
	}
	
	private ElasticLoadBalancingV2AsyncClient getElasticLoadBalancingV2Client(SessionProfile sessionProfile) {
		return ElasticLoadBalancingV2AsyncClient.builder()
				   .credentialsProvider(sessionProfile.getProfileCredentialsProvider())
				   .region(sessionProfile.getRegion())
				   .build();
	}
	
	public ServiceMap getClassicLoadBalancers(SessionProfile sessionProfile) {
		ServiceMap classicLoadBalancerMap = sessionProfile.serviceMap();
		ElasticLoadBalancingAsyncClient elasticLoadBalancingClient = this.getElasticLoadBalancingClient(sessionProfile);
		for(LoadBalancerDescription loadBalancerDescription : elasticLoadBalancingClient.describeLoadBalancers().join().loadBalancerDescriptions()) {
			classicLoadBalancerMap.put(sessionProfile.getUserid(), sessionProfile.getProfileName(), sessionProfile.getRegion(), loadBalancerDescription.loadBalancerName(), loadBalancerDescription, LoadBalancerDescription.class);
		}
		return classicLoadBalancerMap;
	}
	
	public ServiceMap getLoadBalancers(SessionProfile sessionProfile) {
		ServiceMap loadBalancerMap = sessionProfile.serviceMap();
		ElasticLoadBalancingV2AsyncClient elasticLoadBalancingV2Client = this.getElasticLoadBalancingV2Client(sessionProfile);
		for(LoadBalancer loadBalancer : elasticLoadBalancingV2Client.describeLoadBalancers().join().loadBalancers()) {
			loadBalancerMap.put(sessionProfile.getUserid(), sessionProfile.getProfileName(), sessionProfile.getRegion(), loadBalancer.loadBalancerArn(), loadBalancer, LoadBalancer.class);
		}
		return loadBalancerMap;
	}
	
	public ServiceMap getLoadBalancerListeners(SessionProfile sessionProfile, List<AwsData> loadBalancers) {
		ServiceMap loadBalancerListenerMap = sessionProfile.serviceMap(true);
		ElasticLoadBalancingV2AsyncClient elasticLoadBalancingV2Client = this.getElasticLoadBalancingV2Client(sessionProfile);
		for(AwsData awsData : loadBalancers) {
			LoadBalancer loadBalancer = awsData.getData();
			DescribeListenersResponse describeListenersResponse = elasticLoadBalancingV2Client.describeListeners(
					DescribeListenersRequest.builder()
						.loadBalancerArn(loadBalancer.loadBalancerArn())
						.build()
			).join();
			loadBalancerListenerMap.put(sessionProfile.getUserid(), sessionProfile.getProfileName(), sessionProfile.getRegion(), loadBalancer.loadBalancerArn(), describeListenersResponse.listeners(), Listener.class);
			Utils.sleep(100);
		}
		
		return loadBalancerListenerMap;
	}
	
	public ServiceMap getLoadBalancerRules(SessionProfile sessionProfile, List<AwsData> listenerss) {
		ServiceMap loadBalancerRuleMap = sessionProfile.serviceMap(true);
		ElasticLoadBalancingV2AsyncClient elasticLoadBalancingV2Client = this.getElasticLoadBalancingV2Client(sessionProfile);		
		for(AwsData awsData : listenerss) {
			List<Listener> listeners = awsData.getDataList();
			for(Listener listener : listeners) {
				DescribeRulesResponse describeRulesResponse = elasticLoadBalancingV2Client.describeRules(
						DescribeRulesRequest.builder()
											.listenerArn(listener.listenerArn())
											.build()
				).join();
				loadBalancerRuleMap.put(sessionProfile.getUserid(), sessionProfile.getProfileName(), sessionProfile.getRegion(), listener.listenerArn(), describeRulesResponse.rules(), Rule.class);
				Utils.sleep(100);
			}
		}
		return loadBalancerRuleMap;
	}
	
	public ServiceMap getTargetGroups(SessionProfile sessionProfile) {
		ServiceMap targetGroupMap = sessionProfile.serviceMap();
		ElasticLoadBalancingV2AsyncClient elasticLoadBalancingV2Client = this.getElasticLoadBalancingV2Client(sessionProfile);
		for(TargetGroup targetGroup : elasticLoadBalancingV2Client.describeTargetGroups().join().targetGroups()) {
			targetGroupMap.put(sessionProfile.getUserid(), sessionProfile.getProfileName(), sessionProfile.getRegion(), targetGroup.targetGroupArn(), targetGroup, TargetGroup.class);
		}
		return targetGroupMap;
	}
	
	public ServiceMap getTargetHealthDescriptions(SessionProfile sessionProfile, List<AwsData> targetGroups) {
		ServiceMap targetHealthDescriptionsMap = sessionProfile.serviceMap(true);
		ElasticLoadBalancingV2AsyncClient elasticLoadBalancingV2Client = this.getElasticLoadBalancingV2Client(sessionProfile);		
		for(AwsData awsData : targetGroups) {
			TargetGroup targetGroup = awsData.getData();
			DescribeTargetHealthResponse describeTargetHealthResponse = elasticLoadBalancingV2Client.describeTargetHealth(
					DescribeTargetHealthRequest.builder()
						.targetGroupArn(targetGroup.targetGroupArn())
						.build()
			).join();
			targetHealthDescriptionsMap.put(sessionProfile.getUserid(), sessionProfile.getProfileName(), sessionProfile.getRegion(), targetGroup.targetGroupArn(), describeTargetHealthResponse.targetHealthDescriptions(), TargetHealthDescription.class);
			Utils.sleep(100);
		}
		return targetHealthDescriptionsMap;
	}
	
	public List<AwsData> getClassicLoadBalancers(SessionProfile sessionProfile, ServiceRepository serviceRepository) {
		return serviceRepository.getClassicLoadBalancerMap().values(sessionProfile, LoadBalancerDescription.class);
	}
	
	public List<AwsData> getLoadBalancers(SessionProfile sessionProfile, ServiceRepository serviceRepository) {
		return serviceRepository.getLoadBalancerMap().values(sessionProfile, LoadBalancer.class);
	}

	public DiagramResult getNetwork(SessionProfile sessionProfile, ServiceRepository serviceRepository, String loadBalancerArn, String targetIp) {
				
		log.trace(loadBalancerArn);
		LoadBalancerNetwork loadBalancerNetwork = new LoadBalancerNetwork(sessionProfile, loadBalancerArn, serviceRepository);
		CheckResults<LoadBalancer> checkResults = loadBalancerNetwork.checkCommunication(targetIp);
		LoadBalancer loadBalancer = checkResults.getResource();
		
		Vpc vpc = serviceRepository.getVpcMap().get(sessionProfile, loadBalancer.vpcId(), Vpc.class).get().getData();
		
		DiagramResult diagramResult = new DiagramResult(vpc.vpcId(), targetIp == null);
		
//		diagramResult.addNode(
//				new DiagramData<DiagramNode>(
//						new DiagramNode(AWS, "")
//				).addClass(NodeType.AWS)
//		);
//		
//		diagramResult.addNode(
//				new DiagramData<DiagramNode>(
//						new DiagramNode(vpc.vpcId(), vpc.vpcId(), AWS)
//				).addClass(NodeType.VPC)
//		);
		
		String serverId = checkResults.getCidr();
		if(serverId != null) {
			diagramResult.addNode(new DiagramData<DiagramNode>(new DiagramNode(serverId, serverId)).addClass(NodeType.SERVER));
		}
		
		List<String> routeTableIds = this.setRouteTable(sessionProfile, serviceRepository, serverId, checkResults.get(CheckType.ROUTE_TABLE), diagramResult);
		for(String routeTableId : routeTableIds) {
			String networkAclId = this.setNetworkAcl(routeTableId, checkResults.get(CheckType.NETWORK_ACL), diagramResult);
			this.setSecurityGroup(networkAclId, loadBalancer, checkResults.get(CheckType.SECURITY_GROUP), diagramResult);
		}
		
		diagramResult.addNode(
				new DiagramData<DiagramNode>(
						new DiagramNode(loadBalancer.loadBalancerArn(), loadBalancer)
				).addClass(NodeType.getLoadBalancerType(loadBalancer.type()))
		);
		
		this.setLoadBalancer(sessionProfile, serviceRepository, targetIp, loadBalancerNetwork, loadBalancer, diagramResult);
		
		return diagramResult;
	}
	
	private void setSecurityGroup(String networkAclId, LoadBalancer loadBalancer, CheckResult securityGroupCheckResult, DiagramResult diagramResult) {
		if(loadBalancer.type() == LoadBalancerTypeEnum.NETWORK) {
			diagramResult.addEdge(new DiagramData<DiagramEdge>(
					DiagramEdge.make(networkAclId, loadBalancer.loadBalancerArn())
							   .setLabel("")
							   .setBoth(true)
			));
			
		} else {
			this.setSecurityGroupSet(networkAclId, loadBalancer.loadBalancerArn(), securityGroupCheckResult, diagramResult);
		}
		
	}
	
	private void setSecurityGroupSet(String sourceId, String targetId, CheckResult securityGroupCheckResult, DiagramResult diagramResult) {
		
		for(CheckRule checkRule : securityGroupCheckResult.getAllRules()) {
			if(checkRule instanceof SecurityGroupCheckRule) {
				SecurityGroupCheckRule securityGroupCheckRule = (SecurityGroupCheckRule) checkRule;
				
				diagramResult.addNode(
						new DiagramData<DiagramNode>(
								new DiagramNode(targetId + "." + securityGroupCheckRule.getId(), securityGroupCheckRule.getSecurityGroup())
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
				
				DiagramEdge diagramEdge = DiagramEdge.make(sourceId, targetId + "." + securityGroupCheckRule.getId()).setLabel(securityGroupCheckRule.getCidr() + "\n" + port);
				DiagramEdge serverEdge = DiagramEdge.make(targetId + "." + securityGroupCheckRule.getId(), targetId).setLabel(port);
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
			diagramResult.addEdge(new DiagramData<DiagramEdge>(
					DiagramEdge.make(sourceId, targetId)
							   .setLabel("Not allow in SecurityGroup")
							   .setBoth(false)
			));
		}
		
	}

	private void setLoadBalancer(SessionProfile sessionProfile, ServiceRepository serviceRepository, String targetIp, LoadBalancerNetwork loadBalancerNetwork, LoadBalancer loadBalancer, DiagramResult diagramResult) {

		List<Listener> listeners = serviceRepository.getLoadBalancerListenersMap().get(sessionProfile, loadBalancer.loadBalancerArn(), Listener.class).get().getData();
		for (Listener listener : listeners) {
			
			List<Rule> rules = serviceRepository.getLoadBalancerRulesMap().get(sessionProfile, listener.listenerArn(), Rule.class).get().getDataList();
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
						TargetGroup targetGroup = serviceRepository.getTargetGroupMap().get(sessionProfile, action.targetGroupArn(), TargetGroup.class).get().getData();
						
						diagramResult.addNode(
								new DiagramData<DiagramNode>(
										new DiagramNode(targetGroup.targetGroupArn(), targetGroup)
								).addClass(NodeType.TARGET_GROUP)
						);
 
						diagramResult.addEdge(new DiagramData<DiagramEdge>(
								DiagramEdge.make(loadBalancer.loadBalancerArn(), targetGroup.targetGroupArn())
										   .setLabel(
												(ruleConditions.size() == 0 ? "Default " : "")
												+ (hostString.length() > 0 ? (hostString.toString() + "->") : "")
												+ (pathString.length() > 0 ? (pathString.toString() + "->") : "")
												+ listener.protocolAsString() + ":" + listener.port()
										   )
										   .setBoth(true)
						));
						
						List<TargetHealthDescription> targetHealthDescriptions = serviceRepository.getTargetHealthDescriptionsMap().get(sessionProfile, targetGroup.targetGroupArn(), TargetHealthDescription.class).get().getDataList();
						
						for (TargetHealthDescription targetHealthDescription : targetHealthDescriptions) {
							TargetDescription targetDescription = targetHealthDescription.target();

							switch(targetGroup.targetType()) {
							case INSTANCE :
								this.setTargetInstance(sessionProfile, serviceRepository, targetIp, loadBalancerNetwork, loadBalancer, listener, targetGroup, targetDescription, targetHealthDescription, diagramResult);
								break;
							case IP :
								diagramResult.addNode(
										new DiagramData<DiagramNode>(
												new DiagramNode(targetDescription.id(), targetDescription.id())
										).addClass(NodeType.SERVER)
								);
								diagramResult.addEdge(new DiagramData<DiagramEdge>(
										DiagramEdge.make(targetGroup.targetGroupArn(), targetDescription.id())
												   .setLabel(listener.protocolAsString() + ":" + listener.port() + "->" + targetDescription.port())
												   .setBoth(targetHealthDescription.targetHealth().state() == TargetHealthStateEnum.HEALTHY)
								));
								break;
							case LAMBDA :
								diagramResult.addNode(
										new DiagramData<DiagramNode>(
												new DiagramNode(targetDescription.id(), targetDescription.id())
										).addClass(NodeType.LAMBDA)
								);
								diagramResult.addEdge(new DiagramData<DiagramEdge>(
										DiagramEdge.make(targetGroup.targetGroupArn(), targetDescription.id())
												   .setLabel(listener.protocolAsString() + ":" + listener.port())
												   .setBoth(true)
								));
								break;
							default :
								break;
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
																											.append(redirectActionConfig.statusCodeAsString()).toString())
								).addClass(NodeType.INTERNET)
						);
						diagramResult.addEdge(new DiagramData<DiagramEdge>(
								DiagramEdge.make(loadBalancer.loadBalancerArn(), Integer.toString(redirectActionConfig.hashCode()))
										.setLabel(
												new StringBuilder()
												.append(listener.protocolAsString())
												.append(":")
												.append(listener.port()).toString()
										)
										.setBoth(true)
						));
						break;
					case FIXED_RESPONSE :
						FixedResponseActionConfig fixedResponseActionConfig = action.fixedResponseConfig();
						diagramResult.addNode(
								new DiagramData<DiagramNode>(
										new DiagramNode(Integer.toString(fixedResponseActionConfig.hashCode()), fixedResponseActionConfig.contentType())
								).addClass(NodeType.INTERNET)
						);
						diagramResult.addEdge(new DiagramData<DiagramEdge>(
								DiagramEdge.make(loadBalancer.loadBalancerArn(), Integer.toString(fixedResponseActionConfig.hashCode()))
										.setLabel(
												new StringBuilder()
												.append(listener.protocolAsString())
												.append(":")
												.append(listener.port()).toString()
										)
										.setBoth(true)
						));
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
	
	private void setTargetInstance(SessionProfile sessionProfile, ServiceRepository serviceRepository, String targetIp, LoadBalancerNetwork loadBalancerNetwork, LoadBalancer loadBalancer, Listener listener, TargetGroup targetGroup, TargetDescription targetDescription, TargetHealthDescription targetHealthDescription, DiagramResult diagramResult) {
		serviceRepository
				.getEc2InstanceMap().get(sessionProfile, targetDescription.id(), Instance.class)
				.ifPresent(o -> this.setTargetInstance(sessionProfile, serviceRepository, targetIp, loadBalancerNetwork, loadBalancer, listener, targetGroup, targetDescription, targetHealthDescription, diagramResult, o.getData()));
	}

	private void setTargetInstance(SessionProfile sessionProfile, ServiceRepository serviceRepository, String targetIp, LoadBalancerNetwork loadBalancerNetwork, LoadBalancer loadBalancer, Listener listener, TargetGroup targetGroup, TargetDescription targetDescription, TargetHealthDescription targetHealthDescription, DiagramResult diagramResult, Instance instance) {
		String targetPortId = targetDescription.id() + "." + targetDescription.port();
		
		Map<String, List<SecurityGroupCheckRule>> sgRulesMap = new HashMap<>();		
		for(GroupIdentifier groupIdentifier : instance.securityGroups()) {
			SecurityGroup securityGroup = serviceRepository.getSecurityGroupMap().get(sessionProfile, groupIdentifier.groupId(), SecurityGroup.class).get().getData();
			sgRulesMap.put(securityGroup.groupName(), loadBalancerNetwork.getSecurityGroupRules(securityGroup));
		}
		
		List<String> targetIps = new ArrayList<>();
		
		if(loadBalancer.type() == LoadBalancerTypeEnum.APPLICATION) {
			
			//ALB인 경우 targetIp를 ALB의 해당 Region에 대한 IP로 NAT 처리 필요
			try {
				InetAddress[] inetAddresses = InetAddress.getAllByName(loadBalancer.dnsName());
				for(InetAddress inetAddress : inetAddresses) {
					targetIps.add(inetAddress.getHostAddress());
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}

		} else {
			targetIps.add(targetIp);
		}
		
		CheckResult securityGroupCheckResult = null;		
		if(targetIp == null) {
			securityGroupCheckResult = loadBalancerNetwork.getAllSecurityGroup(sgRulesMap);
		} else {
			securityGroupCheckResult = loadBalancerNetwork.checkSecurityGroup(sgRulesMap, targetIps);
		}		
		this.setSecurityGroupSet(targetPortId, targetDescription.id(), securityGroupCheckResult, diagramResult);
		
		diagramResult.addNode(
				new DiagramData<DiagramNode>(
						new DiagramNode(targetPortId, Utils.getNameTag(Utils.getNameFromTags(instance.tags()), targetDescription.id()) + "\n" + targetDescription.port() + " port")
				).addClass(NodeType.NETWORK_INTERFACE)
		);
		
		diagramResult.addEdge(new DiagramData<DiagramEdge>(
				DiagramEdge.make(targetGroup.targetGroupArn(), targetPortId)
						   .setLabel(listener.protocolAsString() + ":" + listener.port() + " port")
						   .setBoth(targetHealthDescription.targetHealth().state() == TargetHealthStateEnum.HEALTHY)
		));
		
		diagramResult.addNode(
				new DiagramData<DiagramNode>(
						new DiagramNode(targetDescription.id(), instance)
				).addClass(NodeType.EC2_INSTANCE)
		);
		
	}

}
