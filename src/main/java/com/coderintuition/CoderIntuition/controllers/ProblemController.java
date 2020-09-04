package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.models.Problem;
import com.coderintuition.CoderIntuition.repositories.ProblemRepository;
import com.coderintuition.CoderIntuition.repositories.ProblemStepRepository;
import com.coderintuition.CoderIntuition.repositories.TestcaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class ProblemController {

    @Autowired
    ProblemRepository problemRepository;
    @Autowired
    ProblemStepRepository problemStepRepository;
    @Autowired
    TestcaseRepository testcaseRepository;

    @GetMapping("/problems/{category}")
    public List<Problem> getProblemsByCategory(@PathVariable String category) {
        return problemRepository.findByCategory(category);
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
