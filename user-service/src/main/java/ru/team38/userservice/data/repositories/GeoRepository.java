package ru.team38.userservice.data.repositories;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import ru.team38.common.dto.CityDto;
import ru.team38.common.dto.CountryDto;
import ru.team38.common.jooq.tables.Cities;
import ru.team38.common.jooq.tables.Countries;
import ru.team38.common.jooq.tables.records.CitiesRecord;
import ru.team38.common.jooq.tables.records.CountriesRecord;
import ru.team38.common.mappers.CitiesMapper;
import ru.team38.common.mappers.CountryMapper;
import org.mapstruct.factory.Mappers;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class GeoRepository {
    private final DSLContext dslContext;
    private final Cities cities = Cities.CITIES;
    private final Countries countries = Countries.COUNTRIES;
    private final CitiesMapper citiesMapper = Mappers.getMapper(CitiesMapper.class);
    private final CountryMapper countryMapper = Mappers.getMapper(CountryMapper.class);

    public GeoRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public void saveCity(List<CityDto> cities) {
        List<CitiesRecord> records = convertToCityRecords(cities);
        dslContext.batchInsert(records).execute();
    }

    private List<CitiesRecord> convertToCityRecords(List<CityDto> cities) {
        List<CitiesRecord> records = new ArrayList<>();
        for (CityDto cityDto : cities) {
            CitiesRecord cityRecord = citiesMapper.cityDtoToCitiesRecord(cityDto);
            cityRecord.setIsDeleted(cityDto.isDeleted());
            records.add(cityRecord);
        }
        return records;
    }

    public void saveCountry(List<CountryDto> countries) {
        List<CountriesRecord> records = convertToCountryRecords(countries);
        dslContext.batchInsert(records).execute();
    }

    private List<CountriesRecord> convertToCountryRecords(List<CountryDto> countries) {
        List<CountriesRecord> records = new ArrayList<>();
        long countryId = 0;
        for (CountryDto countryDto : countries) {
            CountriesRecord countryRecord = countryMapper.countryDtoToCountriesRecord(countryDto);
            countryRecord.setCountryId(countryId);
            countryRecord.setIsDeleted(countryDto.isDeleted());
            records.add(countryRecord);
            countryId++;
        }
        return records;
    }

    public CountryDto getCountryById(Long id) {
        CountriesRecord countriesRecord = dslContext.selectFrom(countries)
                .where(countries.ID.eq(id).and(countries.IS_DELETED.eq(false)))
                .fetchOne();
        if (countriesRecord == null) {
            return new CountryDto();
        }
        List<String> cities = dslContext.select(Cities.CITIES.CITY_NAME)
                .from(Cities.CITIES)
                .where(Cities.CITIES.COUNTRY_ID.eq(id))
                .fetchInto(String.class);
        CountryDto countryDto = countryMapper.mapToCountryDto(countriesRecord);
        countryDto.setCities(cities);
        return countryDto;
    }

    public List<CountryDto> getAllCountries() {
        Result<CountriesRecord> result = dslContext.selectFrom(countries)
                .where(countries.IS_DELETED.eq(false))
                .fetch();
        List<CountryDto> countryDtos = result.stream()
                .map(countryMapper::mapToCountryDto)
                .collect(Collectors.toList());
        return countryDtos;
    }
    public void clearCountriesTable() {
        dslContext.deleteFrom(countries).where(DSL.trueCondition()).execute();
    }

    public void clearCitiesTable() {
        dslContext.deleteFrom(cities).where(DSL.trueCondition()).execute();
    }
}
