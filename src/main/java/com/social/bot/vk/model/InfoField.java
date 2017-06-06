package com.social.bot.vk.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum InfoField {
    CONNECTIONS("connections"), CONTACTS("contacts"),
    COUNTERS("counters"), PHOTO("photo");

    private String value;

    InfoField(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
