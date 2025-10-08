package com.example.SmartFinanceManager.Service;

import com.example.SmartFinanceManager.Dto.ExpenseDto;
import com.example.SmartFinanceManager.Model.Category;
import com.example.SmartFinanceManager.Model.Expense;
import com.example.SmartFinanceManager.Model.Profile;
import com.example.SmartFinanceManager.Repository.CategoryRepository;
import com.example.SmartFinanceManager.Repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;


    public ExpenseDto addExpense(ExpenseDto expenseDto) {
        Profile profile = profileService.getCurrentProfile();
        Category category = categoryRepository.findById(expenseDto.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found"));

        Expense newExpense = toEntity(expenseDto, profile, category);
        newExpense = expenseRepository.save(newExpense);

        return toDto(newExpense);
    }


    public List<ExpenseDto> getCurrentMonthExpensesForCurrentUser() {
        Profile profile = profileService.getCurrentProfile();

        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = currentDate.withDayOfMonth(1);
        LocalDate endDate = currentDate.withDayOfMonth(currentDate.lengthOfMonth());

        List<Expense> allExpense = expenseRepository.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);

        return allExpense.stream().map(this::toDto).collect(Collectors.toList());
    }

    public void deleteExpenseById(Long expenseId) {
        Profile profile = profileService.getCurrentProfile();
        Expense findExpense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!findExpense.getProfile().getId().equals(profile.getId())) {
            throw new RuntimeException("Unauthorized to delete Expense ");
        }
        expenseRepository.delete(findExpense);
    }

    //filter expenses
    public List<ExpenseDto> filterExpense(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){
        Profile profile = profileService.getCurrentProfile();
        List<Expense>  list = expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate, endDate, keyword, sort);
        return list.stream()
                .map(this::toDto)
                .toList();
    }

    // Notification
    public List<ExpenseDto> getExpensesForUserOnDate(Long profileId,LocalDate date){
            List<Expense> expenses = expenseRepository.findByProfileIdAndDate(profileId, date);

            return expenses.stream()
                    .map(this::toDto)
                    .toList();
    }

    public List<ExpenseDto> getLatest5ExpenseForCurrentUser() {
        Profile profile = profileService.getCurrentProfile();
        List<Expense> allExpense = expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return allExpense.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public BigDecimal getTotalExpenseForCurrentUser() {
        Profile profile = profileService.getCurrentProfile();
        BigDecimal total = expenseRepository.findTotalExpenseByProfileId(profile.getId());
        return total != null ? total : BigDecimal.ZERO;
    }


    // Helper Methods
    private Expense toEntity(ExpenseDto expenseDto, Profile profile, Category category) {
        return Expense.builder()
                .name(expenseDto.getName())
                .icon(expenseDto.getIcon())
                .amount(expenseDto.getAmount())
                .date(expenseDto.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    private ExpenseDto toDto(Expense expense) {
        return ExpenseDto.builder()
                .id(expense.getExpenseId())
                .name(expense.getName())
                .icon(expense.getIcon())
                .amount(expense.getAmount())
                .date(expense.getDate())
                .categoryId(expense.getCategory() != null ? expense.getCategory().getId() : null)
                .categoryName(expense.getCategory() != null ? expense.getCategory().getName() : null)
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }

}
