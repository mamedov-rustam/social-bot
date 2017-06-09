package com.social.bot.instagram.runner.unfollower.view;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Following {
    @JsonProperty("username")
    private String username;
}
