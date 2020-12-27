package com.coderintuition.CoderIntuition.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

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
    @OneToMany(mappedBy = "problem")
    @NotEmpty
    private List<ProblemStep> problemSteps;

    @JsonIgnoreProperties("problem")
    @OneToMany(mappedBy = "problem")
    @NotEmpty
    private List<TestCase> testCases;

    @JsonIgnoreProperties("problem")
    @OneToMany(mappedBy = "problem")
    @NotEmpty
    private List<Solution> solutions;

    @JsonIgnoreProperties("problem")
    @OneToMany(mappedBy = "problem")
    @NotEmpty
    private List<Argument> arguments;

    @JsonIgnoreProperties("problem")
    @OneToOne(mappedBy = "problem")
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

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "difficulty")
    @NotNull
    @Min(1)
    @Max(5)
    private Integer difficulty;

    @Column(name = "description", columnDefinition = "TEXT")
    @NotBlank
    private String description;

    @Column(name = "python_code", columnDefinition = "TEXT")
    @NotBlank
    private String pythonCode;

    @Column(name = "java_code", columnDefinition = "TEXT")
    @NotNull
    private String javaCode;

    @Column(name = "javascript_code", columnDefinition = "TEXT")
    @NotNull
    private String javascriptCode;

    @Column(name = "deleted")
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
}
