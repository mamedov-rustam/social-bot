package com.social.bot.vk.client.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Sex {
    FEMALE(1), MALE(2), ALL(3);

    private Integer code;

    Sex(Integer code) {
        this.code = code;
    }

    @JsonValue
    public Integer getCode() {
        return code;
    }
}
