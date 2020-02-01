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
                telegramClient.helloMessage(message);
                break;

            case "/login":
                login(message, user);
                break;

            case "/booktable":
                bookTable(message);
                break;

            case "/reentercredentials":
                reenterCredentials(message, user);
                break;

            default:
                throw new RuntimeException();
        }
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

    private void bookTable(Message message) {
        tennisService.bookTable(message);
        telegramClient.simpleMessage(DictionaryUtil.getDictionaryValue(TABLE_BOOKED), message);
    }
}
