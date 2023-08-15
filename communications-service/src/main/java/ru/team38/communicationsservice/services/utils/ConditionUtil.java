package ru.team38.communicationsservice.services.utils;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;
import ru.team38.common.dto.post.*;
import ru.team38.common.jooq.Tables;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.jooq.impl.DSL.field;
@Component
public class ConditionUtil {
    public Condition searchCondition(PostSearchDto postSearchDto){
        Condition condition = Tables.POST.IS_DELETED.eq(false)
                .and(Tables.POST.IS_BLOCKED.eq(false));
        if(postSearchDto.getAuthor() != null){
            condition = condition.and(authorIdCondition(postSearchDto.getAuthor()));
        }
        if(postSearchDto.getTags() != null){
            condition = condition.and(tagsCondition(postSearchDto.getTags()));
        }
        if(postSearchDto.getDateTo() != null){
            condition = condition.and(timeCondition(postSearchDto.getDateTo(), postSearchDto.getDateFrom()));
        }
        if (postSearchDto.getText() != null){
            condition = condition.and(textCondition(postSearchDto.getText()));
        }
        return condition;
    }
    private Condition textCondition(String text){
        String lowerText = text.toLowerCase();
        return DSL.lower(Tables.POST.TITLE).contains(lowerText)
                .or(DSL.lower(Tables.POST.POST_TEXT).contains(lowerText));
    }
    private Condition tagsCondition(List<String> tags) {
        Condition tagsCondition = DSL.trueCondition();
        for (String tag : tags) {
            tagsCondition = tagsCondition.and(DSL.coalesce(Tables.POST.TAGS.cast(String[].class), field("ARRAY['']")).like("%" + tag + "%"));
        }
        return tagsCondition;
    }

    private Condition authorIdCondition(String author) {
        Condition authorIdCondition = DSL.trueCondition();
        Field<UUID> authorIdField = null;
        String[] name = author.split(" ");
        if (name.length == 1) {
            authorIdField = field(DSL.select(Tables.ACCOUNT.ID)
                    .from(Tables.ACCOUNT)
                    .where(DSL.lower(Tables.ACCOUNT.FIRST_NAME).eq(name[0].toLowerCase()))
                    .or(DSL.lower(Tables.ACCOUNT.LAST_NAME).eq(name[0].toLowerCase()))
                    .limit(1));
        } else if (name.length >= 2) {
            authorIdField = field(DSL.select(Tables.ACCOUNT.ID)
                    .from(Tables.ACCOUNT)
                    .where(DSL.lower(Tables.ACCOUNT.FIRST_NAME).eq(name[0].toLowerCase()).and(DSL.lower(Tables.ACCOUNT.LAST_NAME).eq(name[1].toLowerCase())))
                    .or(DSL.lower(Tables.ACCOUNT.FIRST_NAME).eq(name[1].toLowerCase()).and(DSL.lower(Tables.ACCOUNT.LAST_NAME).eq(name[0].toLowerCase())))
                    .limit(1));
        }

        if (authorIdField != null) {
            authorIdCondition = Tables.POST.AUTHOR_ID.in(authorIdField);
        }
        return authorIdCondition;
    }

    private Condition timeCondition(String to, String from) {
        Condition timeCondition = DSL.trueCondition();
        if (from != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            LocalDateTime fromDateTime = LocalDateTime.parse(from, formatter);
            LocalDateTime toDateTime = LocalDateTime.parse(to, formatter);

            timeCondition = Tables.POST.PUBLISH_DATE.between(fromDateTime, toDateTime);
        }
        return timeCondition;
    }
}
