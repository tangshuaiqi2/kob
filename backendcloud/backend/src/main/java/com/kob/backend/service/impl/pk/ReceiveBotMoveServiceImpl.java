package com.kob.backend.service.impl.pk;

import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.service.pk.ReceiveBotMoveService;
import org.springframework.stereotype.Service;

@Service
public class ReceiveBotMoveServiceImpl implements ReceiveBotMoveService {

	@Override
	public String reveiveBotMove(Integer userId, Integer direction) {
		System.out.println("reveive: " + userId + " " + direction + " ");
		if(WebSocketServer.users.get(userId) != null){
			Game game = WebSocketServer.users.get(userId).game;
			if(game != null){
				if(game.getPlayA().getId().equals(userId)){
						game.setNextStepA(direction);
				}else if(game.getPlayB().getId().equals(userId)){
						game.setNextStepB(direction);
				}
			}
		}
		return "receive bot move success";
	}
}
