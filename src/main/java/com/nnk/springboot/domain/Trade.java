package com.nnk.springboot.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "Trade")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TradeId")
    private Integer tradeId;

    @NotBlank(message = "Account is mandatory")
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String account;

    @NotBlank(message = "Type is mandatory")
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String type;

    @Digits(integer = 10, fraction = 2, message = "Buy Quantity must contain only digits")
    private Double buyQuantity;

    @Digits(integer = 10, fraction = 2, message = "Sell Quantity must contain only digits")
    private Double sellQuantity;

    @Digits(integer = 10, fraction = 2, message = "Buy Price must contain only digits")
    private Double buyPrice;

    @Digits(integer = 10, fraction = 2, message = "Sell Price must contain only digits")
    private Double sellPrice;

    private LocalDateTime tradeDate;

    @Size(max = 125)
    private String security;

    @Size(max = 10)
    private String status;

    @Size(max = 125)
    private String trader;

    @Size(max = 125)
    private String benchmark;

    @Size(max = 125)
    private String book;

    @Size(max = 125)
    private String creationName;

    @CreationTimestamp
    private LocalDateTime creationDate;

    @Size(max = 125)
    private String revisionName;

    @UpdateTimestamp
    private LocalDateTime revisionDate;

    @Size(max = 125)
    private String dealName;

    @Size(max = 125)
    private String dealType;

    @Size(max = 125)
    private String sourceListId;

    @Size(max = 125)
    private String side;
}