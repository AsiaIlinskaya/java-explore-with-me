package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatDto;
import ru.practicum.exceptions.ValidationRequestException;
import ru.practicum.mapper.StatMapper;
import ru.practicum.model.Stat;
import ru.practicum.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.mapper.HitMapper.toHit;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;

    @Override
    public void addHit(HitDto hitDto) {
        log.debug("Сохраняем hit: {}", hitDto);
        statRepository.save(toHit(hitDto));
    }

    @Override
    public List<StatDto> getStats(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique) {
        log.info("Получение статистики с параметрами: start={}, end={}, uris={}, unique={}",
                start, end, uris != null ? Arrays.toString(uris) : "null", unique);

        List<Stat> stats;

        if (start.isAfter(end)) {
            log.error("Ошибка валидации: start ({}) после end ({})", start, end);
            throw new ValidationRequestException("Параметр 'start' не может быть позже параметра 'end'.");
        }

        try {
            if (uris == null || uris.length == 0) {
                log.debug("Запрос статистики без указания URIs, unique={}", unique);
                stats = unique
                        ? statRepository.findAllStatsUnique(start, end)
                        : statRepository.findAllStats(start, end);
            } else {
                List<String> uriList = List.of(uris);
                log.debug("Запрос статистики для URIs: {}, unique={}", uriList, unique);
                stats = unique
                        ? statRepository.findStatsByUrisUnique(uriList, start, end)
                        : statRepository.findStatsByUris(uriList, start, end);
            }

            log.debug("Найдено {} записей статистики", stats.size());

            List<StatDto> result = stats.isEmpty()
                    ? Collections.emptyList()
                    : stats.stream().map(StatMapper::toStatDto).collect(Collectors.toList());

            log.info("Успешно возвращено {} элементов статистики", result.size());
            return result;
        } catch (Exception e) {
            log.error("Ошибка при получении статистики: {}", e.getMessage(), e);
            throw e;
        }
    }
}