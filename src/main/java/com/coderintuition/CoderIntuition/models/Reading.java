package com.coderintuition.CoderIntuition.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "reading")
@Getter
@Setter
public class Reading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

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

    @Column(name = "content", columnDefinition = "TEXT")
    @NotBlank
    private String content;
}
