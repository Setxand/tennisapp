package com.tennisapp.service;


import com.tennisapp.model.User;
import com.tennisapp.repository.UserRepository;
import com.tennisapp.util.DictionaryUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import telegram.Message;

import java.util.List;
import java.util.Set;

import static com.tennisapp.config.DictionaryKeysConfig.TENNIS_ID_INVALID;
import static com.tennisapp.config.DictionaryKeysConfig.USER_ID_INVALID;

@Service
public class UserService {


	private final Integer ADMIN_ID;
	private final UserRepository userRepository;

	public UserService(@Value("${id.admin}") String adminId, UserRepository userRepository) {
		ADMIN_ID = Integer.valueOf(adminId);
		this.userRepository = userRepository;
	}


	public User createUser(Message message) {
		return userRepository.findById(message.getChat().getId()).orElseGet(() -> {
			User user = new User();
			user.setChatId(message.getChat().getId());
			return userRepository.saveAndFlush(user);
		});
	}

	public String getUserAdminCookie() {
		return getUser(ADMIN_ID).getLoginCookie();
	}

	public User getUser(Integer userId) {
		return userRepository.findById(userId).orElseThrow(
				() -> new IllegalArgumentException(DictionaryUtil.getDictionaryValue(USER_ID_INVALID)));
	}

	public User getUserByTennisId(String tennisId) {
		return userRepository.findByTennisId(tennisId)
				.orElseThrow(() -> new IllegalArgumentException(DictionaryUtil.getDictionaryValue(TENNIS_ID_INVALID)));
	}

	public List<User> findUsersByTennisId(Set<String> keySet) {
		return userRepository.findUsersByTennisIdIn(keySet);
	}
}
