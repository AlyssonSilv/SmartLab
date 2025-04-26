package com.Smartlab.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "horarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String diaSemana; // SEGUNDA, TERÇA, ...

    private String horaInicio;

    private String horaFim;
}
