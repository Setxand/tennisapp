package com.tennisapp.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "url")
@Getter
@Setter
public class UrlConfig {

	private String server;
	private String webhook;
	private String telegramUrls;

}
