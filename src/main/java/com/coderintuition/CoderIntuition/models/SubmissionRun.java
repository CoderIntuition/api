package com.coderintuition.CoderIntuition.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "submission_run")
@Getter
@Setter
public class SubmissionRun {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @JsonIgnoreProperties("submissionRuns")
    @ManyToOne
    @JoinColumn(name = "submission_id")
    private Submission submission;

    @Column(name = "token")
    private String token;

    @Column(name = "input", columnDefinition = "TEXT")
    private String input;

    @Column(name = "status")
    private String status;

    @Column(name = "expected_output", columnDefinition = "TEXT")
    private String expectedOutput;

    @Column(name = "output", columnDefinition = "TEXT")
    private String output;

    @Column(name = "stderr", columnDefinition = "TEXT")
    private String stderr;
}
