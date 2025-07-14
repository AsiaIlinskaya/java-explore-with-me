package ru.practicum.event.model;

public enum StateUserAction {
    SEND_TO_REVIEW, // отправить событие на модерацию
    CANCEL_REVIEW   // отменить публикацию события
}