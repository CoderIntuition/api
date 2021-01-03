package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.models.Issue;
import com.coderintuition.CoderIntuition.models.Problem;
import com.coderintuition.CoderIntuition.pojos.request.IssueRequest;
import com.coderintuition.CoderIntuition.repositories.IssueRepository;
import com.coderintuition.CoderIntuition.repositories.ProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IssueController {

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @PostMapping("/issue")
    public void createIssue(@RequestBody IssueRequest issueRequest) {
        Problem problem = problemRepository.findById(issueRequest.getProblemId()).orElseThrow();
        Issue issue = new Issue();
        issue.setProblem(problem);
        issue.setEmail(issueRequest.getEmail());
        issue.setCategory(issueRequest.getCategory());
        issue.setDescription(issueRequest.getDescription());
        issueRepository.save(issue);
    }
}
