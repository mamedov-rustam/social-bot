package com.social.bot.vk.runner.people.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.social.bot.vk.common.VkResponseError;
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
