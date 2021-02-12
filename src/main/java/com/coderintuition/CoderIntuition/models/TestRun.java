package com.coderintuition.CoderIntuition.models;

import com.coderintuition.CoderIntuition.enums.Language;
import com.coderintuition.CoderIntuition.enums.TestStatus;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "test_run")
@Getter
@Setter
public class TestRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    @NotNull
    private Problem problem;

    @Column(name = "token")
    @NotBlank
    @Size(max = 100)
    private String token;

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    @NotNull
    private Language language;

    @Column(name = "code", columnDefinition = "TEXT")
    @NotBlank
    private String code;

    @Column(name = "input", columnDefinition = "TEXT")
    @NotBlank
    private String input;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TestStatus status;

    @Column(name = "expected_output", columnDefinition = "TEXT")
    private String expectedOutput;

    @Column(name = "output", columnDefinition = "TEXT")
    private String output;

    @Column(name = "stdout", columnDefinition = "TEXT")
    private String stdout;

    @Column(name = "stderr", columnDefinition = "TEXT")
    private String stderr;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date created_at;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updated_at;
}
