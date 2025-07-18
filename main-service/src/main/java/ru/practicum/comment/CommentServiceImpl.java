package ru.practicum.comment;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentMapper;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.model.EventState;
import ru.practicum.exceptions.CommentNotFoundException;
import ru.practicum.exceptions.EventNotFoundException;
import ru.practicum.exceptions.ForbiddenException;
import ru.practicum.exceptions.UserNotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.comment.dto.CommentMapper.toComment;
import static ru.practicum.comment.dto.CommentMapper.toCommentResponseDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CommentResponseDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        log.info("Создан комментарий пользователем с ID = {}, для события с ID = {}", userId, eventId);
        User user = getUserById(userId);
        Event event = getPublishedEventById(eventId);

        Comment comment = toComment(newCommentDto, user, event);
        return toCommentResponseDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentResponseDto> getEventComments(Long eventId, int from, int size) {
        log.info("Получить комментарии события с ID = {}", eventId);
        Event event = getPublishedEventById(eventId);
        List<Comment> comments = commentRepository.findByEvent(event, PageRequest.of(from / size, size));
        return comments.stream()
                .map(CommentMapper::toCommentResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto getCommentById(Long commentId) {
        log.info("Получить комментарий с ID = {}", commentId);
        return toCommentResponseDto(getCommentOrThrow(commentId));
    }

    @Override
    @Transactional
    public CommentResponseDto updateComment(Long userId, Long commentId, NewCommentDto newCommentDto) {
        log.info("Обновить комментарий с ID = {} от пользователя с ID = {}", commentId, userId);
        getUserById(userId);
        Comment comment = getCommentOrThrow(commentId);

        validateCommentOwnership(userId, comment);
        validateCommentNotConfirmed(comment);

        comment.setText(newCommentDto.getText());
        comment.setUpdatedOn(LocalDateTime.now());
        comment.setState(CommentState.PENDING);

        return toCommentResponseDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        log.info("Удалить комментарий с ID = {} от пользователя с ID = {}", commentId, userId);
        getUserById(userId);
        Comment comment = getCommentOrThrow(commentId);

        validateCommentOwnership(userId, comment);
        validateCommentNotConfirmed(comment);

        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public CommentResponseDto updateCommentStatusByAdmin(Long commentId, boolean isConfirm) {
        log.info("Confirm/reject комментарий с ID = {}. New state: {}", commentId, isConfirm);
        Comment comment = getCommentOrThrow(commentId);

        if (isConfirm) {
            comment.setState(CommentState.CONFIRMED);
        } else {
            comment.setState(CommentState.REJECTED);
        }
        comment.setPublishedOn(LocalDateTime.now());

        return toCommentResponseDto(commentRepository.save(comment));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private Event getPublishedEventById(Long eventId) {
        return eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new EventNotFoundException(eventId));
    }

    private Comment getCommentOrThrow(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }

    private void validateCommentOwnership(Long userId, Comment comment) {
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("Нельзя изменить комментарий другого пользователя.");
        }
    }

    private void validateCommentNotConfirmed(Comment comment) {
        if (comment.getState() == CommentState.CONFIRMED) {
            throw new ForbiddenException("Нельзя изменить готовый комментарий.");
        }
    }
}