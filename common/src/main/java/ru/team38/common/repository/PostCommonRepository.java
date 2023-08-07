package ru.team38.common.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;
import ru.team38.common.dto.post.PostDto;
import ru.team38.common.jooq.tables.Post;
import ru.team38.common.jooq.tables.records.PostRecord;
import ru.team38.common.mappers.PostMapper;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PostCommonRepository {
    private final DSLContext DSL;
    private final Post POST = Post.POST;
    private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    public PostDto getPostDtoById(UUID itemId) {
        PostRecord post = DSL.selectFrom(POST).where(POST.ID.eq(itemId)).fetchOne();
        return postMapper.postRecord2PostDto(post);
    }
}
