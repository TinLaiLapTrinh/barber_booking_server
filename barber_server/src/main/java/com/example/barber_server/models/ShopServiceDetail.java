    package com.example.barber_server.models;

    import jakarta.persistence.*;
    import jakarta.validation.constraints.NotNull;
    import lombok.Getter;
    import lombok.Setter;
    import org.hibernate.annotations.ColumnDefault;
    import org.hibernate.annotations.OnDelete;
    import org.hibernate.annotations.OnDeleteAction;

    import java.time.Instant;

    @Getter
    @Setter
    @Entity
    @Table(name = "shop_service_details")
    public class ShopServiceDetail {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id", nullable = false)
        private Integer id;

        @NotNull
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @OnDelete(action = OnDeleteAction.CASCADE)
        @JoinColumn(name = "shop_service_id", nullable = false)
        private ShopService shopService;

        @NotNull
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @OnDelete(action = OnDeleteAction.CASCADE)
        @JoinColumn(name = "service_detail_id", nullable = false)
        private ServiceDetail serviceDetail;

        @NotNull
        @ColumnDefault("0")
        @Column(name = "price", nullable = false)
        private Float price;

        @ColumnDefault("1")
        @Column(name = "is_active")
        private Boolean isActive;

        @ColumnDefault("CURRENT_TIMESTAMP")
        @Column(name = "created_at")
        private Instant createdAt;

        @ColumnDefault("CURRENT_TIMESTAMP")
        @Column(name = "updated_at")
        private Instant updatedAt;

    }