package com.tennisapp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {

    public enum UserStatus {
        ROOM_CONNECTION_STATUS,
        LOGIN
    }

    @Id
    private Integer chatId;
    private String tennisId;
    private String name;

    private String login;
    private String password;

    @Length(max = 600)
    private String loginCookie;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

}
