package com.social.bot.vk.client.model;


import com.fasterxml.jackson.annotation.JsonValue;

public enum PhotoState {
    PRESENTED(true), ANY(false);

    private Boolean value;

    PhotoState(Boolean value) {
        this.value = value;
    }

    @JsonValue
    public Boolean getValue() {
        return value;
    }
}
