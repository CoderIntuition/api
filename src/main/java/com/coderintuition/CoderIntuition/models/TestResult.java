package com.coderintuition.CoderIntuition.models;

import com.coderintuition.CoderIntuition.enums.TestStatus;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "test_result")
@Getter
@Setter
public class TestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "submission_id")
    @NotNull
    private Submission submission;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @NotNull
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

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    @JsonIgnore
    private Date created_at;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    @JsonIgnore
    private Date updated_at;
}
