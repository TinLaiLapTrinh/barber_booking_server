package com.example.barber_server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "service_details")
public class ServiceDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "category_id", nullable = false)
    private ServiceCategory category;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Size(max = 255)
    @Column(name = "service_type")
    private String serviceType;

    @NotNull
    @Column(name = "base_price", nullable = false)
    private Float basePrice;

    @Size(max = 255)
    @Column(name = "description")
    private String description;
    @OneToMany(mappedBy = "serviceDetail")
    private Set<ServiceDetailImage> serviceDetailImages = new LinkedHashSet<>();


}