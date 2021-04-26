package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.common.Utils;
import com.coderintuition.CoderIntuition.enums.ProblemCategory;
import com.coderintuition.CoderIntuition.models.Problem;
import com.coderintuition.CoderIntuition.pojos.response.CategoryDto;
import com.coderintuition.CoderIntuition.pojos.response.ProblemResponse;
import com.coderintuition.CoderIntuition.pojos.response.ProblemsResponse;
import com.coderintuition.CoderIntuition.pojos.response.SimpleProblemResponse;
import com.coderintuition.CoderIntuition.repositories.ProblemRepository;
import com.coderintuition.CoderIntuition.repositories.ProblemStepRepository;
import com.coderintuition.CoderIntuition.repositories.TestCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class ProblemController {

    @Autowired
    ProblemRepository problemRepository;
    @Autowired
    ProblemStepRepository problemStepRepository;
    @Autowired
    TestCaseRepository testCaseRepository;

    @GetMapping("/problems-by-category")
    public Map<String, CategoryDto> getAllProblemsByCategory() {
        Map<String, CategoryDto> map = new HashMap<>();
        for (ProblemCategory category : ProblemCategory.values()) {
            CategoryDto categoryDto = new CategoryDto();
            categoryDto.setName(Utils.capitalize(category.toString().toLowerCase()));
            List<Problem> categoryProblems = problemRepository.findByCategory(category);
            categoryDto.setResults(simplifyProblems(categoryProblems));
            map.put(Utils.capitalize(category.toString().toLowerCase()), categoryDto);
        }
        return map;
    }

    @GetMapping("/all-problems")
    public List<String> getAllProblems() {
        return problemRepository.findAllPublicUrlNames();
    }

    @GetMapping("/all-categories")
    public List<String> getAllCategories() {
        return Stream.of(ProblemCategory.values())
            .map(Enum::name)
            .collect(Collectors.toList());
    }

    @GetMapping(value = "/problems/{category}", params = {"page", "size"})
    public ProblemsResponse getProblemsByCategory(@PathVariable String category,
                                                  @RequestParam("page") int page,
                                                  @RequestParam("size") int size) throws Exception {
        Pageable pageable = PageRequest.of(page, size);

        if (category.equalsIgnoreCase("ALL")) {
            Page<Problem> problems = problemRepository.findAll(pageable);
            return new ProblemsResponse(problems.getTotalPages(), (int) problems.getTotalElements(), simplifyProblems(problems.toList()));
        }

        try {
            Page<Problem> problems = problemRepository.findByCategory(ProblemCategory.valueOf(category.toUpperCase()), pageable);
            return new ProblemsResponse(problems.getTotalPages(), (int) problems.getTotalElements(), simplifyProblems(problems.toList()));
        } catch (Exception e) {
            throw new Exception("Invalid category");
        }
    }

    @GetMapping("/problem/id/{id}")
    public ProblemResponse getProblemById(@PathVariable Long id) {
        Problem problem = problemRepository.findById(id).orElseThrow();
        return ProblemResponse.fromProblem(problem);
    }

    @GetMapping("/problem/{urlName}")
    public ProblemResponse getProblemByUrlName(@PathVariable String urlName) {
        Problem problem = problemRepository.findByUrlName(urlName).orElseThrow();
        return ProblemResponse.fromProblem(problem);
    }

    private List<SimpleProblemResponse> simplifyProblems(List<Problem> problems) {
        List<SimpleProblemResponse> simpleProblemResponses = new ArrayList<>();
        for (Problem problem : problems) {
            simpleProblemResponses.add(SimpleProblemResponse.fromProblem(problem));
        }
        return simpleProblemResponses;
    }
}
