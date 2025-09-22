package com.example.SmartFinanceManager.Controller;

import com.example.SmartFinanceManager.Dto.CategoryDto;
import com.example.SmartFinanceManager.Service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/register")
    public ResponseEntity<CategoryDto> saveCategory(@RequestBody CategoryDto categoryDto) {
        CategoryDto newCategory = categoryService.saveCategory(categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCategory);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategoryList(){
        List<CategoryDto> categoryList = categoryService.getCategoriesForCurrentUser();
        return ResponseEntity.status(HttpStatus.OK).body(categoryList);
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<CategoryDto>> getCategoryListByType(@PathVariable String type){
        List<CategoryDto> categoryList = categoryService.getCategoriesByTypeForCurrentUser(type);
        return ResponseEntity.status(HttpStatus.OK).body(categoryList);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long categoryId,@RequestBody CategoryDto categoryDto) {
        CategoryDto updateCategory = categoryService.updateCategory(categoryId, categoryDto);
        return ResponseEntity.status(HttpStatus.OK).body(updateCategory);
    }
}
