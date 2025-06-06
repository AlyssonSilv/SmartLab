package com.labmanager.service;

import com.labmanager.dto.DashboardStatsResponse;
import com.labmanager.model.Booking;
import com.labmanager.model.BookingStatus;
import com.labmanager.repository.BookingRepository;
import com.labmanager.repository.LaboratoryRepository;
import com.labmanager.repository.PeripheralRepository;
import com.labmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço do dashboard
 * Responsável por fornecer estatísticas e dados para os dashboards dos usuários
 */
@Service
public class DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LaboratoryRepository laboratoryRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PeripheralRepository peripheralRepository;

    /**
     * Busca estatísticas gerais do sistema para o dashboard administrativo
     * @return Estatísticas completas do sistema
     */
    public DashboardStatsResponse getAdminStats() {
        long totalUsers = userRepository.countByActiveTrue();
        long totalLaboratories = laboratoryRepository.countByActiveTrue();
        long totalBookings = bookingRepository.count();
        long pendingBookings = bookingRepository.countByStatus(BookingStatus.PENDING);
        long approvedBookings = bookingRepository.countByStatus(BookingStatus.APPROVED);
        long rejectedBookings = bookingRepository.countByStatus(BookingStatus.REJECTED);
        long totalPeripherals = peripheralRepository.countByActiveTrue();

        return new DashboardStatsResponse(totalUsers, totalLaboratories, totalBookings,
                pendingBookings, approvedBookings, rejectedBookings, totalPeripherals);
    }

    /**
     * Busca estatísticas básicas para usuários não-admin
     * @return Estatísticas limitadas
     */
    public DashboardStatsResponse getBasicStats() {
        long totalLaboratories = laboratoryRepository.countByActiveTrue();
        long totalBookings = bookingRepository.count();
        long pendingBookings = bookingRepository.countByStatus(BookingStatus.PENDING);
        long approvedBookings = bookingRepository.countByStatus(BookingStatus.APPROVED);

        return new DashboardStatsResponse(0, totalLaboratories, totalBookings,
                pendingBookings, approvedBookings, 0, 0);
    }

    /**
     * Busca atividades recentes do sistema (últimos 7 dias)
     * @return Lista de agendamentos recentes
     */
    public List<Booking> getRecentActivities() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        return bookingRepository.findRecentBookings(sevenDaysAgo);
    }

    /**
     * Busca próximos agendamentos aprovados (próximos 7 dias)
     * @return Lista de agendamentos futuros
     */
    public List<Booking> getUpcomingBookings() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysFromNow = now.plusDays(7);
        return bookingRepository.findUpcomingBookings(now, sevenDaysFromNow);
    }
}
