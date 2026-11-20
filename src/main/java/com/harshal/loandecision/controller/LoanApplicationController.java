package com.harshal.loandecision.controller;

import com.harshal.loandecision.dto.LoanApplicationRequest;
import com.harshal.loandecision.dto.LoanApplicationResponse;
import com.harshal.loandecision.service.LoanDecisionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/applications")
public class LoanApplicationController {

    private final LoanDecisionService loanDecisionService;

    public LoanApplicationController(LoanDecisionService loanDecisionService) {
        this.loanDecisionService = loanDecisionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanApplicationResponse createApplication(@Valid @RequestBody LoanApplicationRequest request) {
        return LoanApplicationResponse.from(loanDecisionService.evaluate(request));
    }
}