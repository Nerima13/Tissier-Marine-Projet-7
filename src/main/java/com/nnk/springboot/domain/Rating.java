package com.nnk.springboot.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "Rating")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Size(max = 125)
    private String moodysRating;

    @Size(max = 125)
    private String sandPRating;

    @Size(max = 125)
    private String fitchRating;

    @Digits(integer = 10, fraction = 0, message = "Order number must contain only digits")
    private Integer orderNumber;
}
