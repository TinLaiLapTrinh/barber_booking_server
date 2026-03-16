package com.example.barber_server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "order_details")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "order_id")
    private Order order;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_service_detail_id", nullable = false)
    private ShopServiceDetail shopServiceDetail;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "original_price", nullable = false)
    private Float originalPrice;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "final_price", nullable = false)
    private Float finalPrice;


}