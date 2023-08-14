package ru.team38.common.dto.notification;


import lombok.Getter;

@Getter
public enum BirthdayMessage {
    RU("Не забудьте поздравить и пожелать счастья!"),
    EN("Don't forget to congratulate and wish happiness!");

    private final String message;

    BirthdayMessage(String message) {
        this.message = message;
    }

}
