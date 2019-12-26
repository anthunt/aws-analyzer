package com.anthunt.aws.network.controller.api;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anthunt.aws.network.repository.model.ServiceStatistic;
import com.anthunt.aws.network.session.SessionHandler;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController extends AbstractAPIController {

	@RequestMapping("")
	public List<ServiceStatistic> getServiceStatistics(HttpSession session) {
		return SessionHandler.getSessionServiceRepository(session).getServiceStatistic();		
	}
	
}
