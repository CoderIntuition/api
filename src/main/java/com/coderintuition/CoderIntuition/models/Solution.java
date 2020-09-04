package com.coderintuition.CoderIntuition.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
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
    private Problem problem;

    @Column(name = "solution_num")
    private Integer solutionNum;

    @Column(name = "name")
    private String name;

    @Column(name = "is_primary")
    private Boolean isPrimary;

    @Column(name = "code", columnDefinition = "TEXT")
    private String code;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date created_at;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updated_at;
}
