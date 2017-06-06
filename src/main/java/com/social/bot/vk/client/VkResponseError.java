package com.social.bot.vk.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VkResponseError {
    @JsonProperty("error_code")
    private Long code;
    @JsonProperty("error_msg")
    private String message;
}
