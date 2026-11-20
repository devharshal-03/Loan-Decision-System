package com.harshal.loandecision.dto;

import java.util.List;

public record ErrorResponse(List<String> errors) {
}