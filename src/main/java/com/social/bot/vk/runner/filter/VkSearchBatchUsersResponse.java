package com.social.bot.vk.runner.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.social.bot.vk.model.User;
import lombok.Data;

import java.util.List;

@Data
public class VkSearchBatchUsersResponse {
    @JsonProperty("response")
    private List<User> users;
}
