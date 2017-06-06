package com.social.bot.vk.service;

import com.social.bot.vk.client.VkHttpClient;
import com.social.bot.vk.client.model.City;
import com.social.bot.vk.client.model.Country;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GeoService {
    private final VkHttpClient vkHttpClient;

    public GeoService(VkHttpClient vkHttpClient) {
        this.vkHttpClient = vkHttpClient;
    }

    public Optional<City> findCity(Country country, String cityName) {
        return vkHttpClient.searchForCities(country).stream()
                .filter(city -> city.getName().equalsIgnoreCase(cityName))
                .findFirst();
    }
}
