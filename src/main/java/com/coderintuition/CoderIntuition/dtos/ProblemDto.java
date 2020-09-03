package com.coderintuition.CoderIntuition.dtos;

import com.coderintuition.CoderIntuition.models.Problem;
import com.coderintuition.CoderIntuition.models.ProblemStep;
import com.coderintuition.CoderIntuition.models.Testcase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ProblemDto {
    private Problem problem;
    private List<ProblemStep> steps;
    private List<Testcase> testcases;
}
