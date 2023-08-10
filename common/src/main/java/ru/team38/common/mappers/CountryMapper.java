package ru.team38.common.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.team38.common.dto.geography.CountryDto;
import ru.team38.common.jooq.tables.records.CountriesRecord;

@Mapper
public interface CountryMapper {

    @Mapping(source = "countriesRecord.countryName", target = "title")
    CountryDto mapToCountryDto(CountriesRecord countriesRecord);
    @Mapping(source = "title", target = "countryName")
    CountriesRecord countryDtoToCountriesRecord(CountryDto countryDto);
}