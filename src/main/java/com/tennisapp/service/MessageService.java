package com.tennisapp.service;

import com.tennisapp.model.User;
import org.springframework.stereotype.Service;
import telegram.Message;

import javax.transaction.Transactional;

@Service
public class MessageService {

	private final CommandService commandService;

	public MessageService(CommandService commandService) {
		this.commandService = commandService;
	}

	@Transactional
	public void messageFromBot(Message message, User user) {

		if (message.getText().contains("/")) {
			commandService.commandToBot(message, user);

		} else if (user.getStatus() != null) {
			checkByStatus(message, user);
		}
	}

	private void checkByStatus(Message message, User user) {

		switch (user.getStatus()) {
			case LOGIN:
				loginFinalStep(message, user);
				break;

			default:
				throw new RuntimeException();
		}

	}

	private void loginFinalStep(Message message, User user) {
		String[] credentials = message.getText().split(" ");

		user.setLogin(credentials[0].trim());
		user.setPassword(credentials[1].trim());

		commandService.login(message, user);

		user.setStatus(null);
	}
}
