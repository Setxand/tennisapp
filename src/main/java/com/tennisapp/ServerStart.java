package com.tennisapp;

import com.tennisapp.client.Platform;
import com.tennisapp.client.TelegramClient;
import com.tennisapp.client.TennisClient;
import com.tennisapp.dto.TableModelDto;
import com.tennisapp.model.User;
import com.tennisapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import telegram.Chat;
import telegram.Message;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class ServerStart {

    private static final int FIRST_IN_QUEUE = 0;
    private static final int FIRST_PLAYER = 0;

    @Autowired private TelegramClient telegramClient;
    @Autowired UserService userService;
    @Autowired TennisClient tennisClient;

    @PostConstruct
    public void setUp() {
        telegramClient.setWebHooks();
    }

    @Scheduled(fixedRate = 10000)
    public void checkAvailability() {
        TableModelDto tableModel = tennisClient.getTableModel(userService.getUserAdminCookie());
        TableModelDto.NowPlaying nowPlaying = tableModel.nowPlaying;

        if (nowPlaying != null && !nowPlaying.isAccepted) {
            User user = userService.getUserByTennisId(nowPlaying.players.get(0).id);

            tennisClient.acceptInvite(user.getLoginCookie());

            Message message = new Message(new Chat(user.getChatId()));
            message.setPlatform(Platform.COMMON);
            telegramClient.simpleMessage("Your game is started!", message);
        }

    }

}
