package com.coderintuition.CoderIntuition.models;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.coderintuition.CoderIntuition.common.JSONObjectConverter;
import com.coderintuition.CoderIntuition.enums.AuthProvider;
import com.coderintuition.CoderIntuition.enums.ERole;
import com.coderintuition.CoderIntuition.enums.Language;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;

@Entity
@Table(name = "user", uniqueConstraints = { @UniqueConstraint(columnNames = "email") })
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uuid")
    @Size(max = 100)
    private String uuid;

    @Column(name = "username")
    @NotBlank
    @Size(max = 20)
    private String username;

    @Column(name = "name")
    @NotBlank
    @Size(max = 300)
    private String name;

    @Column(name = "email")
    @NotBlank
    @Size(max = 300)
    @Email
    private String email;

    @Column(name = "verified")
    @NotNull
    private Boolean verified;

    @Column(name = "password")
    @Size(max = 150)
    @JsonIgnore
    private String password;

    @Column(name = "image_url")
    @Size(max = 500)
    private String imageUrl;

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private Language language;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @Column(name = "auth_provider")
    @Enumerated(EnumType.STRING)
    @NotNull
    private AuthProvider authProvider;

    @Column(name = "auth_provider_id")
    @Size(max = 200)
    private String authProviderId;

    @OneToMany(mappedBy = "user")
    private List<Submission> submissions;

    @JsonIgnoreProperties("user")
    @OneToMany(mappedBy = "user")
    private List<Activity> activities;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "user_badge", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "badge_id"))
    private List<Badge> badges;

    @Column(name = "points")
    private Long points;

    @Column(name = "github_link")
    @Size(max = 200)
    private String githubLink;

    @Column(name = "linkedin_link")
    @Size(max = 200)
    private String linkedinLink;

    @Column(name = "website_link")
    @Size(max = 200)
    private String websiteLink;

    @Column(name = "stripe_customer_id")
    @Size(max = 200)
    private String stripeCustomerId;

    @Column(name = "plus_expiration_date")
    private Date plusExpirationDate;

    @Column(name = "email_opt_out")
    private Boolean emailOptOut;

    @Column(name = "last_email_sent_at")
    private Date lastEmailSentAt;

    @Column(name = "problems_sent", columnDefinition = "json")
    @Convert(converter = JSONObjectConverter.class)
    private String problemsSent;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date created_at;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updated_at;

    public User(String uuid, String name, String email, String password, Boolean verified, String username,
            AuthProvider authProvider, Set<Role> roles) {
        this.uuid = uuid;
        this.name = name;
        this.email = email;
        this.password = password;
        this.verified = verified;
        this.username = username;
        this.authProvider = authProvider;
        this.roles = roles;
    }

    public boolean hasRole(ERole role) {
        return roles.stream().anyMatch(x -> x.getName() == role);
    }
}
