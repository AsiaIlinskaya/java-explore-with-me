package ru.practicum.mapper;

import ru.practicum.dto.HitDto;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HitMapper {

    private HitMapper() {
    }

    public static Hit toHit(HitDto hitDto) {
        return Hit.builder()
                  .app(hitDto.getApp())
                  .uri(hitDto.getUri())
                  .ip(hitDto.getIp())
                  .timestamp(LocalDateTime.parse(hitDto.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                  .build();
    }
}