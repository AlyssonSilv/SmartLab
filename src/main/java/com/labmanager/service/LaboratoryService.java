package com.labmanager.service;

import com.labmanager.model.Laboratory;
import com.labmanager.model.Peripheral;
import com.labmanager.repository.LaboratoryRepository;
import com.labmanager.repository.PeripheralRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço de laboratórios
 * Responsável por operações relacionadas aos laboratórios e seus periféricos
 */
@Service
public class LaboratoryService {

    @Autowired
    private LaboratoryRepository laboratoryRepository;

    @Autowired
    private PeripheralRepository peripheralRepository;

    /**
     * Busca todos os laboratórios ativos
     * @return Lista de laboratórios ativos
     */
    public List<Laboratory> getAllLaboratories() {
        return laboratoryRepository.findByActiveTrue();
    }

    /**
     * Busca laboratórios disponíveis em um período específico
     * @param startTime Data/hora de início
     * @param endTime Data/hora de fim
     * @return Lista de laboratórios disponíveis
     */
    public List<Laboratory> getAvailableLaboratories(LocalDateTime startTime, LocalDateTime endTime) {
        return laboratoryRepository.findAvailableLaboratories(startTime, endTime);
    }

    /**
     * Busca um laboratório por ID
     * @param id ID do laboratório
     * @return Laboratório encontrado
     */
    public Laboratory getLaboratoryById(Long id) {
        return laboratoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Laboratório não encontrado"));
    }

    /**
     * Cria um novo laboratório
     * Apenas administradores podem executar esta operação
     * @param laboratory Dados do laboratório
     * @return Laboratório criado
     */
    @Transactional
    public Laboratory createLaboratory(Laboratory laboratory) {
        return laboratoryRepository.save(laboratory);
    }

    /**
     * Atualiza um laboratório existente
     * @param id ID do laboratório
     * @param laboratoryDetails Novos dados do laboratório
     * @return Laboratório atualizado
     */
    @Transactional
    public Laboratory updateLaboratory(Long id, Laboratory laboratoryDetails) {
        Laboratory laboratory = getLaboratoryById(id);
        
        laboratory.setName(laboratoryDetails.getName());
        laboratory.setDescription(laboratoryDetails.getDescription());
        laboratory.setCapacity(laboratoryDetails.getCapacity());
        laboratory.setLocation(laboratoryDetails.getLocation());
        
        return laboratoryRepository.save(laboratory);
    }

    /**
     * Desativa um laboratório (soft delete)
     * @param id ID do laboratório
     */
    @Transactional
    public void deleteLaboratory(Long id) {
        Laboratory laboratory = getLaboratoryById(id);
        laboratory.setActive(false);
        laboratoryRepository.save(laboratory);
    }

    /**
     * Busca periféricos de um laboratório específico
     * @param laboratoryId ID do laboratório
     * @return Lista de periféricos do laboratório
     */
    public List<Peripheral> getLaboratoryPeripherals(Long laboratoryId) {
        return peripheralRepository.findByLaboratoryIdAndActiveTrue(laboratoryId);
    }

    /**
     * Adiciona um periférico a um laboratório
     * @param laboratoryId ID do laboratório
     * @param peripheral Dados do periférico
     * @return Periférico criado
     */
    @Transactional
    public Peripheral addPeripheralToLaboratory(Long laboratoryId, Peripheral peripheral) {
        Laboratory laboratory = getLaboratoryById(laboratoryId);
        peripheral.setLaboratory(laboratory);
        return peripheralRepository.save(peripheral);
    }

    /**
     * Atualiza um periférico
     * @param peripheralId ID do periférico
     * @param peripheralDetails Novos dados do periférico
     * @return Periférico atualizado
     */
    @Transactional
    public Peripheral updatePeripheral(Long peripheralId, Peripheral peripheralDetails) {
        Peripheral peripheral = peripheralRepository.findById(peripheralId)
                .orElseThrow(() -> new RuntimeException("Periférico não encontrado"));
        
        peripheral.setName(peripheralDetails.getName());
        peripheral.setDescription(peripheralDetails.getDescription());
        peripheral.setQuantity(peripheralDetails.getQuantity());
        
        return peripheralRepository.save(peripheral);
    }

    /**
     * Remove um periférico (soft delete)
     * @param peripheralId ID do periférico
     */
    @Transactional
    public void deletePeripheral(Long peripheralId) {
        Peripheral peripheral = peripheralRepository.findById(peripheralId)
                .orElseThrow(() -> new RuntimeException("Periférico não encontrado"));
        peripheral.setActive(false);
        peripheralRepository.save(peripheral);
    }
}
