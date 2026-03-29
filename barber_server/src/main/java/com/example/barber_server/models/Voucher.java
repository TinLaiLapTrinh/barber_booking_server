package com.example.barber_server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "vouchers")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 45)
    @NotNull
    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @NotNull
    @Column(name = "discount", nullable = false)
    private Double discount;

    @NotNull
    @Column(name = "expiry", nullable = false)
    private Integer expiry;

    @NotNull
    @Column(name = "date_start", nullable = false)
    private LocalDate dateStart;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    @NotNull
    @ColumnDefault("0")
    @Column(name = "discount_type", nullable = false)
    private Boolean discountType;
    @NotNull
    @ColumnDefault("0")
    @Column(name = "min_order_value", nullable = false)
    private Double minOrderValue;
    @Column(name = "max_discount_value")
    private Double maxDiscountValue;


}