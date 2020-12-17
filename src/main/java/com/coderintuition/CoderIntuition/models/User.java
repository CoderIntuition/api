package com.coderintuition.CoderIntuition.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

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
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Column(name = "auth_provider")
    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @Column(name = "auth_provider_id")
    @Size(max = 200)
    private String authProviderId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_activity",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "activity_id"))
    private List<Activity> activities = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_badge",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "badge_id"))
    private List<Badge> badges = new ArrayList<>();

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

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date created_at;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updated_at;

    public User(String name, String email, String password, Boolean verified, String username, AuthProvider authProvider, Set<Role> roles) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.verified = verified;
        this.username = username;
        this.authProvider = authProvider;
        this.roles = roles;
    }
}
