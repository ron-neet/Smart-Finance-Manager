package com.example.SmartFinanceManager.Controller;

import com.example.SmartFinanceManager.Dto.ExpenseDto;
import com.example.SmartFinanceManager.Service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expense")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseDto> addExpense(@RequestBody ExpenseDto expenseDto) {
        ExpenseDto addExpense =  expenseService.addExpense(expenseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(addExpense);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDto>> getAllExpensesForAMonth() {
        List<ExpenseDto> getExpense = expenseService.getCurrentMonthExpensesForCurrentUser();
        return  ResponseEntity.status(HttpStatus.OK).body(getExpense);
    }

}
