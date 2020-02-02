package com.tennisapp.client;

import com.tennisapp.config.UrlConfig;
import org.springframework.stereotype.Component;

@Component
public class TelegramClient extends telegram.client.TelegramClient {

	public TelegramClient(UrlConfig config) {
		super(config.getServer(), config.getWebhook(), config.getTelegramUrls());
	}
}
