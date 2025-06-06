package com.labmanager.service;

import com.labmanager.repository.UserRepository; // Importa UserRepository para buscar usuários
import com.labmanager.dto.BookingRequest;
import com.labmanager.dto.BookingStatusUpdateRequest;
import com.labmanager.model.*; // Importa Booking, BookingPeripheral, Laboratory, Peripheral, User, UserRole
import com.labmanager.repository.BookingPeripheralRepository;
import com.labmanager.repository.BookingRepository;
import com.labmanager.repository.LaboratoryRepository;
import com.labmanager.repository.PeripheralRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Para transações ACID

import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço de agendamentos
 * Responsável por todas as operações relacionadas aos agendamentos de laboratórios
 */
@Service // Indica que é um componente de serviço Spring
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private LaboratoryRepository laboratoryRepository;

    @Autowired
    private PeripheralRepository peripheralRepository;

    @Autowired
    private BookingPeripheralRepository bookingPeripheralRepository;

    @Autowired
    private AuthService authService; // Para obter o usuário autenticado

    @Autowired
    private UserRepository userRepository; // Para buscar usuários por ID

    /**
     * Cria um novo agendamento
     * Valida disponibilidade do laboratório e cria o agendamento com periféricos
     * @param bookingRequest Dados do agendamento
     * @return Agendamento criado
     */
    @Transactional // Garante que a operação seja atômica
    public Booking createBooking(BookingRequest bookingRequest) {
        User currentUser = authService.getCurrentUser(); // Obtém o usuário que está fazendo o agendamento

        // Busca o laboratório
        Laboratory laboratory = laboratoryRepository.findById(bookingRequest.getLaboratoryId())
                .orElseThrow(() -> new RuntimeException("Laboratório não encontrado"));

        // Valida se o laboratório está ativo
        if (!laboratory.getActive()) {
            throw new RuntimeException("Laboratório não está disponível para agendamento (inativo).");
        }

        // Valida se a data de início é anterior à data de fim
        if (bookingRequest.getStartTime().isAfter(bookingRequest.getEndTime())) {
            throw new RuntimeException("Data e hora de início devem ser anteriores à data e hora de fim.");
        }

        // Valida se o agendamento é para o futuro
        if (bookingRequest.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Não é possível agendar para datas/horas passadas.");
        }

        // Verifica conflitos de horário (usando a query otimizada do repositório)
        List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(
                laboratory.getId(), bookingRequest.getStartTime(), bookingRequest.getEndTime());

        if (!conflictingBookings.isEmpty()) {
            throw new RuntimeException("Laboratório já está agendado para este horário e período.");
        }

        // Cria o agendamento
        Booking booking = new Booking(currentUser, laboratory,
                bookingRequest.getStartTime(), bookingRequest.getEndTime(),
                bookingRequest.getPurpose());

        booking = bookingRepository.save(booking); // Salva o agendamento principal

        // Adiciona periféricos solicitados
        if (bookingRequest.getPeripherals() != null && !bookingRequest.getPeripherals().isEmpty()) {
            for (BookingRequest.PeripheralRequest peripheralRequest : bookingRequest.getPeripherals()) {
                Peripheral peripheral = peripheralRepository.findById(peripheralRequest.getPeripheralId())
                        .orElseThrow(() -> new RuntimeException("Periférico com ID " + peripheralRequest.getPeripheralId() + " não encontrado."));

                // Valida se o periférico pertence ao laboratório (opcional, mas boa prática)
                if (!peripheral.getLaboratory().getId().equals(laboratory.getId())) {
                    throw new RuntimeException("Periférico '" + peripheral.getName() + "' não pertence ao laboratório selecionado.");
                }

                // Verifica disponibilidade de quantidade
                if (peripheral.getQuantity() < peripheralRequest.getQuantity()) {
                    throw new RuntimeException("Quantidade insuficiente do periférico: " + peripheral.getName() + ". Disponível: " + peripheral.getQuantity() + ", Solicitado: " + peripheralRequest.getQuantity());
                }

                BookingPeripheral bookingPeripheral = new BookingPeripheral(
                        booking, peripheral, peripheralRequest.getQuantity());
                bookingPeripheralRepository.save(bookingPeripheral); // Salva a associação periférico-agendamento
            }
        }

        return booking;
    }

    

    /**
     * Busca agendamentos do usuário atual
     * @return Lista de agendamentos do usuário
     */
    public List<Booking> getMyBookings() {
        User currentUser = authService.getCurrentUser();
        return bookingRepository.findByUserOrderByCreatedAtDesc(currentUser);
    }

    /**
     * Busca agendamentos por um ID de usuário específico (para uso no DashboardController)
     * Este método foi adicionado para ser chamado pelo DashboardController
     * @param userId ID do usuário
     * @return Lista de agendamentos do usuário
     */
    public List<Booking> findBookingsByUser(Long userId) {
        User user = userRepository.findById(userId) // Supondo que userRepository está injetado
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado para buscar agendamentos."));
        return bookingRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * Busca todos os agendamentos (apenas para admin - a autorização é no Controller)
     * @return Lista de todos os agendamentos
     */
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    /**
     * Busca agendamentos pendentes de aprovação
     * @return Lista de agendamentos pendentes
     */
    public List<Booking> getPendingBookings() {
        return bookingRepository.findByStatusOrderByCreatedAtAsc(BookingStatus.PENDING);
    }

    /**
     * Busca agendamentos por status (para uso no DashboardController, por exemplo)
     * @param status O status do agendamento
     * @return Lista de agendamentos com o status especificado
     */
    public List<Booking> findBookingsByStatus(BookingStatus status) {
        return bookingRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    

    /**
     * Atualiza o status de um agendamento (aprovar/rejeitar/cancelar/completar)
     * Apenas administradores e professores podem aprovar/rejeitar.
     * @param bookingId ID do agendamento
     * @param newStatus O novo status a ser definido (APPROVED, REJECTED, CANCELLED, COMPLETED)
     * @param adminNotes Observações do administrador/professor (motivo da rejeição, etc.)
     * @return Agendamento atualizado
     */
    @Transactional
    public Booking updateBookingStatus(Long bookingId, BookingStatus newStatus, String adminNotes) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado."));

        // Regras de transição de status (ex: só pode mudar PENDING para APPROVED/REJECTED)
        // Isso pode ser mais complexo dependendo das suas regras de negócio.
        if (booking.getStatus() == BookingStatus.PENDING &&
           (newStatus == BookingStatus.APPROVED || newStatus == BookingStatus.REJECTED)) {
            // OK para ADMIN/PROFESSOR
        } else if (booking.getStatus() != BookingStatus.CANCELLED &&
                   booking.getStatus() != BookingStatus.COMPLETED &&
                   newStatus == BookingStatus.CANCELLED) {
            // OK para cancelar (se não estiver já cancelado ou completo)
        } else if (booking.getStatus() == BookingStatus.APPROVED &&
                   newStatus == BookingStatus.COMPLETED) {
            // OK para marcar como concluído (se estiver aprovado)
        } else {
            throw new RuntimeException("Transição de status inválida de " + booking.getStatus() + " para " + newStatus + ".");
        }

        booking.setStatus(newStatus);
        booking.setAdminNotes(adminNotes); // Adiciona ou atualiza as notas do admin/motivo da rejeição

        return bookingRepository.save(booking);
    }

    /**
     * Cancela um agendamento
     * Usuários podem cancelar seus próprios agendamentos
     * @param bookingId ID do agendamento
     * @return Agendamento cancelado
     */
    @Transactional
    public Booking cancelBooking(Long bookingId) {
        User currentUser = authService.getCurrentUser();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        // Verifica se o usuário é o dono do agendamento ou é admin
        if (!booking.getUser().getId().equals(currentUser.getId()) &&
                currentUser.getRole() != UserRole.ADMIN) { // Supondo que UserRole.ADMIN esteja acessível aqui
            throw new RuntimeException("Você não tem permissão para cancelar este agendamento");
        }

        // Verifica se o agendamento pode ser cancelado
        if (booking.getStatus() == BookingStatus.CANCELLED ||
                booking.getStatus() == BookingStatus.COMPLETED) {
            throw new RuntimeException("Este agendamento não pode ser cancelado");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }


    /**
     * Deleta um agendamento
     * @param bookingId ID do agendamento a ser deletado
     */
    @Transactional
    public void deleteBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado."));

        // Opcional: Adicionar validações de permissão ou status antes de deletar
        // if (booking.getStatus() == BookingStatus.APPROVED) {
        //    throw new RuntimeException("Não é possível deletar agendamentos aprovados diretamente. Cancele primeiro.");
        // }

        // Como BookingPeripheral tem CascadeType.ALL em Booking, ao deletar o Booking,
        // os BookingPeripherals associados também serão deletados.
        // bookingPeripheralRepository.deleteByBooking(booking); // Esta linha não é estritamente necessária devido ao CascadeType.ALL
        bookingRepository.delete(booking);
    }

    /**
     * Busca agendamentos em um período específico
     * @param startDate Data de início
     * @param endDate Data de fim
     * @return Lista de agendamentos no período
     */
    public List<Booking> getBookingsInPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return bookingRepository.findBookingsInPeriod(startDate, endDate);
    }
}