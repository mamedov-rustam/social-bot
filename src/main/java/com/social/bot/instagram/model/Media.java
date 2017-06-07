package com.social.bot.instagram.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Media {
    @JsonProperty("nodes")
    private List<Post> posts;
    @JsonProperty("count")
    private Long count;
}
