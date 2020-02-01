package com.tennisapp.service;


import com.tennisapp.model.User;
import com.tennisapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import telegram.Message;

import javax.transaction.Transactional;

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
            user.setName(message.getFrom().getFirstName() + " " + message.getFrom().getLastName());
            return userRepository.saveAndFlush(user);
        });
    }

    public String getUserAdminCookie() {
        return getUser(ADMIN_ID).getLoginCookie();
    }

    public User getUser(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
    }

    public User getUserByTennisId(String tennisId) {
        return userRepository.findByTennisId(tennisId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tennis ID"));
    }
}
