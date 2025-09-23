package com.example.SmartFinanceManager.Service;

import com.example.SmartFinanceManager.Dto.IncomeDto;
import com.example.SmartFinanceManager.Model.Category;
import com.example.SmartFinanceManager.Model.Income;
import com.example.SmartFinanceManager.Model.Profile;
import com.example.SmartFinanceManager.Repository.CategoryRepository;
import com.example.SmartFinanceManager.Repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;


    public IncomeDto addIncome(IncomeDto incomeDto) {
        Profile profile = profileService.getCurrentProfile();
        Category category = categoryRepository.findById(incomeDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Income newIncome = toEntity(incomeDto, profile, category);
        newIncome = incomeRepository.save(newIncome);

        return toDto(newIncome);
    }

    public List<IncomeDto> getCurrentMonthIncomeForCurrentUser() {
        Profile profile = profileService.getCurrentProfile();

        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = currentDate.withDayOfMonth(1);
        LocalDate endDate = currentDate.withDayOfMonth(currentDate.lengthOfMonth());

        List<Income> list = incomeRepository.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);

        return list.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Helper Methods
    private Income toEntity(IncomeDto incomeDto, Profile profile, Category category) {
        return Income.builder()
                .name(incomeDto.getName())
                .icon(incomeDto.getIcon())
                .amount(incomeDto.getAmount())
                .date(incomeDto.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    private IncomeDto toDto(Income income) {
        return IncomeDto.builder()
                .id(income.getIncomeId())
                .name(income.getName())
                .icon(income.getIcon())
                .amount(income.getAmount())
                .date(income.getDate())
                .categoryId(income.getCategory() != null ? income.getCategory().getId() : null)
                .categoryName(income.getCategory() != null ? income.getCategory().getName() : null)
                .createdAt(income.getCreatedAt())
                .updatedAt(income.getUpdatedAt())
                .build();
    }

}
