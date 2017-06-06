package com.social.bot.vk.model;


import com.fasterxml.jackson.annotation.JsonValue;

/*
*   1 — не женат (не замужем);
    2 — встречается;
    3 — помолвлен(-а);
    4 — женат (замужем);
    5 — всё сложно;
    6 — в активном поиске;
    7 — влюблен(-а);
    8 — в гражданском браке.
* */
public enum Status {
    UNMARRIED(1),
    MARRIED(4),
    BETROTHED(3),
    IN_RELATIONSHIP(2),
    COMPLICATED(5),
    IN_SEARCH(6),
    IN_LOVE(7),
    CIVIL_MARRIAGE(8);

    private Integer value;

    Status(Integer value) {
        this.value = value;
    }

    @JsonValue
    public Integer getValue() {
        return value;
    }
}
