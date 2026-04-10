package com.capgemini.hms.patient.service;

import com.capgemini.hms.patient.entity.Patient;
import com.capgemini.hms.patient.repository.PatientRepository;
import com.capgemini.hms.physician.repository.PhysicianRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final PhysicianRepository physicianRepository;

    public PatientService(PatientRepository patientRepository, PhysicianRepository physicianRepository) {
        this.patientRepository = patientRepository;
        this.physicianRepository = physicianRepository;
    }

    @Transactional
    public Patient registerPatient(Patient patient) {
        if (patientRepository.existsById(patient.getSsn())) {
            throw new RuntimeException("Patient with SSN " + patient.getSsn() + " already exists.");
        }
        patient.setIsDeleted(false);
        return patientRepository.save(patient);
    }

    public Page<Patient> getAllPatients(Pageable pageable) {
        return patientRepository.findAllActive(pageable);
    }

    public Optional<Patient> getPatientBySsn(Integer ssn) {
        return patientRepository.findById(ssn)
                .filter(p -> !p.getIsDeleted());
    }

    @Transactional
    public Patient updatePatient(Patient patient) {
        Patient existing = patientRepository.findById(patient.getSsn())
                .filter(p -> !p.getIsDeleted())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        existing.setName(patient.getName());
        existing.setAddress(patient.getAddress());
        existing.setPhone(patient.getPhone());
        existing.setInsuranceId(patient.getInsuranceId());
        existing.setPcp(patient.getPcp());
        
        return patientRepository.save(existing);
    }

    public Page<Patient> searchPatients(String query, Pageable pageable) {
        return patientRepository.searchActive(query, pageable);
    }

    @Transactional
    public void deletePatient(Integer ssn) {
        Patient patient = patientRepository.findById(ssn)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        patient.setIsDeleted(true);
        patientRepository.save(patient);
    }
}
