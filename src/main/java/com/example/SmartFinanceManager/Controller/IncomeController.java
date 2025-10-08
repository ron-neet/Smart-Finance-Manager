package com.example.SmartFinanceManager.Controller;

import com.example.SmartFinanceManager.Dto.IncomeDto;
import com.example.SmartFinanceManager.Service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/income")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeDto> addIncome(@RequestBody IncomeDto incomeDto) {
        IncomeDto addIncome = incomeService.addIncome(incomeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(addIncome);
    }

    @GetMapping
    public ResponseEntity<List<IncomeDto>> getIncomeForMonth() {
        List<IncomeDto> getIncomeForAMonth = incomeService.getCurrentMonthIncomeForCurrentUser();
        return ResponseEntity.status(HttpStatus.OK).body(getIncomeForAMonth);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id) {
        incomeService.deleteIncomeById(id);
        return ResponseEntity.noContent().build();
    }

}
