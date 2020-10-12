package com.coderintuition.CoderIntuition.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "problem")
@Getter
@Setter
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @JsonIgnoreProperties("problem")
    @OneToMany(mappedBy = "problem")
    private List<ProblemStep> problemSteps;

    @JsonIgnoreProperties("problem")
    @OneToMany(mappedBy = "problem")
    private List<TestCase> testCases;

    @JsonIgnoreProperties("problem")
    @OneToMany(mappedBy = "problem")
    private List<Solution> solutions;

    @Column(name = "name")
    private String name;

    @Column(name = "url_name")
    private String urlName;

    @Column(name = "category")
    private String category;

    @Column(name = "difficulty")
    private Integer difficulty;

    @Column(name = "num_steps")
    private Integer numSteps;

    @Column(name = "total_time")
    private Integer totalTime;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "python_code")
    private String pythonCode;

    @Column(name = "java_code")
    private String javaCode;

    @Column(name = "javascript_code")
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
