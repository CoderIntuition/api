package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.dtos.request.cms.ProblemDto;
import com.coderintuition.CoderIntuition.models.Problem;
import com.coderintuition.CoderIntuition.models.ProblemStep;
import com.coderintuition.CoderIntuition.models.Solution;
import com.coderintuition.CoderIntuition.models.TestCase;
import com.coderintuition.CoderIntuition.repositories.ProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/cms")
public class CmsController {
    @Autowired
    ProblemRepository problemRepository;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ResponseEntity addProblem(ProblemDto problemDto) {
        Problem problem = new Problem();
        problem.setName(problemDto.getName());
        problem.setUrlName(problemDto.getUrlName());
        problem.setCategory(problemDto.getCategory());
        problem.setDifficulty(problemDto.getDifficulty());
        problem.setPythonCode(problemDto.getPythonCode());
        problem.setJavaCode(problemDto.getJavaCode());
        problem.setJavascriptCode(problemDto.getJavascriptCode());

        // process problem steps
        List<ProblemStep> problemSteps = new ArrayList<>();
        for (int i = 0; i < problemDto.getProblemSteps().size(); i++) {
            var problemStepDto = problemDto.getProblemSteps().get(i);
            ProblemStep problemStep = new ProblemStep();
            problemStep.setStepNum(i + 1);
            problemStep.setName(problemStepDto.getName());
            problemStep.setType(problemStepDto.getType());
            problemStep.setContent(problemStepDto.getContent());
            problemStep.setTime(problemStepDto.getTime());
            problemSteps.add(problemStep);
        }
        problem.setProblemSteps(problemSteps);

        // process test cases
        List<TestCase> testCases = new ArrayList<>();
        for (int i = 0; i < problemDto.getTestCases().size(); i++) {
            var testCaseDto = problemDto.getTestCases().get(i);
            TestCase testCase = new TestCase();
            testCase.setTestCaseNum(i + 1);
            testCase.setName(testCaseDto.getName());
            testCase.setIsDefault(testCaseDto.getIsDefault());
            testCase.setInput(testCaseDto.getInput());
            testCase.setOutput(testCaseDto.getOutput());
            testCases.add(testCase);
        }
        problem.setTestCases(testCases);

        // process solutions
        List<Solution> solutions = new ArrayList<>();
        for (int i = 0; i < problemDto.getSolutions().size(); i++) {
            var solutionDto = problemDto.getSolutions().get(i);
            Solution solution = new Solution();
            solution.setSolutionNum(i + 1);
            solution.setName(solutionDto.getName());
            solution.setIsPrimary(solutionDto.getIsPrimary());
            solution.setDescription(solutionDto.getDescription());
            solution.setPythonCode(solutionDto.getPythonCode());
            solution.setJavaCode(solutionDto.getJavaCode());
            solution.setJavascriptCode(solutionDto.getJavascriptCode());
            solutions.add(solution);
        }
        problem.setSolutions(solutions);

        problemRepository.save(problem);
        return ResponseEntity.ok().build();
    }
}
