package ru.team38.common.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.team38.common.dto.CityDto;
import ru.team38.common.jooq.tables.records.CitiesRecord;

@Mapper
public interface CitiesMapper {

    @Mapping(source = "citiesRecord.cityName", target = "title")
    CityDto citiesRecordToCityDto(CitiesRecord citiesRecord);
    @Mapping(source = "title", target = "cityName")
    CitiesRecord cityDtoToCitiesRecord(CityDto cityDto);
}