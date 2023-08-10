package ru.team38.userservice.data.repositories;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;
import ru.team38.common.dto.geography.CityDto;
import ru.team38.common.dto.geography.CountryDto;
import ru.team38.common.jooq.tables.Cities;
import ru.team38.common.jooq.tables.Countries;
import ru.team38.common.jooq.tables.records.CitiesRecord;
import ru.team38.common.jooq.tables.records.CountriesRecord;
import ru.team38.common.mappers.CitiesMapper;
import ru.team38.common.mappers.CountryMapper;

import ru.team38.userservice.services.task.LocationBasedUtilities;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GeoRepository {
    private final DSLContext dslContext;
    private final LocationBasedUtilities locationBasedUtilities;
    private static final Cities cities = Cities.CITIES;
    private static final Countries countries = Countries.COUNTRIES;
    private final CitiesMapper citiesMapper = Mappers.getMapper(CitiesMapper.class);
    private final CountryMapper countryMapper = Mappers.getMapper(CountryMapper.class);

    public boolean areTablesEmpty() {
        int countCountries = dslContext.fetchCount(countries);
        int countCities = dslContext.fetchCount(cities);
        return countCountries == 0 || countCities == 0;
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
        List<String> citiesList = dslContext.select(Cities.CITIES.CITY_NAME)
                .from(Cities.CITIES)
                .where(Cities.CITIES.COUNTRY_ID.eq(id))
                .fetchInto(String.class);
        CountryDto countryDto = countryMapper.mapToCountryDto(countriesRecord);
        countryDto.setCities(citiesList);
        return countryDto;
    }

    public List<CountryDto> getAllCountries() {
        Result<CountriesRecord> result = dslContext.selectFrom(countries)
                .where(countries.IS_DELETED.eq(false))
                .fetch();
        return result.stream()
                .map(countryMapper::mapToCountryDto)
                .toList();
    }
    public void clearCountriesTable() {
        dslContext.deleteFrom(countries).where(DSL.trueCondition()).execute();
    }

    public void clearCitiesTable() {
        dslContext.deleteFrom(cities).where(DSL.trueCondition()).execute();
    }
    public void addZoneIdInCitiesTable(String city, String country){
        CitiesRecord citiesRecord = citiesRecordByCityAndCountry(city, country);
        if(citiesRecord != null) {
            double latitude = citiesRecord.getLatitude();
            double longitude = citiesRecord.getLongitude();
            Long citiesId = citiesRecord.getId();
            String zoneId = locationBasedUtilities.getTimeZoneByCoordinates(latitude, longitude);
            dslContext.update(cities)
                    .set(cities.ZONE_ID, zoneId)
                    .where(cities.ID.eq(citiesId))
                    .returning()
                    .fetchOne();
        }
    }
    private CitiesRecord citiesRecordByCityAndCountry(String city, String country){
        return dslContext.select()
                .from(cities)
                .join(countries)
                .on(cities.COUNTRY_ID.eq(countries.COUNTRY_ID))
                .where(countries.COUNTRY_NAME.eq(country))
                .and(cities.CITY_NAME.eq(city))
                .and(cities.ZONE_ID.isNull())
                .fetchOneInto(CitiesRecord.class);
    }
}
