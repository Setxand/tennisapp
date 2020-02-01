package com.tennisapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

public class TableModelDto {

    @JsonProperty("Queue")
    public List<QueueDto> queue;

    @JsonProperty("NowPlaying")
    public NowPlaying nowPlaying;

    public static class QueueDto  {

        @JsonProperty("Players")
        public List<Player> Players;

    }

    public static class NowPlaying {

        @JsonProperty("IsAccepted")
        public boolean isAccepted;

        @JsonProperty("Players")
        public List<Player> players;

        @JsonProperty("Id")
        public String id;
    }

    public static class Player {

        @JsonProperty("Id")
        public String id;

        @JsonProperty("Name")
        public String name;
    }
}
