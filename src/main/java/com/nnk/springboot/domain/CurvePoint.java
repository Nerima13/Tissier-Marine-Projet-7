package com.nnk.springboot.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.sql.Timestamp;

@Entity
@Table(name = "CurvePoint")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurvePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Digits(integer = 10, fraction = 0, message = "Curve Id must contain only digits")
    private Integer curveId;

    private Timestamp asOfDate;

    @Digits(integer = 10, fraction = 2, message = "Term must contain only digits")
    private Double term;

    @Digits(integer = 10, fraction = 2, message = "Value must contain only digits")
    private Double value;

    private Timestamp creationDate;
}
