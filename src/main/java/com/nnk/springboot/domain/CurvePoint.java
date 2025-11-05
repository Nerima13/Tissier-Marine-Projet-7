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

    private Integer curveId;

    private Timestamp asOfDate;

    @Digits(integer = 10, fraction = 2)
    private Double term;

    @Digits(integer = 10, fraction = 2)
    private Double value;

    private Timestamp creationDate;
}
