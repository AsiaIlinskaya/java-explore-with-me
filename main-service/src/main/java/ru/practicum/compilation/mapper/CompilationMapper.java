package ru.practicum.compilation.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.mapper.EventMapper;

import java.util.Collections;
import java.util.stream.Collectors;

@NoArgsConstructor
public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                             .id(compilation.getId())
                             .events(compilation.getEvents() != null ?
                                             compilation.getEvents().stream()
                                                        .map(EventMapper::toEventShortDto)
                                                        .collect(Collectors.toList()) : Collections.emptyList())
                             .pinned(compilation.isPinned())
                             .title(compilation.getTitle())
                             .build();
    }

    public static Compilation toCompilation(NewCompilationDto newCompilationDto) {
        return Compilation.builder()
                          .pinned(newCompilationDto.getPinned())
                          .title(newCompilationDto.getTitle())
                          .build();
    }
}