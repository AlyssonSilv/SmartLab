package com.labmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore; // <-- IMPORTAR ISTO!

/**
 * Entidade que representa um usuário do sistema
 * Mapeia para a tabela 'users' no banco de dados
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RA (Registro Acadêmico) - identificador único do usuário na instituição
    @NotBlank
    @Size(max = 20)
    @Column(unique = true)
    private String ra;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 100)
    @Email
    private String email;

    // Senha criptografada com BCrypt
    @NotBlank
    @Size(max = 120)
    private String password;

    // Tipo de usuário (ADMIN, PROFESSOR, ALUNO)
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private UserRole role;

    // Controle de usuários ativos/inativos
    private Boolean active = true;

    // Timestamps para auditoria
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relacionamento com agendamentos - um usuário pode ter vários agendamentos
    // --- CORREÇÃO AQUI: Adicionado @JsonIgnore ---
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // <-- ADICIONADO: Ignora este campo na serialização JSON para evitar LazyInitializationException
    private List<Booking> bookings;

    // Métodos de callback JPA para definir timestamps automaticamente
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Construtores
    public User() {}

    public User(String ra, String name, String email, String password, UserRole role) {
        this.ra = ra;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRa() { return ra; }
    public void setRa(String ra) { this.ra = ra; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Booking> getBookings() { return bookings; } // Mantenha o getter, o JsonIgnore lida com a serialização
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }
}