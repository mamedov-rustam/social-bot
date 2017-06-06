package com.social.bot.vk.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Country {
    RU("Russia", 1L), UA("Ukraine", 2L);

    private String name;
    private Long code;

    Country(String name, Long code) {
        this.name = name;
        this.code = code;
    }

    @JsonValue
    public Long getCode() {
        return code;
    }
}
