package com.tennisapp.service;

import com.tennisapp.client.TennisClient;
import com.tennisapp.model.User;
import org.springframework.stereotype.Service;
import telegram.Message;

import javax.transaction.Transactional;

@Service
public class TennisService {

    private final TennisClient tennisClient;
    private final UserService userService;

    public TennisService(TennisClient tennisClient, UserService userService) {
        this.tennisClient = tennisClient;
        this.userService = userService;
    }

    @Transactional
    public void login(User user) {
        String loginCookie = tennisClient.login(user.getLogin(), user.getPassword());
        String tennisUserId = tennisClient.getProfileId(loginCookie).get("Id").toString();

        user.setLoginCookie(loginCookie.substring(0, loginCookie.indexOf(";")));
        user.setTennisId(tennisUserId);
    }

    @Transactional
    public void bookTable(Message message, User user) {
        tennisClient.bookTable(message, user.getLoginCookie());
    }

    public void cancelGame(User user) {
        tennisClient.cancelGame(user.getLoginCookie());
    }
}
