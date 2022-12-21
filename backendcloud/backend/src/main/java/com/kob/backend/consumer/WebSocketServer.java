package com.kob.backend.consumer;
import com.alibaba.fastjson.JSONObject;
import com.kob.backend.config.RestTemplateConfig;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {

	public final static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();


	private User user;
	private Session session = null;

	public static  UserMapper userMapper;
	public static RecordMapper recordMapper;
	public static RestTemplate restTemplate;

	private Game game = null;

	private final static  String addPlayerUrl = "http://127.0.0.1:3001/player/add/";
	private final static  String removePlayerUrl = "http://127.0.0.1:3001/player/remove/";



	@Autowired
	public void setUserMapper(UserMapper userMapper){
		WebSocketServer.userMapper = userMapper;
	}

	@Autowired
	public void setRecordMapper(RecordMapper recordMapper){
		WebSocketServer.recordMapper = recordMapper;
	}

	@Autowired
	public void setRestTemplate(RestTemplate restTemplate){
		WebSocketServer.restTemplate = restTemplate;
	}

	@OnOpen
	public void onOpen(Session session, @PathParam("token") String token) throws IOException {
		// 建立连接
		this.session = session;
		System.out.println("connected!");

		Integer userId = JwtAuthentication.getUserId(token);
		this.user = userMapper.selectById(userId);

		if(this.user != null){
			users.put(userId, this);
			System.out.println(user);
		}else{
			this.session.close();
		}



	}

	public static void startGame(Integer aId, Integer bId){
		User a = userMapper.selectById(aId), b = userMapper.selectById(bId);
		Game game = new Game(13,14,20, a.getId(), b.getId());
		game.createMap();

		game.start();
		if(users.get(a.getId()) != null){
			users.get(a.getId()).game = game;
		}

		if(users.get(b.getId())!=null){
			users.get(b.getId()).game = game;
		}

		JSONObject gamemap = new JSONObject();
		gamemap.put("a_id", game.getPlayA().getId());
		gamemap.put("a_sx", game.getPlayA().getSx());
		gamemap.put("a_sy", game.getPlayA().getSy());
		gamemap.put("b.id", game.getPlayB().getId());
		gamemap.put("b_sx", game.getPlayB().getSx());
		gamemap.put("b_sy", game.getPlayA().getSy());
		gamemap.put("map", game.getG());

		JSONObject respA = new JSONObject();
		respA.put("event", "start-pk");
		respA.put("opponent_username", b.getUsername());
		respA.put("opponent_photo", b.getPhoto());
		respA.put("game", gamemap);
		if(users.get(a.getId()) != null){
			users.get(a.getId()).sendMessage(respA.toJSONString());
		}



		JSONObject respB= new JSONObject();
		respB.put("event", "start-pk");
		respB.put("opponent_username", a.getUsername());
		respB.put("opponent_photo", a.getPhoto());
		respB.put("game", gamemap);
		if(users.get(b.getId()) != null){
			users.get(b.getId()).sendMessage(respB.toJSONString());
		}

	}

	private void startMatcing(){
		System.out.println("startMatcing");
		MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
		data.add("user_id", this.user.getId().toString());
		data.add("rating", this.user.getRating().toString());
		restTemplate.postForObject(addPlayerUrl,data, String.class);
	}

	private void stopMatcing(){

		System.out.println("stopMatcing");
		MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
		data.add("user_id", this.user.getId().toString());
		restTemplate.postForObject(removePlayerUrl,data, String.class);
	}



	@OnClose
	public void onClose() {
		// 关闭链接
		System.out.println("disconnected!");
		if(this.user != null){
			users.remove(this.user.getId());
		}

	}

	private void move(int direction){
		if(game.getPlayA().getId().equals(user.getId())){
			game.setNextStepA(direction);
		}else if(game.getPlayB().getId().equals(user.getId())){
			game.setNextStepB(direction);
		}
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		// 从Client接收消息
		System.out.println("receive message!");
		JSONObject data = JSONObject.parseObject(message);
		String event = data.getString("event");
		if("start-matching".equals(event)){
			startMatcing();
		}else if("stop-matching".equals(event)){
			stopMatcing();
		}else if("move".equals(event)){
			move(data.getInteger("direction"));
		}
	}

	@OnError
	public void onError(Session session, Throwable error) {
		error.printStackTrace();
	}

	public void sendMessage(String message){
		synchronized(this.session){
			try{
				this.session.getBasicRemote().sendText(message);
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
}