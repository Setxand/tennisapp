package com.tennisapp.service;

import com.tennisapp.client.Platform;
import com.tennisapp.model.User;
import org.springframework.stereotype.Service;
import telegram.Message;
import telegram.Update;

import javax.transaction.Transactional;

@Service
public class DirectionService {

    private final UserService userService;
    private MessageService messageService;

    public DirectionService(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    public void directUpdate(Update update) {
        try {

            if (update.getMessage() != null) {
                update.getMessage().setPlatform(Platform.COMMON);
                User user = userService.createUser(update.getMessage());
                messageService.messageFromBot(update.getMessage(), user);
            }
        } catch (Exception e) {

        }
//        } else if (update.getCallBackQuery() != null) {
//            update.getCallBackQuery().getMessage().setPlatform(Platform.COMMON);
//            User user = createUser(update.getCallBackQuery().getMessage());
//            callBackQUeryService.callBackQueryToBot(update.getCallBackQuery(), user);
//        }


    }
}
