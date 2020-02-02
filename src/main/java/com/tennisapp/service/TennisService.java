package com.tennisapp.service;

import com.tennisapp.client.Platform;
import com.tennisapp.client.TelegramClient;
import com.tennisapp.client.TennisClient;
import com.tennisapp.dto.TableModelDto;
import com.tennisapp.model.User;
import com.tennisapp.util.DictionaryUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import telegram.Chat;
import telegram.Message;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.tennisapp.config.DictionaryKeysConfig.*;

@Service
public class TennisService {

	private static final int FIRST_PLAYER = 0;
	private static final String ID_PROP = "Id";
	private static final String END_OF_COOKIE = ";";

	private final TennisClient tennisClient;
	private final UserService userService;
	private final TelegramClient telegramClient;

	public TennisService(TennisClient tennisClient, UserService userService, TelegramClient telegramClient) {
		this.tennisClient = tennisClient;
		this.userService = userService;
		this.telegramClient = telegramClient;
	}

	@Transactional
	public void login(User user) {
		String loginCookie = tennisClient.login(user.getLogin(), user.getPassword());
		String tennisUserId = tennisClient.getProfileId(loginCookie).get(ID_PROP).toString();

		user.setLoginCookie(loginCookie.substring(0, loginCookie.indexOf(END_OF_COOKIE)));
		user.setTennisId(tennisUserId);
	}

	public void bookTable(Message message, User user) {
		tennisClient.bookTable(message, user.getLoginCookie());
		telegramClient.simpleMessage(DictionaryUtil.getDictionaryValue(TABLE_BOOKED), message);
		sendQueueToCurrentUser(message, user);
	}

	public void cancelGame(User user) {
		tennisClient.cancelGame(user.getLoginCookie());
	}

	@Scheduled(fixedRate = 600000)
	public void sendQueueToUsers() {
		TableModelDto tableModel = tennisClient.getTableModel(userService.getUserAdminCookie());
		List<TableModelDto.QueueDto> queue = tableModel.getQueue();

		Map<String, Integer> queueMap = new HashMap<>();

		if (!queue.isEmpty()) {
			setUpQueueMap(queue, queueMap);

			long nowPlayingTime = TimeUnit.MILLISECONDS.toMinutes(tableModel.getNowPlaying().getTimePlayingMs());
			userService.findUsersByTennisId(queueMap.keySet()).forEach(u -> {

				telegramClient.simpleMessage(String.format(DictionaryUtil.getDictionaryValue(ORDER_MESSAGE),
						queueMap.get(u.getTennisId()), nowPlayingTime), creteTelegramMessage(u.getChatId()));
			});
		}
	}

	@Scheduled(fixedRate = 120000)
	public void checkAndStartGameIfTableIsFree() {
		TableModelDto tableModel = tennisClient.getTableModel(userService.getUserAdminCookie());
		TableModelDto.NowPlaying nowPlaying = tableModel.getNowPlaying();

		if (nowPlaying != null && !nowPlaying.isAccepted()) {
			User user = userService.getUserByTennisId(nowPlaying.getPlayers().get(FIRST_PLAYER).getId());

			tennisClient.acceptInvite(user.getLoginCookie());
			telegramClient.simpleMessage(DictionaryUtil
					.getDictionaryValue(GAME_STARTED), creteTelegramMessage(user.getChatId()));

			sendQueueToUsers();
		}
	}

	public void sendQueueToCurrentUser(Message message, User user) {
		TableModelDto tableModel = tennisClient.getTableModel(userService.getUserAdminCookie());
		long nowPlayingTime = TimeUnit.MILLISECONDS.toMinutes(tableModel.getNowPlaying().getTimePlayingMs());

		if (tableModel.getQueue().isEmpty()) {
			telegramClient.simpleMessage(DictionaryUtil.getDictionaryValue(TIME_TO_GO), message);
		} else {
			telegramClient.simpleMessage(String.format(DictionaryUtil.getDictionaryValue(ORDER_MESSAGE),
					user.getTennisId(), nowPlayingTime), creteTelegramMessage(user.getChatId()));
		}
	}

	private Message creteTelegramMessage(Integer chatId) {
		Message message = new Message(new Chat(chatId));
		message.setPlatform(Platform.COMMON);
		return message;
	}

	private void setUpQueueMap(List<TableModelDto.QueueDto> queue, Map<String, Integer> map) {
		for (int i = 0; i < queue.size(); i++) {
			List<TableModelDto.Player> players = queue.get(i).getPlayers();

			for (int order = 0; order < players.size(); order++) {
				map.put(players.get(order).getId(), i + 1);
			}
		}
	}
}
