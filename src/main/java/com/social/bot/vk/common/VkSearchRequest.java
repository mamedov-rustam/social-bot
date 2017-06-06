package com.social.bot.vk.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.social.bot.vk.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @JsonProperty("status")
    private Status status;
    @JsonProperty("birth_month")
    private Integer birthdayMonth;
    @JsonProperty("group_id")
    private String groupId;

    //ToDo: investigate better solution
    public VkSearchRequest clone() {
        VkSearchRequest requestClone = new VkSearchRequest();
        BeanUtils.copyProperties(this, requestClone);

        return requestClone;
    }
}
