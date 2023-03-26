package ru.practicum.shareitserver.item.mapper;

import ru.practicum.shareitserver.item.dto.CommentDto;
import ru.practicum.shareitserver.item.model.Comment;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

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
