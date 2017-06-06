package com.social.bot.vk.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.social.bot.vk.model.City;
import com.social.bot.vk.model.Country;
import com.social.bot.vk.runner.filter.VkSearchBatchUsersResponse;
import com.social.bot.vk.runner.people.model.VkCitiesResponseWrapper;
import com.social.bot.vk.common.VkSearchResponseWrapper;
import com.social.bot.vk.common.VkSearchRequest;
import lombok.SneakyThrows;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class VkSearchHttpClient {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public VkSearchHttpClient(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    public VkSearchResponseWrapper searchForPeopleInGroup(VkSearchRequest searchRequest) {
        return search(searchRequest, "https://api.vk.com/method/groups.getMembers");
    }

    @SneakyThrows
    public VkSearchResponseWrapper searchForPeople(VkSearchRequest searchRequest) {
        return search(searchRequest, "https://api.vk.com/method/users.search");
    }

    @SneakyThrows
    public VkSearchBatchUsersResponse searchForPeopleBatch(VkSearchRequest searchRequest) {
        HttpGet request = new HttpGet("https://api.vk.com/method/users.get");
        List<NameValuePair> params = toNameValuePairs(searchRequest);
        URI uri = new URIBuilder(request.getURI()).addParameters(params).build();

        request.setURI(uri);
        InputStream input = httpClient.execute(request).getEntity().getContent();

        return objectMapper.readValue(input, VkSearchBatchUsersResponse.class);
    }

    @SneakyThrows
    private VkSearchResponseWrapper search(VkSearchRequest searchRequest, String url) {
        HttpGet request = new HttpGet(url);
        List<NameValuePair> params = toNameValuePairs(searchRequest);
        URI uri = new URIBuilder(request.getURI()).addParameters(params).build();

        request.setURI(uri);
        InputStream input = httpClient.execute(request).getEntity().getContent();

        return objectMapper.readValue(input, VkSearchResponseWrapper.class);
    }

    @SneakyThrows
    public List<City> searchForCities(Country country) {
        // ToDo: need to investigate how to set language
        String url = "https://vk.com/select_ajax.php?act=a_get_cities&country=" + country.getCode();
        HttpGet request = new HttpGet(url);

        InputStream input = httpClient.execute(request).getEntity().getContent();

        VkCitiesResponseWrapper response = objectMapper.readValue(input, VkCitiesResponseWrapper.class);
        return response.getCountryList().stream()
                .map(city -> {
                    String code = city.get(0);
                    String name = city.get(1)
                            .replaceAll("<b>", "")
                            .replaceAll("</b>", "");

                    return new City(code, name);
                })
                .collect(toList());
    }

    @SuppressWarnings("unchecked")
    private static List<NameValuePair> toNameValuePairs(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.convertValue(object, Map.class);

        return map.entrySet().stream()
                .map(entry -> {
                    Object value = entry.getValue();
                    if (List.class.isAssignableFrom(value.getClass())) {
                        List values = (List) value;
                        value = values.stream().map(Object::toString).collect(Collectors.joining(","));
                    }

                    return new BasicNameValuePair(entry.getKey(), value.toString());
                })
                .collect(toList());
    }

}
