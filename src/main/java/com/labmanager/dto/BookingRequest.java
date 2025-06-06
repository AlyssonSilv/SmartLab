package com.labmanager.dto;

import jakarta.validation.constraints.NotNull; // Validação: campo não pode ser nulo
import jakarta.validation.constraints.Size;   // Validação: tamanho máximo da string
import java.time.LocalDateTime;              // Para datas e horas
import java.util.List;                       // Para a lista de periféricos

/**
 * DTO para requisição de criação de agendamento
 * Contém todos os dados necessários para criar um novo agendamento
 */
public class BookingRequest {

    @NotNull
    private Long laboratoryId; // ID do laboratório que está sendo agendado

    @NotNull
    private LocalDateTime startTime; // Data e hora de início do agendamento

    @NotNull
    private LocalDateTime endTime;   // Data e hora de fim do agendamento

    @Size(max = 500)
    private String purpose;          // Finalidade do agendamento

    // Lista de IDs dos periféricos solicitados com suas quantidades
    // Usará a classe interna PeripheralRequest para mapear o JSON
    private List<PeripheralRequest> peripherals;

    // Classe interna para representar periféricos solicitados
    // Isso é uma excelente prática para encapsular dados relacionados à requisição
    public static class PeripheralRequest {
        private Long peripheralId; // ID do periférico
        private Integer quantity = 1; // Quantidade do periférico (valor padrão: 1)

        // Construtores
        public PeripheralRequest() {}

        public PeripheralRequest(Long peripheralId, Integer quantity) {
            this.peripheralId = peripheralId;
            this.quantity = quantity;
        }

        // Getters e Setters
        public Long getPeripheralId() { return peripheralId; }
        public void setPeripheralId(Long peripheralId) { this.peripheralId = peripheralId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; } // Corrigido erro de digitação no setter
    }

    // Construtores
    public BookingRequest() {}

    public BookingRequest(Long laboratoryId, LocalDateTime startTime, LocalDateTime endTime, String purpose) {
        this.laboratoryId = laboratoryId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
    }

    // Getters e Setters
    public Long getLaboratoryId() { return laboratoryId; }
    public void setLaboratoryId(Long laboratoryId) { this.laboratoryId = laboratoryId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public List<PeripheralRequest> getPeripherals() { return peripherals; }
    public void setPeripherals(List<PeripheralRequest> peripherals) { this.peripherals = peripherals; }
}