package com.social.bot.vk.client.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class City {
    private String name;
    private Long code;

    @JsonValue
    public Long getCode() {
        return code;
    }
}
