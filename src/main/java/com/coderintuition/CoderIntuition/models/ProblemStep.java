package com.coderintuition.CoderIntuition.models;

import com.coderintuition.CoderIntuition.enums.ProblemStepType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;

@Entity
@Table(name = "problem_step")
@Getter
@Setter
public class ProblemStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JsonIgnoreProperties("problemSteps")
    @ManyToOne
    @JoinColumn(name = "problem_id")
    @NotNull
    private Problem problem;

    @Column(name = "step_num")
    @NotNull
    @Positive
    private Integer stepNum;

    @Column(name = "name")
    @NotBlank
    @Size(max = 300)
    private String name;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ProblemStepType type;

    @Column(name = "content", columnDefinition = "TEXT")
    @NotBlank
    private String content;

    @Column(name = "time")
    @NotNull
    @PositiveOrZero
    private Integer time;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date created_at;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updated_at;
}
