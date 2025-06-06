package com.labmanager.controller;

import com.labmanager.dto.DashboardStatsResponse;
import com.labmanager.model.Booking;
import com.labmanager.service.DashboardService;
import com.labmanager.service.BookingService;
import com.labmanager.model.User;
import com.labmanager.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.labmanager.model.BookingStatus;
import java.util.List;
import java.util.Map; // Para o body do PATCH de status de agendamento

/**
 * Controlador do dashboard
 * Endpoints: /api/dashboard/*
 * Responsável por fornecer dados estatísticos e atividades para os dashboards
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private AuthService authService;

    /**
     * Endpoint para estatísticas gerais do sistema
     * GET /api/dashboard/stats
     * Acessível por todos os usuários autenticados (Alunos, Professores)
     * @return Estatísticas básicas do sistema
     */
    @GetMapping("/stats")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getStats() { // ALTERADO: ResponseEntity<DashboardStatsResponse> para ResponseEntity<?>
        try {
            DashboardStatsResponse stats = dashboardService.getBasicStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("Erro ao obter estatísticas básicas do dashboard: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao obter estatísticas básicas: " + e.getMessage()); // Corrigido o retorno
        }
    }

    /**
     * Endpoint para estatísticas administrativas completas
     * GET /api/dashboard/admin/stats
     * Acessível apenas por administradores
     * @return Estatísticas completas do sistema
     */
    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAdminStats() { // ALTERADO: ResponseEntity<DashboardStatsResponse> para ResponseEntity<?>
        try {
            DashboardStatsResponse stats = dashboardService.getAdminStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("Erro ao obter estatísticas do dashboard ADMIN: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao obter estatísticas de admin: " + e.getMessage()); // Corrigido o retorno
        }
    }

    // --- NOVOS ENDPOINTS PARA DASHBOARD DE PROFESSOR E ALUNO ---

    /**
     * Endpoint para estatísticas específicas do professor
     * GET /api/dashboard/professor/stats
     * Acessível apenas por professores
     * @return Estatísticas relevantes para o professor
     */
    @GetMapping("/professor/stats")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> getProfessorStats() { // ALTERADO: ResponseEntity<DashboardStatsResponse> para ResponseEntity<?>
        try {
            DashboardStatsResponse stats = dashboardService.getBasicStats(); // Ou getProfessorStats()
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("Erro ao obter estatísticas do dashboard PROFESSOR: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao obter estatísticas de professor: " + e.getMessage()); // Corrigido o retorno
        }
    }

    /**
     * Endpoint para estatísticas específicas do aluno
     * GET /api/dashboard/student/stats
     * Acessível apenas por alunos
     * @return Estatísticas relevantes para o aluno
     */
    @GetMapping("/student/stats")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<?> getStudentStats() { // ALTERADO: ResponseEntity<DashboardStatsResponse> para ResponseEntity<?>
        try {
            DashboardStatsResponse stats = dashboardService.getBasicStats(); // Ou getStudentStats()
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("Erro ao obter estatísticas do dashboard ALUNO: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao obter estatísticas de aluno: " + e.getMessage()); // Corrigido o retorno
        }
    }

    /**
     * Endpoint para agendamentos do usuário logado (Aluno/Professor)
     * GET /api/bookings/my-bookings
     * @return Lista de agendamentos do usuário
     */
    @GetMapping("/bookings/my-bookings")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyBookings() { // ALTERADO: ResponseEntity<List<Booking>> para ResponseEntity<?>
        try {
            User currentUser = authService.getCurrentUser();
            List<Booking> myBookings = bookingService.findBookingsByUser(currentUser.getId());
            return ResponseEntity.ok(myBookings);
        } catch (Exception e) {
            System.err.println("Erro ao obter agendamentos do usuário logado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar seus agendamentos: " + e.getMessage()); // Corrigido o retorno
        }
    }

    /**
     * Endpoint para agendamentos pendentes (geralmente para Admin/Professor)
     * GET /api/dashboard/bookings/pending
     * @return Lista de agendamentos pendentes
     */
    @GetMapping("/bookings/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    public ResponseEntity<?> getPendingBookings() { // ALTERADO: ResponseEntity<List<Booking>> para ResponseEntity<?>
        try {
            List<Booking> pendingBookings = bookingService.findBookingsByStatus(com.labmanager.model.BookingStatus.PENDING);
            return ResponseEntity.ok(pendingBookings);
        } catch (Exception e) {
            System.err.println("Erro ao obter agendamentos pendentes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao obter agendamentos pendentes: " + e.getMessage()); // Corrigido o retorno
        }
    }

    /**
     * Endpoint para alertas do sistema (geralmente para Admin)
     * GET /api/dashboard/alerts
     * @return Lista de alertas do sistema
     */
    @GetMapping("/alerts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getSystemAlerts() { // ALTERADO: ResponseEntity<List<String>> para ResponseEntity<?>
        try {
            List<String> alerts = List.of("Alerta 1: Lab X em manutenção", "Alerta 2: Servidor com uso alto"); // Mock simples
            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            System.err.println("Erro ao obter alertas do sistema: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao obter alertas do sistema: " + e.getMessage()); // Corrigido o retorno
        }
    }

    /**
     * Endpoint para atividades recentes (geral)
     * GET /api/dashboard/activities
     * @return Lista de agendamentos recentes (últimos 7 dias)
     */
    @GetMapping("/activities")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getRecentActivities() { // ALTERADO: ResponseEntity<List<Booking>> para ResponseEntity<?>
        try {
            List<Booking> activities = dashboardService.getRecentActivities();
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            System.err.println("Erro ao obter atividades recentes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao obter atividades recentes: " + e.getMessage()); // Corrigido o retorno
        }
    }

    /**
     * Endpoint para próximos agendamentos (geral)
     * GET /api/dashboard/upcoming
     * @return Lista de agendamentos futuros (próximos 7 dias)
     */
    @GetMapping("/upcoming")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUpcomingBookings() { // ALTERADO: ResponseEntity<List<Booking>> para ResponseEntity<?>
        try {
            List<Booking> upcoming = dashboardService.getUpcomingBookings();
            return ResponseEntity.ok(upcoming);
        } catch (Exception e) {
            System.err.println("Erro ao obter próximos agendamentos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao obter próximos agendamentos: " + e.getMessage()); // Corrigido o retorno
        }
    }

    // --- ENDPOINTS PARA AÇÕES EM AGENDAMENTOS (APROVAR/REJEITAR/DELETAR) ---

    /**
     * Endpoint para atualizar o status de um agendamento
     * PATCH /api/bookings/{id}/status
     * Acessível apenas por administradores ou professores
     * @param id ID do agendamento
     * @param requestBody Corpo da requisição com o novo status e, opcionalmente, motivo da rejeição
     * @return ResponseEntity indicando sucesso ou falha
     */
    @PatchMapping("/bookings/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    public ResponseEntity<?> updateBookingStatus(@PathVariable Long id, @RequestBody Map<String, String> requestBody) {
        try {
            String status = requestBody.get("status");
            String rejectedReason = requestBody.get("rejectedReason");

            if (status == null || (!status.equalsIgnoreCase("APPROVED") && !status.equalsIgnoreCase("REJECTED"))) {
                return ResponseEntity.badRequest().body("Status inválido. Use 'APPROVED' ou 'REJECTED'.");
            }

            BookingStatus newStatus = BookingStatus.valueOf(status.toUpperCase());
            bookingService.updateBookingStatus(id, newStatus, rejectedReason); // Adaptar no BookingService
            return ResponseEntity.ok("Status do agendamento atualizado com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao atualizar status do agendamento " + id + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar status do agendamento.");
        }
    }

    /**
     * Endpoint para deletar um agendamento
     * DELETE /api/bookings/{id}
     * Acessível apenas por administradores ou professores
     * @param id ID do agendamento
     * @return ResponseEntity indicando sucesso ou falha
     */
    @DeleteMapping("/bookings/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {
        try {
            bookingService.deleteBooking(id); // Adaptar no BookingService
            return ResponseEntity.ok("Agendamento excluído com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao excluir agendamento " + id + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao excluir agendamento.");
        }
    }

    // --- ENDPOINTS PARA CRIAÇÃO DE RESERVA (NOVA-RESERVA) ---
    /**
     * Endpoint para iniciar uma nova reserva.
     * POST /api/laboratories/{labId}/reserve
     * Acessível por Alunos e Professores.
     * Este endpoint seria o primeiro passo para uma reserva, antes de coletar todos os detalhes.
     * @param labId ID do laboratório a ser reservado.
     * @return Resposta de sucesso ou falha.
     */
    @PostMapping("/laboratories/{labId}/reserve")
    @PreAuthorize("hasAnyRole('ALUNO', 'PROFESSOR')")
    public ResponseEntity<?> initiateReservation(@PathVariable Long labId) {
        try {
            // Este método não cria a reserva completa, mas sim um "agendamento provisório"
            // ou apenas indica que o lab está disponível para reserva.
            // A lógica real de criação da reserva com data, hora, etc. seria em outro endpoint.
            // Por simplicidade, aqui apenas confirmamos que o lab existe e o usuário pode iniciar a reserva.
            // Você pode adicionar uma chamada a um bookingService.initiateBooking(labId, currentUser.getId());
            // ou apenas retornar um status de sucesso.
            return ResponseEntity.ok("Iniciando processo de reserva para o laboratório " + labId + ". Prossiga para os detalhes.");
        } catch (Exception e) {
            System.err.println("Erro ao iniciar reserva para laboratório " + labId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao iniciar reserva.");
        }
    }

    // Você provavelmente precisaria de um endpoint POST /api/bookings para criar a reserva completa
    // com todos os detalhes (data, hora, propósito, periféricos, etc.)
    // Exemplo:
    /*
    @PostMapping("/bookings")
    @PreAuthorize("hasAnyRole('ALUNO', 'PROFESSOR')")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest bookingRequest) {
        try {
            User currentUser = authService.getCurrentUser();
            Booking newBooking = bookingService.createBooking(bookingRequest, currentUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(newBooking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao criar reserva: " + e.getMessage());
        }
    }
    */
}