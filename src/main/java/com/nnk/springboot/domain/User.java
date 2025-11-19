package com.nnk.springboot.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "Users")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
