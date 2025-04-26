package com.Smartlab.controller;

import com.Smartlab.model.Ambiente;
import com.Smartlab.repository.AmbienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ambientes")
public class AmbienteController {

    @Autowired
    private AmbienteRepository ambienteRepository;

    @GetMapping
    public List<Ambiente> listar() {
        return ambienteRepository.findAll();
    }

    @PostMapping
    public Ambiente cadastrar(@RequestBody Ambiente ambiente) {
        return ambienteRepository.save(ambiente);
    }

    @GetMapping("/{id}")
    public Ambiente buscar(@PathVariable Long id) {
        return ambienteRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Ambiente atualizar(@PathVariable Long id, @RequestBody Ambiente novoAmbiente) {
        return ambienteRepository.findById(id).map(a -> {
            a.setNome(novoAmbiente.getNome());
            a.setLocalizacao(novoAmbiente.getLocalizacao());
            a.setTipo(novoAmbiente.getTipo());
            a.setCapacidade(novoAmbiente.getCapacidade());
            return ambienteRepository.save(a);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        ambienteRepository.deleteById(id);
    }
}
