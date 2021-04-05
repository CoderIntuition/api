package com.coderintuition.CoderIntuition.models;

import com.coderintuition.CoderIntuition.common.Utils;
import com.coderintuition.CoderIntuition.enums.Language;
import com.coderintuition.CoderIntuition.enums.SubmissionStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "submission")
@Getter
@Setter
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    @NotNull
    private Problem problem;

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    @NotNull
    private Language language;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @NotNull
    private SubmissionStatus status;

    @Column(name = "code", columnDefinition = "TEXT")
    @NotBlank
    private String code;

    @Column(name = "output", columnDefinition = "TEXT")
    private String output;

    @Column(name = "stderr", columnDefinition = "TEXT")
    private String stderr;

    @Column(name = "token")
    @NotBlank
    @Size(max = 100)
    private String token;

    @JsonIgnoreProperties("submission")
    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL)
    private List<TestResult> testResults;

    @JsonIgnoreProperties("submissions")
    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @JsonIgnoreProperties("submission")
    @OneToOne(mappedBy = "submission")
    private Activity activity;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date created_at;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updated_at;
}

