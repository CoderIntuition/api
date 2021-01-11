package com.coderintuition.CoderIntuition.models;

import com.coderintuition.CoderIntuition.enums.IssueCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "contact")
@Getter
@Setter
@NoArgsConstructor
public class SupportTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email")
    @NotBlank
    @Size(max = 300)
    @Email
    private String email;

    @Column(name = "name")
    @NotBlank
    @Size(max = 100)
    private String name;

    @Column(name = "subject")
    @NotBlank
    @Size(max = 500)
    private String subject;

    @Column(name = "message")
    @NotBlank
    @Size(max = 2000)
    private String message;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date created_at;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updated_at;
}