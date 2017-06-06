package com.social.bot.vk.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Sex {
    FEMALE(1), MALE(2);

    private Integer code;

    Sex(Integer code) {
        this.code = code;
    }

    @JsonValue
    public Integer getCode() {
        return code;
    }
}
