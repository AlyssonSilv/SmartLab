package com.labmanager.model;

import jakarta.persistence.*; // Anotações JPA
import jakarta.validation.constraints.NotNull; // Validação
import jakarta.validation.constraints.Size;   // Validação
import java.time.LocalDateTime; // Para datas e horas
import java.util.List; // Para a lista de periféricos

/**
 * Entidade que representa um agendamento de laboratório
 * Mapeia para a tabela 'bookings' no banco de dados
 */
@Entity // Declara que é uma entidade JPA
@Table(name = "bookings") // Mapeia para a tabela 'bookings'
public class Booking {

    @Id // Chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Geração automática de ID
    private Long id;

    // Relacionamento com usuário - quem fez o agendamento
    @ManyToOne(fetch = FetchType.LAZY) // Muitos agendamentos para um usuário, carregamento sob demanda
    @JoinColumn(name = "user_id") // Coluna de junção na tabela 'bookings'
    @NotNull // O usuário não pode ser nulo
    private User user;

    // Relacionamento com laboratório - qual laboratório foi agendado
    @ManyToOne(fetch = FetchType.LAZY) // Muitos agendamentos para um laboratório, carregamento sob demanda
    @JoinColumn(name = "laboratory_id") // Coluna de junção na tabela 'bookings'
    @NotNull // O laboratório não pode ser nulo
    private Laboratory laboratory;

    // Data e hora de início do agendamento
    @Column(name = "start_time") // Mapeia para a coluna 'start_time'
    @NotNull // Não pode ser nulo
    private LocalDateTime startTime;

    // Data e hora de fim do agendamento
    @Column(name = "end_time") // Mapeia para a coluna 'end_time'
    @NotNull // Não pode ser nulo
    private LocalDateTime endTime;

    // Finalidade do agendamento (aula, pesquisa, etc.)
    @Size(max = 500) // Tamanho máximo da string
    private String purpose;

    // Status atual do agendamento
    @Enumerated(EnumType.STRING) // Armazena o enum como String no banco de dados
    @Column(length = 20) // Tamanho máximo da coluna para o status (ex: "PENDING")
    private BookingStatus status = BookingStatus.PENDING; // Valor padrão: PENDING

    // Observações do administrador (motivo da rejeição, etc.)
    @Size(max = 1000)
    @Column(name = "admin_notes")
    private String adminNotes; // Usado para 'rejectionReason' no frontend

    // Timestamps para auditoria
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relacionamento com periféricos solicitados no agendamento
    // 'mappedBy' indica que a coluna de junção está na entidade BookingPeripheral
    // 'cascade = CascadeType.ALL' propaga operações (salvar, deletar) para os periféricos
    // 'fetch = FetchType.LAZY' carregamento sob demanda
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookingPeripheral> bookingPeripherals;

    // Métodos de callback JPA para gerenciar timestamps automaticamente
    @PrePersist // Executado antes de persistir uma nova entidade
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate // Executado antes de atualizar uma entidade existente
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Construtores
    public Booking() {}

    public Booking(User user, Laboratory laboratory, LocalDateTime startTime,
                   LocalDateTime endTime, String purpose) {
        this.user = user;
        this.laboratory = laboratory;
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
    }

    // Getters e Setters (padrão)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Laboratory getLaboratory() { return laboratory; }
    public void setLaboratory(Laboratory laboratory) { this.laboratory = laboratory; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public String getAdminNotes() { return adminNotes; } // Mapeado para `rejectionReason` no frontend
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<BookingPeripheral> getBookingPeripherals() { return bookingPeripherals; }
    public void setBookingPeripherals(List<BookingPeripheral> bookingPeripherals) {
        this.bookingPeripherals = bookingPeripherals;
    }
}