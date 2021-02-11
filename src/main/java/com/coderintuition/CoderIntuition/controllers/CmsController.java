package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.exceptions.RecordNotFoundException;
import com.coderintuition.CoderIntuition.models.*;
import com.coderintuition.CoderIntuition.pojos.request.cms.*;
import com.coderintuition.CoderIntuition.pojos.response.MessageResponse;
import com.coderintuition.CoderIntuition.repositories.*;
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

    @Autowired
    ArgumentRepository argumentRepository;

    @Autowired
    ReturnTypeRepository returnTypeRepository;

    @Autowired
    ReadingRepository readingRepository;

    @PostMapping("/problem/update")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ResponseEntity<?> updateProblem(@Valid @RequestBody UpdateProblemRequest updateProblemRequest) throws Exception {
        ProblemDto problemDto = updateProblemRequest.getProblem();
        if (problemDto.getTestCases().stream().filter(TestCaseDto::getIsDefault).count() != 1) {
            throw new Exception("There must be exactly one default test case");
        }
        if (problemDto.getSolutions().stream().filter(SolutionDto::getIsPrimary).count() != 1) {
            throw new Exception("There must be exactly one primary solution");
        }

        // update general problem info
        Problem problem = problemRepository.findById(updateProblemRequest.getId()).orElseThrow();
        problem.setName(problemDto.getName());
        problem.setUrlName(problemDto.getUrlName());
        problem.setPlusOnly(problemDto.getPlusOnly());
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

        // update arguments
        for (int i = 0; i < problemDto.getArguments().size(); i++) {
            var argumentDto = problemDto.getArguments().get(i);
            Argument argument;
            if (i < problem.getArguments().size()) {
                argument = argumentRepository.findByProblemAndArgumentNum(problem, i + 1).orElseThrow();
            } else {
                argument = new Argument();
                argument.setProblem(problem);
                argument.setArgumentNum(i + 1);
            }
            argument.setType(argumentDto.getType());
            argument.setUnderlyingType(argumentDto.getUnderlyingType());
            argument.setUnderlyingType2(argumentDto.getUnderlyingType2());
            argumentRepository.save(argument);
        }
        // delete remaining solutions
        for (int i = problemDto.getArguments().size(); i < problem.getArguments().size(); i++) {
            Argument argument = argumentRepository.findByProblemAndArgumentNum(problem, i + 1).orElseThrow();
            argumentRepository.delete(argument);
        }

        // update return type
        ReturnType returnType = returnTypeRepository.findByProblem(problem).orElseThrow();
        returnType.setType(problemDto.getReturnType().getType());
        returnType.setUnderlyingType(problemDto.getReturnType().getUnderlyingType());
        returnType.setUnderlyingType2(problemDto.getReturnType().getUnderlyingType2());
        returnTypeRepository.save(returnType);

        return ResponseEntity.ok().body(new MessageResponse("Problem updated successfully"));
    }


    @PostMapping("/problem/add")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ResponseEntity<?> addProblem(@Valid @RequestBody ProblemDto problemDto) throws Exception {
        if (problemDto.getTestCases().stream().filter(TestCaseDto::getIsDefault).count() != 1) {
            throw new Exception("There must be exactly one default test case");
        }
        if (problemDto.getSolutions().stream().filter(SolutionDto::getIsPrimary).count() != 1) {
            throw new Exception("There must be exactly one primary solution");
        }
        Problem problem = new Problem();
        problem.setName(problemDto.getName());
        problem.setUrlName(problemDto.getUrlName());
        problem.setPlusOnly(problemDto.getPlusOnly());
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

            solutions.add(solution);
        }
        problem.setSolutions(solutions);

        // process arguments
        List<Argument> arguments = new ArrayList<>();
        for (int i = 0; i < problemDto.getArguments().size(); i++) {
            var argumentDto = problemDto.getArguments().get(i);
            Argument argument = new Argument();
            argument.setProblem(problem);
            argument.setArgumentNum(i + 1);
            argument.setType(argumentDto.getType());
            argument.setUnderlyingType(argumentDto.getUnderlyingType());
            argument.setUnderlyingType2(argumentDto.getUnderlyingType2());

            arguments.add(argument);
        }
        problem.setArguments(arguments);

        // process return type
        ReturnType returnType = new ReturnType();
        returnType.setProblem(problem);
        returnType.setType(problemDto.getReturnType().getType());
        returnType.setUnderlyingType(problemDto.getReturnType().getUnderlyingType());
        returnType.setUnderlyingType2(problemDto.getReturnType().getUnderlyingType2());
        problem.setReturnType(returnType);

        problemRepository.save(problem);
        for (var problemStep : problemSteps) {
            problemStepRepository.save(problemStep);
        }
        for (var testCase : testCases) {
            testCaseRepository.save(testCase);
        }
        for (var solution : solutions) {
            solutionRepository.save(solution);
        }
        for (var argument : arguments) {
            argumentRepository.save(argument);
        }

        return ResponseEntity.ok().body(new MessageResponse("Problem saved successfully"));
    }

    @PostMapping("/problem/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteProblem(@Valid @RequestBody DeleteProblemRequest deleteProblemRequest) {
        Problem problem = problemRepository.findById(deleteProblemRequest.getId()).orElseThrow(() -> new RecordNotFoundException("Problem not found"));
        problem.setDeleted(true);
        problemRepository.save(problem);

        return ResponseEntity.ok().body(new MessageResponse("Problem deleted successfully"));
    }

    @PostMapping("/reading/add")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ResponseEntity<?> addReading(@Valid @RequestBody ReadingDto readingDto) {
        Reading reading = new Reading();
        reading.setName(readingDto.getName());
        reading.setUrlName(readingDto.getUrlName());
        reading.setPlusOnly(readingDto.getPlusOnly());
        reading.setContent(readingDto.getContent());
        readingRepository.save(reading);

        return ResponseEntity.ok().body(new MessageResponse("Reading saved successfully"));
    }

    @PostMapping("/reading/update")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ResponseEntity<?> updateReading(@Valid @RequestBody UpdateReadingRequest updateReadingRequest) {
        ReadingDto readingDto = updateReadingRequest.getReading();

        // update general problem info
        Reading reading = readingRepository.findById(updateReadingRequest.getId()).orElseThrow();
        reading.setName(readingDto.getName());
        reading.setUrlName(readingDto.getUrlName());
        reading.setPlusOnly(readingDto.getPlusOnly());
        reading.setContent(readingDto.getContent());
        readingRepository.save(reading);

        return ResponseEntity.ok().body(new MessageResponse("Reading updated successfully"));
    }
}
