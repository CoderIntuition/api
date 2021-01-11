package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.config.AppProperties;
import com.coderintuition.CoderIntuition.models.Issue;
import com.coderintuition.CoderIntuition.models.Problem;
import com.coderintuition.CoderIntuition.models.SupportTicket;
import com.coderintuition.CoderIntuition.pojos.request.IssueRequest;
import com.coderintuition.CoderIntuition.pojos.request.SupportTicketRequest;
import com.coderintuition.CoderIntuition.repositories.IssueRepository;
import com.coderintuition.CoderIntuition.repositories.ProblemRepository;
import com.coderintuition.CoderIntuition.repositories.SupportTicketRepository;
import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.Mail;
import net.sargue.mailgun.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SupportController {

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private SupportTicketRepository supportTicketRepository;

    @Autowired
    private AppProperties appProperties;

    @PostMapping("/issue")
    public void createIssue(@RequestBody IssueRequest issueRequest) {
        Problem problem = problemRepository.findById(issueRequest.getProblemId()).orElseThrow();

        // save the issue to the db
        Issue issue = new Issue();
        issue.setProblem(problem);
        issue.setEmail(issueRequest.getEmail());
        issue.setCategory(issueRequest.getCategory());
        issue.setDescription(issueRequest.getDescription());
        issueRepository.save(issue);

        // send email to support about the issue
        Configuration configuration = new Configuration()
                .domain("coderintuition.com")
                .apiKey(appProperties.getMailgun().getKey())
                .from("CoderIntuition", "support@coderintuition.com");
        Response response = Mail.using(configuration)
                .body()
                .h1("New Issue")
                .p("Problem: " + problem.getName() + " (" + problem.getId() + ")")
                .p("Email: " + issue.getEmail())
                .p("Category: " + issue.getCategory())
                .p("Description:")
                .p(issue.getDescription())
                .mail()
                .to("support@coderintuition.com")
                .subject("New Issue: " + problem.getName())
                .build()
                .send();

        if (!response.isOk()) {
            System.out.println("Error sending email to support@coderintuition.com. Response code: "
                    + response.responseCode() + " with message: " + response.responseMessage());
        }
    }

    @PostMapping("/support")
    public void createSupportTicket(@RequestBody SupportTicketRequest supportTicketRequest) {
        // save the contact to the db
        SupportTicket supportTicket = new SupportTicket();
        supportTicket.setEmail(supportTicketRequest.getEmail());
        supportTicket.setName(supportTicketRequest.getName());
        supportTicket.setSubject(supportTicketRequest.getSubject());
        supportTicket.setMessage(supportTicketRequest.getMessage());
        supportTicketRepository.save(supportTicket);

        // send email to support about the issue
        Configuration configuration = new Configuration()
                .domain("coderintuition.com")
                .apiKey(appProperties.getMailgun().getKey())
                .from("CoderIntuition", "support@coderintuition.com");
        Response response = Mail.using(configuration)
                .body()
                .h1("New Support Ticket")
                .p("Name: " + supportTicket.getName())
                .p("Email: " + supportTicket.getEmail())
                .p("Subject: " + supportTicket.getSubject())
                .p("Message:")
                .p(supportTicket.getMessage())
                .mail()
                .to("support@coderintuition.com")
                .subject("New Support Ticket: " + supportTicket.getSubject())
                .build()
                .send();

        if (!response.isOk()) {
            System.out.println("Error sending email to support@coderintuition.com. Response code: "
                    + response.responseCode() + " with message: " + response.responseMessage());
        }
    }
}
