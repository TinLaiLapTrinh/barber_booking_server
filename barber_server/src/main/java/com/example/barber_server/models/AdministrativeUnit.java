package com.example.barber_server.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "administrative_units")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AdministrativeUnit {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @Column(name = "full_name")
    private String fullName;

    @Size(max = 255)
    @Column(name = "full_name_en")
    private String fullNameEn;

    @Size(max = 255)
    @Column(name = "short_name")
    private String shortName;

    @Size(max = 255)
    @Column(name = "short_name_en")
    private String shortNameEn;

    @Size(max = 255)
    @Column(name = "code_name")
    private String codeName;

    @Size(max = 255)
    @Column(name = "code_name_en")
    private String codeNameEn;


}