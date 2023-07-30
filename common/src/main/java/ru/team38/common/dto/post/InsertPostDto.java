package ru.team38.common.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InsertPostDto {
    private String imagePath;
    private String postText;
    private String title;
    private LocalDateTime time;
    private String type;
    private LocalDateTime publishDate;
    private String[] tags;
    private LocalDateTime timeChanged;
}
