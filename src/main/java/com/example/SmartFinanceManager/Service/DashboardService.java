package com.example.SmartFinanceManager.Service;

import com.example.SmartFinanceManager.Dto.ExpenseDto;
import com.example.SmartFinanceManager.Dto.IncomeDto;
import com.example.SmartFinanceManager.Dto.RecentTransactionDto;
import com.example.SmartFinanceManager.Model.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final ProfileService profileService;

    public Map<String, Object> getDashboardData() {
        Profile profile = profileService.getCurrentProfile();
        Map<String, Object> returnValue = new LinkedHashMap<>();

        List<IncomeDto> latestIncome = incomeService.getLatest5IncomeForCurrentUser();
        List<ExpenseDto> latestExpense = expenseService.getLatest5ExpenseForCurrentUser();

        List<RecentTransactionDto> recentTransactionList = Stream.concat(latestIncome.stream().map(income ->
                        RecentTransactionDto.builder()
                                .id(income.getId())
                                .profileId(profile.getId())
                                .icon(income.getIcon())
                                .name(income.getName())
                                .amount(income.getAmount())
                                .date(income.getDate())
                                .createdAt(income.getCreatedAt())
                                .updatedAt(income.getUpdatedAt())
                                .type("Income")
                                .build()),

                latestExpense.stream().map(expense ->
                        RecentTransactionDto.builder()
                                .id(expense.getId())
                                .profileId(profile.getId())
                                .icon(expense.getIcon())
                                .name(expense.getName())
                                .amount(expense.getAmount())
                                .date(expense.getDate())
                                .createdAt(expense.getCreatedAt())
                                .updatedAt(expense.getUpdatedAt())
                                .type("Expense")
                                .build())
        ).sorted((a, b) -> {
            int cmp = b.getDate().compareTo(a.getDate());
            if (cmp == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null) {
                return b.getCreatedAt().compareTo(a.getCreatedAt());
            }
            return cmp;
        }).collect(Collectors.toList());
        returnValue.put("TotalBalance", incomeService.getTotalIncomeForCurrentUser()
                .subtract(expenseService.getTotalExpenseForCurrentUser()));
        returnValue.put("LatestIncome", incomeService.getTotalIncomeForCurrentUser());
        returnValue.put("latestExpense", expenseService.getTotalExpenseForCurrentUser());
        returnValue.put("recent5Expense", latestExpense);
        returnValue.put("recent5Income", latestIncome);
        returnValue.put("recent5TransactionList", recentTransactionList);
        return returnValue;
    }

}
