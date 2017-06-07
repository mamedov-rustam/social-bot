package com.social.bot.vk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VkUser {
    @JsonProperty("id")
    private String id;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("photo")
    private String photoHref;
    @JsonProperty("instagram")
    private String instagram;
    @JsonProperty("twitter")
    private String twitter;
    @JsonProperty("facebook")
    private String facebook;
    @JsonProperty("skype")
    private String skype;
    @JsonProperty("bdate")
    private String birthdayDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        VkUser user = (VkUser) o;

        return id != null ? id.equals(user.id) : user.id == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
