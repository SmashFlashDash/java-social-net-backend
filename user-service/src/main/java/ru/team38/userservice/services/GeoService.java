package ru.team38.userservice.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.team38.common.dto.CityDto;
import ru.team38.common.dto.CountryDto;


import java.util.*;
import java.util.stream.Collectors;

@Service
public class GeoService {
    public String loadGeoData() {
        // Загрузка списка стран и городов с внешнего интернет-ресурса
        return "Geo data loaded successfully";
    }

    List<CountryDto> countryDtos = new ArrayList<>();

    public ResponseEntity<List<CountryDto>> getCountries() {
        CountryDto country = new CountryDto();
        country.setId("1");
        country.setTitle("Россия");
        country.setCities(Arrays.asList("Москва", "Владивосток", "Красноярск"));
        List<CountryDto> countries = Arrays.asList(country);
        countryDtos.addAll(countries);
        return ResponseEntity.ok(countries);
    }

    public List<CityDto> getCitiesByCountryId(String countryId) {
        Optional<CountryDto> countryOptional = countryDtos.stream()
                .filter(country -> country.getId().equals(countryId))
                .findFirst();
        if (countryOptional.isPresent()) {
            CountryDto country = countryOptional.get();
            String countryIdRussia = country.getId();
            List<String> cityListRussia = country.getCities();
            List<CityDto> cityDtos = cityListRussia.stream()
                    .map(city -> {
                        CityDto cityDto = CityDto.builder()
                                .id(UUID.randomUUID().toString())
                                .isDeleted(false)
                                .title(city)
                                .countryId(countryIdRussia)
                                .build();
                        return cityDto;
                    })
                    .collect(Collectors.toList());
            return cityDtos;
        } else {
            return new ArrayList<>();
        }
    }
}