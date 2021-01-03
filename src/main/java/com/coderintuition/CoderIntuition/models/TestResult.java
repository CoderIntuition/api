package com.coderintuition.CoderIntuition.models;

import com.coderintuition.CoderIntuition.enums.TestStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "test_result")
@Getter
@Setter
public class TestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "submission_id")
    @NotNull
    private Submission submission;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TestStatus status;

    @Column(name = "input", columnDefinition = "TEXT")
    @NotNull
    private String input;

    @Column(name = "expected_output", columnDefinition = "TEXT")
    @NotNull
    private String expectedOutput;

    @Column(name = "output", columnDefinition = "TEXT")
    @NotNull
    private String output;
}
