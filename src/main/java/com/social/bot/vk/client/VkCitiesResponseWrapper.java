package com.social.bot.vk.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class VkCitiesResponseWrapper {
    @JsonProperty("cities")
    private List<List<String>> countryList;
    @JsonProperty("error")
    private VkResponseError error;

    public boolean hasError() {
        return error != null;
    }
}
