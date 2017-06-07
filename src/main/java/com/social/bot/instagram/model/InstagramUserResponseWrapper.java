package com.social.bot.instagram.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InstagramUserResponseWrapper {
    @JsonProperty("user")
    private InstagramUser user;
    private boolean hasError;
    private String errorCause;

    public static InstagramUserResponseWrapper error(String errorCause) {
        InstagramUserResponseWrapper responseWrapper = new InstagramUserResponseWrapper();
        responseWrapper.setHasError(true);
        responseWrapper.setErrorCause(errorCause);

        return  responseWrapper;
    }
}
