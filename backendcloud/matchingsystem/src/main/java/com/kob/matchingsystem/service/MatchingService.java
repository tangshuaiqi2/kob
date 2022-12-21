package com.kob.matchingsystem.service;

import org.springframework.stereotype.Service;


public interface MatchingService {
	String addPlayer(Integer userId, Integer rating, Integer botId);
	String removePlayer(Integer userId);
}
