package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;

@Data
@Builder(toBuilder = true)
public class CommentDto {

    private Long id;
    @NotBlank
    private String text;
    private String authorName;
    private LocalDateTime created;

}
