package com.tennisapp.service;

import com.tennisapp.client.Platform;
import com.tennisapp.client.TelegramClient;
import com.tennisapp.client.TennisClient;
import com.tennisapp.dto.TableModelDto;
import com.tennisapp.exception.BotException;
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
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.tennisapp.config.DictionaryKeysConfig.*;
import static com.tennisapp.service.CommandService.CHECK_ORDER;

@Service
public class TennisService {

	private static final int FIRST_PLAYER = 0;
	private static final String END_OF_COOKIE = ";";
	private static final String ID_PROFILE_PROP = "Id";
	private static final String FIRST_NAME_PROFILE_PROP = "FName";
	private static final String LAST_NAME_PROFILE_PROP = "LName";
	private static final String ORDER_PROP = "Order";
	private static final String SPLITTER = " - ";
	private static final String NEXT_ROW = "\n";

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
		Map<String, Object> profileMap = tennisClient.getProfile(loginCookie);

		user.setLoginCookie(loginCookie.substring(0, loginCookie.indexOf(END_OF_COOKIE)));
		user.setTennisId(profileMap.get(ID_PROFILE_PROP).toString());

		String name = new StringBuilder()
				.append(profileMap.get(FIRST_NAME_PROFILE_PROP).toString())
				.append(" ")
				.append(profileMap.get(LAST_NAME_PROFILE_PROP)).toString();

		user.setName(name);
	}

	public void bookTable(Message message, User user) {
		tennisClient.bookTable(message, user.getLoginCookie());
		telegramClient.simpleMessage(DictionaryUtil.getDictionaryValue(TABLE_BOOKED), message);

		sleep(2000);

		checkQueueAndSendQueueNumberForOneUser(message, user);
	}

	public void cancelGame(Message message, User user) {
		if (!tennisClient.cancelGame(user.getLoginCookie())) {
			throw new BotException(DictionaryUtil.getDictionaryValue(CANT_CANCEL), message);
		}

		sleep(2000);
		sendQueueToAllUsers();
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

			sleep(2000);
			sendQueueToAllUsers();
		}
	}

	public void checkQueueAndSendQueueNumberForOneUser(Message message, User user) {
		TableModelDto tableModel = tennisClient.getTableModel(userService.getUserAdminCookie());

		if (tableModel.getQueue().isEmpty()) {
			telegramClient.simpleMessage(DictionaryUtil.getDictionaryValue(TIME_TO_GO), message);
		} else {
			sendUserCurrentQueueNumber(message, tableModel, user, null);
		}
	}

	public void sendQueueList(Message message, User user) {
		TableModelDto tableModel = tennisClient.getTableModel(userService.getUserAdminCookie());
		TableModelDto.NowPlaying nowPlaying = tableModel.getNowPlaying();

			StringBuilder stringBuilder = new StringBuilder();
			AtomicInteger counter = new AtomicInteger(0);

		Optional<User> nowPlayingUser = userService
				.getUserByTennisIdOptional(nowPlaying.getPlayers().get(FIRST_PLAYER).getId());

		boolean requestByNowPlaying = nowPlayingUser.isPresent() &&
				message.getChat().getId().equals(nowPlayingUser.get().getChatId());

		long nowPlayingTime = TimeUnit.MILLISECONDS.toMinutes(nowPlaying.getTimePlayingMs());
			stringBuilder.append("Now playing: ").append(nowPlaying.getPlayers().get(0).getName());

			if (requestByNowPlaying)
				stringBuilder.append("(You)");

			stringBuilder.append(SPLITTER)
					.append(nowPlayingTime)
					.append(" Minutes")
					.append(NEXT_ROW);

			tableModel.getQueue().forEach(q -> {
				stringBuilder.append(ORDER_PROP).append(counter.incrementAndGet()).append(SPLITTER);
				stringBuilder.append(q.getPlayers().get(FIRST_PLAYER).getName()).append(NEXT_ROW);
			});
			telegramClient.simpleMessage(stringBuilder.toString(), creteTelegramMessage(user.getChatId()));

	}

	public void sendQueueToAllUsers() {
		TableModelDto tableModel = tennisClient.getTableModel(userService.getUserAdminCookie());
		List<TableModelDto.QueueDto> queue = tableModel.getQueue();

		if (!queue.isEmpty()) {
			Map<String, Integer> queueMap = createQueueMap(queue);

			userService.findUsersByTennisId(queueMap.keySet()).forEach(u -> {
				sendUserCurrentQueueNumber(creteTelegramMessage(u.getChatId()), tableModel, u, queueMap);
			});

		}
	}

	private void sendUserCurrentQueueNumber(Message message, TableModelDto tableModel, User user, Map<String, Integer> queueMap) {
		TableModelDto.NowPlaying nowPlaying = tableModel.getNowPlaying();

		long nowPlayingTime = TimeUnit.MILLISECONDS.toMinutes(nowPlaying.getTimePlayingMs());
		String name = nowPlaying.getPlayers().get(0).getName();

		if (queueMap == null) {
			queueMap = createQueueMap(tableModel.getQueue());
		}

		telegramClient.simpleMessage(String.format(DictionaryUtil.getDictionaryValue(ORDER_MESSAGE),
				queueMap.get(user.getTennisId()), name, nowPlayingTime), message);
	}

	private void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			throw new RuntimeException();
		}
	}

	private Message creteTelegramMessage(Integer chatId) {
		Message message = new Message(new Chat(chatId));
		message.setPlatform(Platform.COMMON);
		return message;
	}

	private Map<String, Integer> createQueueMap(List<TableModelDto.QueueDto> queue) {
		Map<String, Integer> map = new HashMap<>();
		for (int i = 0; i < queue.size(); i++) {
			List<TableModelDto.Player> players = queue.get(i).getPlayers();

			for (int order = 0; order < players.size(); order++) {
				map.put(players.get(order).getId(), i + 1);
			}
		}
		return map;
	}
}
