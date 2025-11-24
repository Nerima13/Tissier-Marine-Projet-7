package com.nnk.springboot.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @NotBlank(message = "Username is mandatory")
    @Size(max = 125)
    private String username;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, max = 125, message = "Password must be at least 8 characters long.")
    private String password;

    @NotBlank(message = "Fullname is mandatory")
    @Size(max = 125)
    private String fullname;

    @NotBlank(message = "Role is mandatory")
    @Size(max = 125)
    private String role;

    // Constructors
    public User() {
    }

    public User(Integer id,
                String username,
                String password,
                String fullname,
                String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.role = role;
    }

    // Getters / Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}