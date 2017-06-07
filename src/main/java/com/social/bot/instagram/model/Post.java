package com.social.bot.instagram.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.commons.lang.BooleanUtils;

import java.util.Date;

@Data
public class Post {
    @JsonProperty("id")
    private String id;
    @JsonProperty("date")
    private Date date;
    @JsonProperty("comments")
    private Count comments;
    @JsonProperty("likes")
    private Count likes;
    @JsonProperty("is_video")
    private Boolean isVideo;

    public boolean isPhoto() {
        return BooleanUtils.isNotTrue(isVideo);
    }

    @JsonProperty("date")
    public void setDateInSeconds(Long dateInSeconds) {
        this.date = new Date(dateInSeconds * 1000);
    }
}
