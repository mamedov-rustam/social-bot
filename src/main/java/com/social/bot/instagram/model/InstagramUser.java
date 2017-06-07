package com.social.bot.instagram.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstagramUser {
    @JsonProperty("id")
    private String id;
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("username")
    private String username;
    @JsonProperty("biography")
    private String description;
    @JsonProperty("follows")
    private Count following;
    @JsonProperty("followed_by")
    private Count followers;
    @JsonProperty("media")
    private Media media;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        InstagramUser that = (InstagramUser) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
