package com.coderintuition.CoderIntuition.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "solution")
@Getter
@Setter
public class Solution {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @JsonIgnoreProperties("solutions")
    @ManyToOne
    @JoinColumn(name = "problem_id")
    @NotNull
    private Problem problem;

    @Column(name = "solution_num")
    @NotNull
    @Positive
    private Integer solutionNum;

    @Column(name = "name")
    @NotBlank
    @Size(max = 300)
    private String name;

    @Column(name = "is_primary")
    @NotNull
    private Boolean isPrimary;

    @Column(name = "description", columnDefinition = "TEXT")
    @NotBlank
    private String description;

    @Column(name = "python_code", columnDefinition = "TEXT")
    @NotBlank
    private String pythonCode;

    @Column(name = "java_code", columnDefinition = "TEXT")
    private String javaCode;

    @Column(name = "javascript_code", columnDefinition = "TEXT")
    private String javascriptCode;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date created_at;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updated_at;
}
