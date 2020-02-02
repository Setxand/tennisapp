package com.tennisapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginForm {

	@JsonProperty("Email")
	public String login;
	@JsonProperty("Password")
	public String password;

	public LoginForm(String login, String password) {
		this.login = login;
		this.password = password;
	}
}
