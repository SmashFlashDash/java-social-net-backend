package ru.team38.common.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.team38.common.dto.friend.FriendDto;
import ru.team38.common.dto.friend.FriendShortDto;
import ru.team38.common.jooq.tables.records.AccountRecord;
import ru.team38.common.jooq.tables.records.FriendsRecord;

@Mapper
public interface FriendDtoMapper {

    FriendDtoMapper INSTANCE = Mappers.getMapper(FriendDtoMapper.class);

    @Mapping(source = "accountRecord.firstName", target = "firstName")
    @Mapping(source = "accountRecord.lastName", target = "lastName")
    @Mapping(source = "accountRecord.city", target = "city")
    @Mapping(source = "accountRecord.country", target = "country")
    @Mapping(source = "accountRecord.birthDate", target = "birthDate")
    @Mapping(source = "accountRecord.isOnline", target = "isOnline")
    @Mapping(source = "accountRecord.photo", target = "photo")
    @Mapping(source = "accountRecord.isDeleted", target = "isDeleted")
    @Mapping(source = "friendsRecord.id", target = "id")
    @Mapping(source = "friendsRecord.statusCode", target = "statusCode")
    @Mapping(source = "friendsRecord.accountFromId", target = "accountFrom")
    @Mapping(source = "friendsRecord.requestedAccountId", target = "accountTo")
    @Mapping(source = "friendsRecord.previousStatus", target = "previousStatus")
    @Mapping(source = "friendsRecord.rating", target = "rating")
    FriendDto mapToFriendDto(FriendsRecord friendsRecord, AccountRecord accountRecord);

    @Mapping(source = "statusCode", target = "statusCode")
    @Mapping(source = "accountFrom", target = "accountFromId")
    @Mapping(source = "accountTo", target = "requestedAccountId")
    @Mapping(source = "previousStatus", target = "previousStatus")
    @Mapping(source = "rating", target = "rating")
    FriendsRecord friendDtoToFriendsRecord(FriendDto friendDto);

    @Mapping(target = "id", constant = "00000000-0000-0000-0000-000000000000")
    @Mapping(source = "accountRecord.isDeleted", target = "isDeleted")
    @Mapping(source = "friendsRecord.statusCode", target = "statusCode")
    @Mapping(source = "accountRecord.id", target = "friendId")
    @Mapping(source = "accountRecord.id", target = "idFriend")
    @Mapping(source = "friendsRecord.previousStatus", target = "previousStatusCode")
    @Mapping(source = "friendsRecord.rating", target = "rating")
    FriendShortDto mapToFriendShortDto(FriendsRecord friendsRecord, AccountRecord accountRecord);
}