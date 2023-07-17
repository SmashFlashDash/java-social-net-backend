package ru.team38.userservice.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.team38.common.dto.CityDto;
import ru.team38.common.dto.CountryDto;
import ru.team38.userservice.services.GeoService;

import java.util.List;



@RestController
@RequestMapping("/api/v1/geo")
@AllArgsConstructor
public class GeoController {
    private final GeoService geoService;
    @GetMapping("/country")
    public ResponseEntity<List<CountryDto>> getCountries() {
        return geoService.getCountries();
    }


    @GetMapping("/country/{countryId}/city")
    public ResponseEntity<List<CityDto>> getCitiesByCountryId(@PathVariable String countryId) {
        return ResponseEntity.ok(geoService.getCitiesByCountryId(countryId));
    }
}