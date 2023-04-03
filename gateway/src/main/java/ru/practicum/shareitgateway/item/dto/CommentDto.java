package ru.practicum.shareitgateway.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class CommentDto {

    @NotBlank
    private String text;
    private LocalDateTime created;

}
