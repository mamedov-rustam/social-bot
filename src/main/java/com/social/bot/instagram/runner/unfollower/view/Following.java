package com.social.bot.instagram.runner.unfollower.view;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Following {
    @JsonProperty("followed_by_viewer")
    private boolean myFollower;
    @JsonProperty("username")
    private String username;
}
