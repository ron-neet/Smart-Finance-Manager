package com.example.SmartFinanceManager.Controller;

import com.example.SmartFinanceManager.Dto.IncomeDto;
import com.example.SmartFinanceManager.Model.Income;
import com.example.SmartFinanceManager.Service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/income")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeDto> addIncome(@RequestBody IncomeDto incomeDto){
        IncomeDto addIncome = incomeService.addIncome(incomeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(addIncome);
    }

}
