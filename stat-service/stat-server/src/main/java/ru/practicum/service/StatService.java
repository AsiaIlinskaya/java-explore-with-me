package ru.practicum.service;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    void addHit(HitDto hitDto);

    List<StatDto> getStats(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique);
}