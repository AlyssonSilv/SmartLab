package com.Smartlab.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime inicio;

    private LocalDateTime fim;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "ambiente_id")
    private Ambiente ambiente;
    private String descricao; // Ex: Reunião com o cliente, Treinamento de equipe, etc.
    
    private String status; // Ex: PENDENTE, APROVADA, REJEITADA




}
