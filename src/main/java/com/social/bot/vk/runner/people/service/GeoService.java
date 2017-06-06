package com.social.bot.vk.runner.people.service;

import com.social.bot.vk.common.VkSearchHttpClient;
import com.social.bot.vk.model.City;
import com.social.bot.vk.model.Country;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GeoService {
    private final VkSearchHttpClient vkHttpClient;

    public GeoService(VkSearchHttpClient vkHttpClient) {
        this.vkHttpClient = vkHttpClient;
    }

    public Optional<City> findCity(Country country, String cityName) {
        return vkHttpClient.searchForCities(country).stream()
                .filter(city -> city.getName().equalsIgnoreCase(cityName))
                .findFirst();
    }
}
