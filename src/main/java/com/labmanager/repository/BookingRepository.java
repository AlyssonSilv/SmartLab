package com.labmanager.repository;

import com.labmanager.model.Booking;
import com.labmanager.model.BookingStatus;
import com.labmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório para operações de banco de dados relacionadas aos agendamentos
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    // Busca agendamentos por usuário, ordenados por data de criação (mais recentes primeiro)
    List<Booking> findByUserOrderByCreatedAtDesc(User user);
    
    // Busca agendamentos por status, ordenados por data de criação
    List<Booking> findByStatusOrderByCreatedAtDesc(BookingStatus status);
    
    // Busca agendamentos pendentes (para aprovação do admin)
    List<Booking> findByStatusOrderByCreatedAtAsc(BookingStatus status);
    
    // Busca agendamentos de um usuário específico com status específico
    List<Booking> findByUserAndStatusOrderByCreatedAtDesc(User user, BookingStatus status);
    
    // Busca agendamentos em um período específico
    @Query("SELECT b FROM Booking b WHERE b.startTime >= :startDate AND b.endTime <= :endDate ORDER BY b.startTime")
    List<Booking> findBookingsInPeriod(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);
    
    // Verifica conflitos de agendamento para um laboratório em um período
    @Query("SELECT b FROM Booking b WHERE b.laboratory.id = :laboratoryId AND " +
           "b.status = 'APPROVED' AND " +
           "((b.startTime < :endTime AND b.endTime > :startTime))")
    List<Booking> findConflictingBookings(@Param("laboratoryId") Long laboratoryId,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);
    
    // Conta agendamentos por status
    long countByStatus(BookingStatus status);
    
    // Busca agendamentos recentes (últimos 7 dias)
    @Query("SELECT b FROM Booking b WHERE b.createdAt >= :sevenDaysAgo ORDER BY b.createdAt DESC")
    List<Booking> findRecentBookings(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);
    
    // Busca próximos agendamentos aprovados (próximos 7 dias)
    @Query("SELECT b FROM Booking b WHERE b.status = 'APPROVED' AND " +
           "b.startTime >= :now AND b.startTime <= :sevenDaysFromNow ORDER BY b.startTime")
    List<Booking> findUpcomingBookings(@Param("now") LocalDateTime now, 
                                     @Param("sevenDaysFromNow") LocalDateTime sevenDaysFromNow);
}
