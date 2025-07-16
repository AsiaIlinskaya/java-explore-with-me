package ru.practicum.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.user.dto.NewUserRequestDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

import static ru.practicum.user.mapper.UserMapper.toUser;
import static ru.practicum.user.mapper.UserMapper.toUserDto;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        log.info("Получение списка пользователей по IDs: ids={}, from={}, size={}", ids, from, size);
        return toUserDto(userRepository.findByIdIn(ids, PageRequest.of(from / size, size)));
    }

    @Override
    public List<UserDto> getUsers(int from, int size) {
        log.info("Получение списка всех пользователей: from={}, size={}", from, size);
        return toUserDto(userRepository.findAll(PageRequest.of(from / size, size)));
    }

    @Override
    public UserDto createUser(NewUserRequestDto newUserRequestDto) {
        log.info("Создание нового пользователя: {}", newUserRequestDto);
        return toUserDto(userRepository.save(toUser(newUserRequestDto)));
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Удаление пользователя с ID={}", id);
        userRepository.deleteById(id);
    }
}