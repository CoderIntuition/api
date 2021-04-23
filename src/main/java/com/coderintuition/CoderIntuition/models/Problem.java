package com.coderintuition.CoderIntuition.models;

import com.coderintuition.CoderIntuition.enums.Difficulty;
import com.coderintuition.CoderIntuition.enums.Language;
import com.coderintuition.CoderIntuition.enums.ProblemCategory;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
@Table(name = "problem")
@Getter
@Setter
@NoArgsConstructor
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JsonIgnoreProperties("problem")
    @OneToMany(mappedBy = "problem", fetch = FetchType.EAGER)
    @NotEmpty
    private Set<ProblemStep> problemSteps;

    @JsonIgnoreProperties("problem")
    @OneToMany(mappedBy = "problem", fetch = FetchType.EAGER)
    @NotEmpty
    private Set<TestCase> testCases;

    @JsonIgnoreProperties("problem")
    @OneToMany(mappedBy = "problem", fetch = FetchType.EAGER)
    @NotEmpty
    private Set<Solution> solutions;

    @JsonIgnoreProperties("problem")
    @OneToMany(mappedBy = "problem", fetch = FetchType.EAGER)
    @NotEmpty
    private Set<Argument> arguments;

    @JsonIgnoreProperties("problem")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "return_type_id", referencedColumnName = "id")
    @NotNull
    private ReturnType returnType;

    @Column(name = "name")
    @NotBlank
    @Size(max = 300)
    private String name;

    @Column(name = "url_name", unique = true)
    @NotBlank
    @Size(max = 300)
    private String urlName;

    @Column(name = "plus_only")
    @NotNull
    private Boolean plusOnly;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    @NotNull
    private ProblemCategory category;

    @Column(name = "difficulty")
    @Enumerated(EnumType.STRING)
    @NotNull
    private Difficulty difficulty;

    @Column(name = "description", columnDefinition = "TEXT")
    @NotBlank
    private String description;

    @Column(name = "python_code", columnDefinition = "TEXT")
    @NotBlank // at least python code must be provided
    private String pythonCode;

    @Column(name = "java_code", columnDefinition = "TEXT")
    @NotNull
    private String javaCode;

    @Column(name = "javascript_code", columnDefinition = "TEXT")
    @NotNull
    private String javascriptCode;

    @Column(name = "deleted")
    @NotNull
    @JsonIgnore
    private Boolean deleted;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date created_at;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updated_at;

    public String getCode(Language language) {
        switch (language) {
            case PYTHON:
                return pythonCode;
            case JAVA:
                return javaCode;
            case JAVASCRIPT:
                return javascriptCode;
            default:
                return "";
        }
    }

    @JsonIgnoreProperties("problem")
    public TestCase getDefaultTestCase() {
        return testCases.stream().filter(TestCase::getIsDefault).findFirst().orElseThrow();
    }

    @JsonIgnoreProperties("problem")
    public List<Argument> getOrderedArguments() {
        List<Argument> newList = new ArrayList<>(arguments);
        newList.sort(Comparator.comparing(Argument::getArgumentNum));
        return newList;
    }

    @JsonIgnoreProperties("problem")
    public List<ProblemStep> getOrderedProblemSteps() {
        List<ProblemStep> newList = new ArrayList<>(problemSteps);
        newList.sort(Comparator.comparing(ProblemStep::getStepNum));
        return newList;
    }

    @JsonIgnoreProperties("problem")
    public List<TestCase> getOrderedTestCases() {
        List<TestCase> newList = new ArrayList<>(testCases);
        newList.sort(Comparator.comparing(TestCase::getTestCaseNum));
        return newList;
    }

    @JsonIgnoreProperties("problem")
    public List<Solution> getOrderedSolutions() {
        List<Solution> newList = new ArrayList<>(solutions);
        newList.sort(Comparator.comparing(Solution::getSolutionNum));
        return newList;
    }
}
