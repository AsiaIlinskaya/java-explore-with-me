package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.CategoryNotFoundException;
import ru.practicum.exceptions.ForbiddenException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.category.mapper.CategoryMapper.toCategory;
import static ru.practicum.category.mapper.CategoryMapper.toCategoryDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        log.info("Получение списка категорий: from = {}, size = {}", from, size);
        return categoryRepository.findAll(PageRequest.of(from / size, size))
                                 .stream()
                                 .map(CategoryMapper::toCategoryDto)
                                 .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(long catId) {
        log.info("Получение информации о категории с ID: cat_id = {}", catId);
        return toCategoryDto(categoryRepository.findById(catId)
                                               .orElseThrow(() -> new CategoryNotFoundException(catId)));
    }

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        log.info("Добавление новой категории: category = {}", newCategoryDto);
        return toCategoryDto(categoryRepository.save(toCategory(newCategoryDto)));
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(long catId, NewCategoryDto newCategoryDto) {
        log.info("Обновление категории: cat_id = {}, category = {}", catId, newCategoryDto);
        Category existCategory = categoryRepository.findById(catId)
                                                   .orElseThrow(() -> new CategoryNotFoundException(catId));
        Category updatedCategory = toCategory(newCategoryDto);
        updatedCategory.setId(existCategory.getId());
        return toCategoryDto(categoryRepository.save(updatedCategory));
    }

    @Override
    @Transactional
    public void deleteCategory(long catId) {
        log.info("Удаление категории: cat_id = {}", catId);
        categoryRepository.findById(catId)
                          .orElseThrow(() -> new CategoryNotFoundException(catId));
        Event event = eventRepository.findFirstByCategoryId(catId);
        if (event != null) {
            throw new ForbiddenException(String.format("Категория с id = %d не пустая", catId));
        }
        categoryRepository.deleteById(catId);
    }
}
