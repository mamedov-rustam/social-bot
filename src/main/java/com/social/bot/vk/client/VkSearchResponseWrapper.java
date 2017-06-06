package com.social.bot.vk.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VkSearchResponseWrapper {
    @JsonProperty("response")
    private VkSearchResponse response;
    @JsonProperty("error")
    private VkResponseError error;

    public boolean hasError() {
        return error != null;
    }
}
