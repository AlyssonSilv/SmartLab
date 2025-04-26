package com.Smartlab.controller;

import com.Smartlab.model.Horario;
import com.Smartlab.repository.HorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

    @Autowired
    private HorarioRepository horarioRepository;

    @GetMapping
    public List<Horario> listar() {
        return horarioRepository.findAll();
    }

    @PostMapping
    public Horario cadastrar(@RequestBody Horario horario) {
        return horarioRepository.save(horario);
    }

    @GetMapping("/{id}")
    public Horario buscar(@PathVariable Long id) {
        return horarioRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Horario atualizar(@PathVariable Long id, @RequestBody Horario novoHorario) {
        return horarioRepository.findById(id).map(h -> {
            h.setDiaSemana(novoHorario.getDiaSemana());
            h.setHoraInicio(novoHorario.getHoraInicio());
            h.setHoraFim(novoHorario.getHoraFim());
            return horarioRepository.save(h);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        horarioRepository.deleteById(id);
    }
}
