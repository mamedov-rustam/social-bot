package com.social.bot.vk.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.social.bot.vk.client.model.User;
import lombok.Data;

import java.util.List;

@Data
public class VkSearchResponse {
    @JsonProperty("count")
    private Long count;
    @JsonProperty("items")
    private List<User> users;
}
