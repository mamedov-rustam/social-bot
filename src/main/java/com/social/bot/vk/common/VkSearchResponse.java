package com.social.bot.vk.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.social.bot.vk.model.User;
import lombok.Data;

import java.util.List;

@Data
public class VkSearchResponse {
    @JsonProperty("count")
    private Long count;
    @JsonProperty("items")
    private List<User> users;

    // Fucking shit like 'alternate deserialization name'
    @JsonProperty("users")
    public void setUsersAsUsers(List<User> users) {
        this.users = users;
    }
}
