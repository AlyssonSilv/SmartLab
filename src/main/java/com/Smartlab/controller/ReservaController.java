package com.Smartlab.controller;

import com.Smartlab.model.Reserva;
import com.Smartlab.repository.ReservaRepository;
import com.Smartlab.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ReservaService reservaService;

    @GetMapping
    public List<Reserva> listar() {
        return reservaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> buscar(@PathVariable Long id) {
        return reservaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody Reserva reserva) {
        try {
            Reserva criada = reservaService.criarReserva(reserva);
            return ResponseEntity.ok(criada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(e.getMessage()); // 409 Conflict
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reserva> atualizar(@PathVariable Long id, @RequestBody Reserva novaReserva) {
        return reservaRepository.findById(id).map(reserva -> {
            reserva.setInicio(novaReserva.getInicio());
            reserva.setFim(novaReserva.getFim());
            reserva.setAmbiente(novaReserva.getAmbiente());
            reserva.setUsuario(novaReserva.getUsuario());
            reserva.setDescricao(novaReserva.getDescricao());
            Reserva atualizada = reservaRepository.save(reserva);
            return ResponseEntity.ok(atualizada);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (reservaRepository.existsById(id)) {
            reservaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
