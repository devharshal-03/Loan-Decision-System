package com.harshal.loandecision.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoanApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void approvedApplicationReturnsSingleOffer() throws Exception {
        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson(720, 75000)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.applicationId").isNotEmpty())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.riskBand").value("MEDIUM"))
                .andExpect(jsonPath("$.offer.interestRate").value(13.5))
                .andExpect(jsonPath("$.offer.tenureMonths").value(36))
                .andExpect(jsonPath("$.offer.emi").value(16967.64))
                .andExpect(jsonPath("$.offer.totalPayable").value(610835.04))
                .andExpect(jsonPath("$.rejectionReasons").doesNotExist());
    }

    @Test
    void rejectedApplicationReturnsReasonsAndNullRiskBand() throws Exception {
        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson(550, 75000)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.applicationId").isNotEmpty())
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.riskBand").value(nullValue()))
                .andExpect(jsonPath("$.rejectionReasons[0]").value("CREDIT_SCORE_BELOW_MINIMUM"))
                .andExpect(jsonPath("$.offer").doesNotExist());
    }

    private String requestJson(int creditScore, int monthlyIncome) {
        return """
                {
                  "applicant": {
                    "name": "Test Applicant",
                    "age": 30,
                    "monthlyIncome": %d,
                    "employmentType": "SALARIED",
                    "creditScore": %d
                  },
                  "loan": {
                    "amount": 500000,
                    "tenureMonths": 36,
                    "purpose": "PERSONAL"
                  }
                }
                """.formatted(monthlyIncome, creditScore);
    }
}