package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.pojos.response.ProblemsResponse;
import com.coderintuition.CoderIntuition.pojos.response.SimpleProblemDto;
import com.coderintuition.CoderIntuition.enums.Category;
import com.coderintuition.CoderIntuition.models.Problem;
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
import java.util.List;
import java.util.Optional;

@RestController
public class ProblemController {

    @Autowired
    ProblemRepository problemRepository;
    @Autowired
    ProblemStepRepository problemStepRepository;
    @Autowired
    TestCaseRepository testCaseRepository;

    @GetMapping(value = "/problems/{category}", params = {"page", "size"})
    public ProblemsResponse getProblemsByCategory(@PathVariable String category,
                                                  @RequestParam("page") int page,
                                                  @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Problem> problems = problemRepository.findByCategory(Category.valueOf(category.toUpperCase()), pageable);
        List<SimpleProblemDto> simpleProblemDtos = new ArrayList<>();
        // convert Problem to SimpleProblemDto
        for (Problem problem : problems) {
            SimpleProblemDto simpleProblemDto = new SimpleProblemDto();
            simpleProblemDto.setId(problem.getId());
            simpleProblemDto.setName(problem.getName());
            simpleProblemDto.setUrlName(problem.getUrlName());
            simpleProblemDto.setCategory(problem.getCategory());
            simpleProblemDto.setDifficulty(problem.getDifficulty());
            simpleProblemDtos.add(simpleProblemDto);
        }
        return new ProblemsResponse(problems.getTotalPages(), problems.getTotalPages(), simpleProblemDtos);
    }

    @GetMapping("/problem/id/{id}")
    public Optional<Problem> getProblemById(@PathVariable Long id) {
        return problemRepository.findById(id);
    }

    @GetMapping("/problem/{urlName}")
    public Optional<Problem> getProblemByUrlname(@PathVariable String urlName) {
        return problemRepository.findByUrlName(urlName);
    }
}
