package com.oddle.app.weather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oddle.app.weather.common.Constant;
import com.oddle.app.weather.dto.HistoricalWeatherResponse;
import com.oddle.app.weather.dto.OpenWeatherResponse;
import com.oddle.app.weather.dto.WeatherResponse;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WeatherServiceImpl implements WeatherService {

    @Autowired
    GeoCodeService geoCodeService;

    WebClient webClient = WebClient.create("https://api.openweathermap.org/data/2.5");


    @Override
    public WeatherResponse getCurrent(String city) {

        OpenWeatherResponse res = webClient.get()
                .uri("/weather?q="+ city +"&appid=ac0f3d4cc8ab37ae0e3a6c0eb9d8afc8")
                .retrieve()
                .bodyToMono(OpenWeatherResponse.class)
                .block();

        assert res != null;
        return WeatherResponse.builder()
                .city(res.getName())
                .temperature(res.getMain().temp)
                .country(res.sys.country)
                .timeZone(res.timezone)
                .weather(res.weather)
                .build();
    }

    @Override
    public HistoricalWeatherResponse getHistorical(String city, String date) throws JsonProcessingException {

        val geocode = geoCodeService.getLatLonByCity(city);

        HistoricalWeatherResponse res = webClient.get()
                .uri("/onecall/timemachine?lat="+geocode.getLat()+"&lon="+geocode.getLon()+"&dt="+date+"&appid="+ Constant.API_KEY)
                .retrieve()
                .bodyToMono(HistoricalWeatherResponse.class)
                .block();

        assert res != null;
        return res;
    }
}