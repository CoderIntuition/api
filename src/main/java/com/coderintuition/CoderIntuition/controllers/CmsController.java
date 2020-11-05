package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.dtos.request.cms.DeleteProblemRequest;
import com.coderintuition.CoderIntuition.dtos.request.cms.ProblemDto;
import com.coderintuition.CoderIntuition.dtos.request.cms.UpdateProblemRequest;
import com.coderintuition.CoderIntuition.dtos.response.MessageResponse;
import com.coderintuition.CoderIntuition.exceptions.RecordNotFoundException;
import com.coderintuition.CoderIntuition.models.Problem;
import com.coderintuition.CoderIntuition.models.ProblemStep;
import com.coderintuition.CoderIntuition.models.Solution;
import com.coderintuition.CoderIntuition.models.TestCase;
import com.coderintuition.CoderIntuition.repositories.ProblemRepository;
import com.coderintuition.CoderIntuition.repositories.ProblemStepRepository;
import com.coderintuition.CoderIntuition.repositories.SolutionRepository;
import com.coderintuition.CoderIntuition.repositories.TestCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/cms")
public class CmsController {
    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    ProblemStepRepository problemStepRepository;

    @Autowired
    TestCaseRepository testCaseRepository;

    @Autowired
    SolutionRepository solutionRepository;

    @PostMapping("/update")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ResponseEntity updateProblem(@Valid @RequestBody UpdateProblemRequest updateProblemRequest) {
        ProblemDto problemDto = updateProblemRequest.getProblem();

        // update general problem info
        Problem problem = problemRepository.findById(updateProblemRequest.getId()).orElseThrow();
        problem.setName(problemDto.getName());
        problem.setUrlName(problemDto.getUrlName());
        problem.setCategory(problemDto.getCategory());
        problem.setDifficulty(problemDto.getDifficulty());
        problem.setDescription(problemDto.getDescription());
        problem.setPythonCode(problemDto.getPythonCode());
        problem.setJavaCode(problemDto.getJavaCode());
        problem.setJavascriptCode(problemDto.getJavascriptCode());
        problemRepository.save(problem);

        // update problem steps
        for (int i = 0; i < problemDto.getProblemSteps().size(); i++) {
            var problemStepDto = problemDto.getProblemSteps().get(i);
            ProblemStep problemStep;
            if (i < problem.getProblemSteps().size()) {
                problemStep = problemStepRepository.findByProblemAndStepNum(problem, i + 1).orElseThrow();
            } else {
                problemStep = new ProblemStep();
                problemStep.setProblem(problem);
                problemStep.setStepNum(i + 1);
            }
            problemStep.setName(problemStepDto.getName());
            problemStep.setType(problemStepDto.getType());
            problemStep.setContent(problemStepDto.getContent());
            problemStep.setTime(problemStepDto.getTime());
            problemStepRepository.save(problemStep);
        }
        // delete remaining problem steps
        for (int i = problemDto.getProblemSteps().size(); i < problem.getProblemSteps().size(); i++) {
            ProblemStep problemStep = problemStepRepository.findByProblemAndStepNum(problem, i + 1).orElseThrow();
            problemStepRepository.delete(problemStep);
        }

        // update test cases
        for (int i = 0; i < problemDto.getTestCases().size(); i++) {
            var testCaseDto = problemDto.getTestCases().get(i);
            TestCase testCase;
            if (i < problem.getTestCases().size()) {
                testCase = testCaseRepository.findByProblemAndTestCaseNum(problem, i + 1).orElseThrow();
            } else {
                testCase = new TestCase();
                testCase.setProblem(problem);
                testCase.setTestCaseNum(i + 1);
            }
            testCase.setName(testCaseDto.getName());
            testCase.setIsDefault(testCaseDto.getIsDefault());
            testCase.setInput(testCaseDto.getInput());
            testCase.setOutput(testCaseDto.getOutput());
            testCaseRepository.save(testCase);
        }
        // delete remaining test cases
        for (int i = problemDto.getTestCases().size(); i < problem.getTestCases().size(); i++) {
            TestCase testCase = testCaseRepository.findByProblemAndTestCaseNum(problem, i + 1).orElseThrow();
            testCaseRepository.delete(testCase);
        }

        // update solutions
        for (int i = 0; i < problemDto.getSolutions().size(); i++) {
            var solutionDto = problemDto.getSolutions().get(i);
            Solution solution;
            if (i < problem.getSolutions().size()) {
                solution = solutionRepository.findByProblemAndSolutionNum(problem, i + 1).orElseThrow();
            } else {
                solution = new Solution();
                solution.setProblem(problem);
                solution.setSolutionNum(i + 1);
            }
            solution.setName(solutionDto.getName());
            solution.setIsPrimary(solutionDto.getIsPrimary());
            solution.setDescription(solutionDto.getDescription());
            solution.setPythonCode(solutionDto.getPythonCode());
            solution.setJavaCode(solutionDto.getJavaCode());
            solution.setJavascriptCode(solutionDto.getJavascriptCode());
            solutionRepository.save(solution);
        }
        // delete remaining solutions
        for (int i = problemDto.getSolutions().size(); i < problem.getSolutions().size(); i++) {
            Solution solution = solutionRepository.findByProblemAndSolutionNum(problem, i + 1).orElseThrow();
            solutionRepository.delete(solution);
        }

        return ResponseEntity.ok().body(new MessageResponse("Problem updated successfully"));
    }


    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ResponseEntity addProblem(@Valid @RequestBody ProblemDto problemDto) {
        Problem problem = new Problem();
        problem.setName(problemDto.getName());
        problem.setUrlName(problemDto.getUrlName());
        problem.setCategory(problemDto.getCategory());
        problem.setDifficulty(problemDto.getDifficulty());
        problem.setDescription(problemDto.getDescription());
        problem.setPythonCode(problemDto.getPythonCode());
        problem.setJavaCode(problemDto.getJavaCode());
        problem.setJavascriptCode(problemDto.getJavascriptCode());

        // process problem steps
        List<ProblemStep> problemSteps = new ArrayList<>();
        for (int i = 0; i < problemDto.getProblemSteps().size(); i++) {
            var problemStepDto = problemDto.getProblemSteps().get(i);
            ProblemStep problemStep = new ProblemStep();
            problemStep.setProblem(problem);
            problemStep.setStepNum(i + 1);
            problemStep.setName(problemStepDto.getName());
            problemStep.setType(problemStepDto.getType());
            problemStep.setContent(problemStepDto.getContent());
            problemStep.setTime(problemStepDto.getTime());

            problemStepRepository.save(problemStep);
            problemSteps.add(problemStep);
        }
        problem.setProblemSteps(problemSteps);

        // process test cases
        List<TestCase> testCases = new ArrayList<>();
        for (int i = 0; i < problemDto.getTestCases().size(); i++) {
            var testCaseDto = problemDto.getTestCases().get(i);
            TestCase testCase = new TestCase();
            testCase.setProblem(problem);
            testCase.setTestCaseNum(i + 1);
            testCase.setName(testCaseDto.getName());
            testCase.setIsDefault(testCaseDto.getIsDefault());
            testCase.setInput(testCaseDto.getInput());
            testCase.setOutput(testCaseDto.getOutput());

            testCaseRepository.save(testCase);
            testCases.add(testCase);
        }
        problem.setTestCases(testCases);

        // process solutions
        List<Solution> solutions = new ArrayList<>();
        for (int i = 0; i < problemDto.getSolutions().size(); i++) {
            var solutionDto = problemDto.getSolutions().get(i);
            Solution solution = new Solution();
            solution.setProblem(problem);
            solution.setSolutionNum(i + 1);
            solution.setName(solutionDto.getName());
            solution.setIsPrimary(solutionDto.getIsPrimary());
            solution.setDescription(solutionDto.getDescription());
            solution.setPythonCode(solutionDto.getPythonCode());
            solution.setJavaCode(solutionDto.getJavaCode());
            solution.setJavascriptCode(solutionDto.getJavascriptCode());

            solutionRepository.save(solution);
            solutions.add(solution);
        }
        problem.setSolutions(solutions);

        problemRepository.save(problem);
        return ResponseEntity.ok().body(new MessageResponse("Problem saved successfully"));
    }

    @PostMapping("/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity deleteProblem(@Valid @RequestBody DeleteProblemRequest deleteProblemRequest) {
        Problem problem = problemRepository.findById(deleteProblemRequest.getId()).orElseThrow(() -> new RecordNotFoundException("Problem not found"));
        problem.setDeleted(true);
        problemRepository.save(problem);

        return ResponseEntity.ok().body(new MessageResponse("Problem deleted successfully"));
    }
}
