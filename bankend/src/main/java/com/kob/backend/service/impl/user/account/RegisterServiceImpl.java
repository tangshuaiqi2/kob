package com.kob.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegisterServiceImpl implements RegisterService {

	@Autowired(required = false)
	UserMapper userMapper;

	@Autowired(required = false)
	private PasswordEncoder passwordEncoder;


	@Override
	public Map<String, String> register(String username, String password, String confirmedPassword) {
		Map<String, String> map = new HashMap<>();

		if(username.length() == 0){
			map.put("error_message", "用户名不能为空");
			return map;
		}
		if(password == null || confirmedPassword == null){
			map.put("error_message", "密码不能为空");
			return map;
		}
		username.trim();

		if(password.length() == 0){
			map.put("error_message", "密码不能为空");
			return map;
		}

		if(username.length() == 0){
			map.put("error_message", "用户名不能为空");
			return map;
		}

		if(username.length() > 100){
			map.put("error_message", "用户名最多100个字符");
			return map;
		}
		if(password.length() > 100){
			map.put("error_message", "密码最多100个字符");
			return map;
		}
		if(!password.equals(confirmedPassword)){
			map.put("error_message", "两次输入的密码不一致");
			return map;
		}

		QueryWrapper queryWrapper = new QueryWrapper();
		queryWrapper.eq("username", username);
		List<User> users = userMapper.selectList(queryWrapper);

		if(!users.isEmpty()){
			map.put("error_message", "用户名已存在");
			return map;
		}

		String encodedPassword = passwordEncoder.encode(password);
		String photo = "https://cdn.acwing.com/media/user/profile/photo/1_lg_844c66b332.jpg";
		User user = new User(null, username, encodedPassword, photo);

		userMapper.insert(user);

		map.put("error_message", "success");

		return map;
	}
}
