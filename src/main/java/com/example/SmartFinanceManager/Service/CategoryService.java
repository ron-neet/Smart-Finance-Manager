package com.example.SmartFinanceManager.Service;

import com.example.SmartFinanceManager.Dto.CategoryDto;
import com.example.SmartFinanceManager.Model.Category;
import com.example.SmartFinanceManager.Model.Profile;
import com.example.SmartFinanceManager.Repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;


    public CategoryDto saveCategory(CategoryDto categoryDto) {
        Profile profile = profileService.getCurrentProfile();
        if(categoryRepository.existsByNameAndProfileId(categoryDto.getName(), profile.getId())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category already exists");
        }
        Category newCategory = toEntity(categoryDto, profile);
        newCategory =categoryRepository.save(newCategory);
        return toDto(newCategory);
    }

    public List<CategoryDto> getCategoriesForCurrentUser(){
        Profile profile = profileService.getCurrentProfile();
        List<Category> categories = categoryRepository.findByProfileId(profile.getId());
        return categories
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<CategoryDto> getCategoriesByTypeForCurrentUser(String type){
        Profile profile = profileService.getCurrentProfile();
        List<Category> categories = categoryRepository.findByTypeAndProfileId(type, profile.getId());
        return categories.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {
        Profile profile = profileService.getCurrentProfile();
        Category category = categoryRepository.findByIdAndProfileId(categoryId, profile.getId())
                .orElseThrow(()-> new RuntimeException("Category not found"));

        category.setName(categoryDto.getName());
        category.setIcon(categoryDto.getIcon());

        category = categoryRepository.save(category);

        return toDto(category);

    }

    // Helper Method
    private Category toEntity(CategoryDto categoryDto, Profile profile) {
            return Category.builder()
                    .name(categoryDto.getName())
                    .type(categoryDto.getType())
                    .icon(categoryDto.getIcon())
                    .profile(profile)
                    .build();
    }

    private CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .icon(category.getIcon())
                .profileId(category.getProfile()!=null?category.getProfile().getId():null)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();

    }

}
