package com.coderintuition.CoderIntuition.models;

import com.coderintuition.CoderIntuition.enums.ArgumentType;
import com.coderintuition.CoderIntuition.enums.UnderlyingType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "return_type")
@Getter
@Setter
@NoArgsConstructor
public class ReturnType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JsonIgnoreProperties("return_type")
    @OneToOne
    @JoinColumn(name = "problem_id")
    @NotNull
    private Problem problem;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ArgumentType type;

    @Column(name = "underlying_type")
    @Enumerated(EnumType.STRING)
    private UnderlyingType underlyingType;

    @Column(name = "underlying_type_2")
    @Enumerated(EnumType.STRING)
    private UnderlyingType underlyingType2;

    @Column(name = "order_matters")
    @NotNull
    private Boolean orderMatters;
}