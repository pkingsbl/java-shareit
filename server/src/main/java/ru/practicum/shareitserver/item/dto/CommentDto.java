package ru.practicum.shareitserver.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class CommentDto {

    private Long id;
    @NotBlank
    private String text;
    private String authorName;
    private LocalDateTime created;

}
