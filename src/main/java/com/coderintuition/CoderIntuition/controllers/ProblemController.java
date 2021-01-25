package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.enums.ProblemCategory;
import com.coderintuition.CoderIntuition.models.Problem;
import com.coderintuition.CoderIntuition.pojos.response.CategoryDto;
import com.coderintuition.CoderIntuition.pojos.response.ProblemsResponse;
import com.coderintuition.CoderIntuition.pojos.response.SimpleProblemDto;
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
import reactor.util.StringUtils;

import java.util.*;

@RestController
public class ProblemController {

    @Autowired
    ProblemRepository problemRepository;
    @Autowired
    ProblemStepRepository problemStepRepository;
    @Autowired
    TestCaseRepository testCaseRepository;

    private List<SimpleProblemDto> simplifyProblems(List<Problem> problems) {
        List<SimpleProblemDto> simpleProblemDtos = new ArrayList<>();
        for (Problem problem : problems) {
            SimpleProblemDto simpleProblemDto = new SimpleProblemDto();
            simpleProblemDto.setId(problem.getId());
            simpleProblemDto.setName(problem.getName());
            simpleProblemDto.setUrlName(problem.getUrlName());
            simpleProblemDto.setPlusOnly(problem.getPlusOnly());
            simpleProblemDto.setCategory(problem.getCategory());
            simpleProblemDto.setDifficulty(problem.getDifficulty());
            simpleProblemDtos.add(simpleProblemDto);
        }
        return simpleProblemDtos;
    }

    @GetMapping("/allproblems")
    public Map<String, CategoryDto> getAllProblems() {
        Map<String, CategoryDto> map = new HashMap<>();
        for (ProblemCategory category : ProblemCategory.values()) {
            CategoryDto categoryDto = new CategoryDto();
            categoryDto.setName(StringUtils.capitalize(category.toString().toLowerCase()));
            List<Problem> categoryProblems = problemRepository.findByCategory(category);
            categoryDto.setResults(simplifyProblems(categoryProblems));
            map.put(StringUtils.capitalize(category.toString().toLowerCase()), categoryDto);
        }
        return map;
    }

    @GetMapping(value = "/problems/{category}", params = {"page", "size"})
    public ProblemsResponse getProblemsByCategory(@PathVariable String category,
                                                  @RequestParam("page") int page,
                                                  @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Problem> problems = problemRepository.findByCategory(ProblemCategory.valueOf(category.toUpperCase()), pageable);
        return new ProblemsResponse(problems.getTotalPages(), (int) problems.getTotalElements(), simplifyProblems(problems.toList()));
    }

    @GetMapping("/problem/id/{id}")
    public Problem getProblemById(@PathVariable Long id) {
        Problem problem = problemRepository.findById(id).orElseThrow();
        return problem;
    }

    @GetMapping("/problem/{urlName}")
    public Optional<Problem> getProblemByUrlname(@PathVariable String urlName) {
        return problemRepository.findByUrlName(urlName);
    }
}
