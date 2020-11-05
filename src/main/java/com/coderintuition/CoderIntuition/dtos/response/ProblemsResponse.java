package com.coderintuition.CoderIntuition.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProblemsResponse {
    private int totalPages;
    private int totalElements;
    private List<SimpleProblemDto> problems;
}
