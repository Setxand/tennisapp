package com.tennisapp.service;

import com.tennisapp.model.User;
import com.tennisapp.util.DictionaryUtil;
import org.springframework.stereotype.Service;
import telegram.Message;
import telegram.client.TelegramClient;

import javax.transaction.Transactional;

import static com.tennisapp.config.DictionaryKeysConfig.*;

@Service
public class CommandService {

	private final TelegramClient telegramClient;
	private final TennisService tennisService;

	public CommandService(TelegramClient telegramClient, TennisService tennisService, UserService userService) {
		this.telegramClient = telegramClient;
		this.tennisService = tennisService;
	}

	@Transactional
	public void commandToBot(Message message, User user) {
		user.setStatus(null);
		String command = message.getText();

		switch (command) {
			case "/start":
				start(message, user);
				break;

			case "/login":
				login(message, user);
				break;

			case "/booktable":
				tennisService.bookTable(message, user);
				break;

			case "/reentercredentials":
				reenterCredentials(message, user);
				break;

			case "/cancelgame":
				cancelGame(message, user);
				break;

			case "/checkorder": tennisService.sendQueueToCurrentUser(message, user);
				break;

			default:
				throw new RuntimeException();
		}
	}

	private void start(Message message, User user) {
		telegramClient.helloMessage(message);
		login(message, user);
	}

	private void cancelGame(Message message, User user) {
		tennisService.cancelGame(user);
		telegramClient.simpleMessage(DictionaryUtil.getDictionaryValue(GAME_CANCELLED), message);
	}

	public void login(Message message, User user) {

		if (user.getLogin() != null && user.getPassword() != null) {
			tennisService.login(user);
			telegramClient
					.simpleMessage(DictionaryUtil.getDictionaryValue(LOGIN_SUCCESS), message);

		} else {
			telegramClient
					.simpleMessage(DictionaryUtil.getDictionaryValue(LOGIN_PROCESS), message);

			user.setStatus(User.UserStatus.LOGIN);
		}

	}

	private void reenterCredentials(Message message, User user) {
		user.setLogin(null);
		user.setPassword(null);
		login(message, user);
	}
}
