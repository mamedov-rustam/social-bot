package com.social.bot.vk.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.social.bot.vk.client.model.City;
import com.social.bot.vk.client.model.Country;
import com.social.bot.vk.client.model.InfoField;
import com.social.bot.vk.client.model.Sex;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VkSearchRequest {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("v")
    private String version;
    @JsonProperty("q")
    private String query;
    @JsonProperty("offset")
    private Long offset;
    @JsonProperty("count")
    private Long count;
    @JsonProperty("fields")
    private List<InfoField> fields;
    @JsonProperty("country")
    private Country country;
    @JsonProperty("city")
    private City city;
    @JsonProperty("sex")
    private Sex sex;
    @JsonProperty("age_from")
    private Integer minAge;
    @JsonProperty("age_to")
    private Integer maxAge;
    @JsonProperty("has_photo")
    private Boolean hasPhoto;
}
