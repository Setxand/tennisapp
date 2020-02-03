package com.tennisapp.exception;

import com.tennisapp.client.TelegramClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@Autowired TelegramClient telegramClient;

	@ExceptionHandler(BotException.class)
	public void handleBotException(final BotException ex) {
		telegramClient.simpleMessage(ex.getMessage(), ex.getTelegramSystemMessage());
	}

}
