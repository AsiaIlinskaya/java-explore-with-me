package ru.practicum.comment.dto;

import lombok.NoArgsConstructor;
import ru.practicum.comment.Comment;
import ru.practicum.comment.CommentState;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ru.practicum.event.mapper.EventMapper.toEventShortDto;
import static ru.practicum.user.mapper.UserMapper.toUserShortDto;

@NoArgsConstructor
public class CommentMapper {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Comment toComment(NewCommentDto newCommentDto, User author, Event event) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .event(event)
                .author(author)
                .state(CommentState.PENDING)
                .createdOn(LocalDateTime.now())
                .build();
    }

    public static CommentResponseDto toCommentResponseDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .event(toEventShortDto(comment.getEvent()))
                .author(toUserShortDto(comment.getAuthor()))
                .text(comment.getText())
                .state(comment.getState().toString())
                .createdOn(comment.getCreatedOn().format(formatter))
                .updatedOn(comment.getUpdatedOn() != null ? comment.getUpdatedOn().format(formatter) : null)
                .publishedOn(comment.getPublishedOn() != null ? comment.getPublishedOn().format(formatter) : null)
                .build();
    }
}