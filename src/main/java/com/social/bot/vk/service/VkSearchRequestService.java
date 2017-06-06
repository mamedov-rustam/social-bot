package com.social.bot.vk.service;


import com.social.bot.vk.common.VkSearchRequest;
import com.social.bot.vk.model.City;
import com.social.bot.vk.model.Country;
import com.social.bot.vk.runner.people.service.GeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static com.social.bot.vk.model.InfoField.CONNECTIONS;

@Service
public class VkSearchRequestService {
    @Value("${vk.api.access.token}")
    private String accessToken;
    @Value("${vk.api.version}")
    private String apiVersion;
    @Value("${vk.page.size}")
    private Long pageSize;

    private final GeoService geoService;

    @Autowired
    public VkSearchRequestService(GeoService geoService) {
        this.geoService = geoService;
    }

    public VkSearchRequest createRequest(Long pageNumber) {
        VkSearchRequest request = createRequest();
        request.setOffset(pageSize * pageNumber);

        return request;
    }

    public VkSearchRequest createGeoRequest(Country country, String cityName) {
        VkSearchRequest request = createRequest();
        // ToDo: create cache for cities
        City city = geoService.findCity(country, cityName).get();
        request.setCity(city);

        return request;
    }

    public VkSearchRequest createRequest() {
        return VkSearchRequest.builder()
                .version(apiVersion)
                .accessToken(accessToken)
                .count(pageSize)
                .fields(Arrays.asList(CONNECTIONS))
                .build();
    }

}
