package com.tennisapp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "url.tennis")
@Getter
@Setter
public class UrlTennis {

	private String loginUrl;
	private String bookingUrl;
	private String showTableUrl;
	private String getProfileUrl;
	private String acceptInvitationUrl;
	private String cancelGameUrl;

}
