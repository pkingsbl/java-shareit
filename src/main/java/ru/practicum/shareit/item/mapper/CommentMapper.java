package ru.practicum.shareit.item.mapper;

import java.util.Collection;
import java.util.stream.Collectors;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dto.CommentDto;

public class CommentMapper {
    public static CommentDto mapToCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static Collection<CommentDto> mapToCommentDto(Collection<Comment> comments) {
        return comments.stream().map(CommentMapper::mapToCommentDto).collect(Collectors.toList());
    }

    public static Comment mapToComment(CommentDto comment, User user, Item item) {
        return Comment.builder()
                .id(comment.getId())
                .text(comment.getText())
                .item(item)
                .author(user)
                .created(comment.getCreated())
                .build();
    }
}
