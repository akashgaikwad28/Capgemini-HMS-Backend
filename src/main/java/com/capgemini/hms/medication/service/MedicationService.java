package com.capgemini.hms.medication.service;

import com.capgemini.hms.medication.entity.Medication;
import com.capgemini.hms.medication.repository.MedicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Service
public class MedicationService {

    private final MedicationRepository medicationRepository;

    public MedicationService(MedicationRepository medicationRepository) {
        this.medicationRepository = medicationRepository;
    }

    @Transactional
    public Medication saveMedication(Medication medication) {
        medication.setIsDeleted(false);
        return medicationRepository.save(medication);
    }

    public Page<Medication> getAllMedications(Pageable pageable) {
        return medicationRepository.findAllActive(pageable);
    }

    public Optional<Medication> getMedicationByCode(Integer code) {
        return medicationRepository.findById(code)
                .filter(m -> !m.getIsDeleted());
    }

    @Transactional
    public Medication updateMedication(Medication medication) {
        Medication existing = medicationRepository.findById(medication.getCode())
                .filter(m -> !m.getIsDeleted())
                .orElseThrow(() -> new RuntimeException("Medication not found"));
        
        existing.setName(medication.getName());
        existing.setBrand(medication.getBrand());
        existing.setDescription(medication.getDescription());
        
        return medicationRepository.save(existing);
    }

    public Page<Medication> searchMedications(String query, Pageable pageable) {
        return medicationRepository.searchActive(query, pageable);
    }

    @Transactional
    public void deleteMedication(Integer code) {
        Medication med = medicationRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("Medication not found"));
        med.setIsDeleted(true);
        medicationRepository.save(med);
    }
}
