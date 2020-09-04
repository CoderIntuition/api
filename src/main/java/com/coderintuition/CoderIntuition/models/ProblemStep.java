package com.coderintuition.CoderIntuition.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "problem_step")
@Getter
@Setter
public class ProblemStep {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @JsonIgnoreProperties("problemSteps")
    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @Column(name = "step_num")
    private Integer stepNum;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "text_contents", columnDefinition = "TEXT")
    private String textContents;

    @Column(name = "quiz_contents", columnDefinition = "TEXT")
    private String quizContents;

    @Column(name = "time")
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
