package com.example.sigema.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "UnidadEmails")
@Getter
@Setter
public class UnidadEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "unidad_id", nullable = false)
    private Unidad unidad;
}
