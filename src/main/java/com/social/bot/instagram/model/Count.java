package com.social.bot.instagram.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Count {
    @JsonProperty("count")
    private Long value;
}
