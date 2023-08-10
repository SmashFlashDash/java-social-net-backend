package ru.team38.common.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.team38.common.dto.geography.CityDto;
import ru.team38.common.jooq.tables.records.CitiesRecord;

@Mapper
public interface CitiesMapper {

    @Mapping(source = "citiesRecord.cityName", target = "title")
    CityDto citiesRecordToCityDto(CitiesRecord citiesRecord);
    @Mapping(source = "title", target = "cityName")
    CitiesRecord cityDtoToCitiesRecord(CityDto cityDto);
}