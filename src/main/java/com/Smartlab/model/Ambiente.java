package com.Smartlab.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ambientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ambiente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String localizacao;

    private String tipo; // Ex: Laboratório, Sala de Aula, Auditório

    private Integer capacidade;
}
