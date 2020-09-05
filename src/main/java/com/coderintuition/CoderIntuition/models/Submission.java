package com.coderintuition.CoderIntuition.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "submission")
@Getter
@Setter
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @Column(name = "language")
    private String language;

    @Column(name = "status")
    private String status;

    @Column(name = "code", columnDefinition = "TEXT")
    private String code;

    @JsonIgnoreProperties("submission")
    @OneToMany(mappedBy = "submission")
    private List<SubmissionRun> submissionRuns;
}

