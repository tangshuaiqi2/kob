package com.kob.backend.consumer;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.stereotype.Component;

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

	private final static CopyOnWriteArraySet<User> matchpool = new CopyOnWriteArraySet<>();

	private User user;
	private Session session = null;

	public static  UserMapper userMapper;
	public static RecordMapper recordMapper;

	private Game game = null;

	@Autowired
	public void setUserMapper(UserMapper userMapper){
		WebSocketServer.userMapper = userMapper;
	}

	@Autowired
	public void setRecordMapper(RecordMapper recordMapper){
		WebSocketServer.recordMapper = recordMapper;
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

	private void startMatcing(){
		System.out.println("startMatcing");
		matchpool.add(this.user);

		while(matchpool.size() >= 2){
			Iterator<User> it = matchpool.iterator();
			User a = it.next(), b = it.next();
			matchpool.remove(a);
			matchpool.remove(b);

			Game game = new Game(13,14,20, a.getId(), b.getId());
			game.createMap();

			game.start();

			users.get(a.getId()).game = game;
			users.get(b.getId()).game = game;

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
			users.get(a.getId()).sendMessage(respA.toJSONString());


			JSONObject respB= new JSONObject();
			respB.put("event", "start-pk");
			respB.put("opponent_username", a.getUsername());
			respB.put("opponent_photo", a.getPhoto());
			respB.put("game", gamemap);
			users.get(b.getId()).sendMessage(respB.toJSONString());

		}
	}

	private void stopMatcing(){
		System.out.println("stopMatcing");
		matchpool.remove(this.user);
	}



	@OnClose
	public void onClose() {
		// 关闭链接
		System.out.println("disconnected!");
		if(this.user != null){
			users.remove(this.user.getId());
			matchpool.remove(this.user);
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