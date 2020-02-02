package com.tennisapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class TableModelDto {

	@JsonProperty("Queue")
	private List<QueueDto> queue;

	@JsonProperty("NowPlaying")
	private NowPlaying nowPlaying;

	@Getter
	public static class QueueDto {

		@JsonProperty("Players")
		private List<Player> Players;

	}

	@Getter
	public static class NowPlaying {

		@JsonProperty("IsAccepted")
		private boolean isAccepted;

		@JsonProperty("Players")
		private List<Player> players;

		@JsonProperty("Id")
		private String id;

		@JsonProperty("TimePlayingMs")
		private Integer timePlayingMs;
	}

	@Getter
	public static class Player {

		@JsonProperty("Id")
		private String id;

		@JsonProperty("Name")
		private String name;
	}
}
