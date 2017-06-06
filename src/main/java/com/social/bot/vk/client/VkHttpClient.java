package com.social.bot.vk.client;

import feign.Headers;
import feign.RequestLine;

public interface VkHttpClient {
    @RequestLine("POST /users.search")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    VkSearchResponseWrapper searchForUsers(VkSearchRequest searchRequest);
}
