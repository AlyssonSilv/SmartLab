package com.Smartlab.service;

import com.Smartlab.model.Reserva;
import com.Smartlab.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    public boolean isDisponivel(LocalDateTime inicio, LocalDateTime fim, Long ambienteId) {
        List<Reserva> reservasExistentes = reservaRepository.findAll();

        return reservasExistentes.stream().noneMatch(r ->
            r.getAmbiente().getId().equals(ambienteId) &&
            !(fim.isBefore(r.getInicio()) || inicio.isAfter(r.getFim()))
        );
    }

    public Reserva criarReserva(Reserva reserva) {
        if (isDisponivel(reserva.getInicio(), reserva.getFim(), reserva.getAmbiente().getId())) {
            return reservaRepository.save(reserva);
        } else {
            throw new RuntimeException("Ambiente indisponível no período solicitado.");
        }
    }
}
