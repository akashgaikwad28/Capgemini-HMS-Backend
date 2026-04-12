package com.capgemini.hms.appointment.service;

import com.capgemini.hms.appointment.entity.Appointment;
import com.capgemini.hms.appointment.repository.AppointmentRepository;
import com.capgemini.hms.nurse.repository.NurseRepository;
import com.capgemini.hms.patient.repository.PatientRepository;
import com.capgemini.hms.physician.repository.PhysicianRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final PhysicianRepository physicianRepository;
    private final NurseRepository nurseRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              PhysicianRepository physicianRepository,
                              NurseRepository nurseRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.physicianRepository = physicianRepository;
        this.nurseRepository = nurseRepository;
    }

    @Transactional
    public Appointment bookAppointment(Appointment appointment) {
        validateAppointment(appointment);
        appointment.setIsDeleted(false);
        return appointmentRepository.save(appointment);
    }

    public Page<Appointment> getAllAppointments(Pageable pageable) {
        return appointmentRepository.findAllActive(pageable);
    }

    public List<Appointment> getPhysicianSchedule(Integer physicianId) {
        return appointmentRepository.findByPhysicianActive(physicianId);
    }

    public List<Appointment> getPatientAppointments(Integer ssn) {
        return appointmentRepository.findByPatientActive(ssn);
    }

    public Optional<Appointment> getAppointmentById(Integer id) {
        return appointmentRepository.findById(id)
                .filter(a -> !a.getIsDeleted());
    }

    @Transactional
    public Appointment updateAppointment(Appointment appointment) {
        Appointment existing = appointmentRepository.findById(appointment.getAppointmentId())
                .filter(a -> !a.getIsDeleted())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        validateAppointment(appointment);
        
        existing.setStart(appointment.getStart());
        existing.setEnd(appointment.getEnd());
        existing.setExaminationRoom(appointment.getExaminationRoom());
        existing.setPhysician(appointment.getPhysician());
        existing.setPrepNurse(appointment.getPrepNurse());
        existing.setPatient(appointment.getPatient());
        
        return appointmentRepository.save(existing);
    }

    @Transactional
    public void deleteAppointment(Integer id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setIsDeleted(true);
        appointmentRepository.save(appointment);
    }

    private void validateAppointment(Appointment appointment) {
        if (appointment.getEnd().isBefore(appointment.getStart())) {
            throw new RuntimeException("End time cannot be before start time");
        }

        if (!patientRepository.existsById(appointment.getPatient().getSsn())) {
            throw new RuntimeException("Patient not found");
        }
        if (!physicianRepository.existsById(appointment.getPhysician().getEmployeeId())) {
            throw new RuntimeException("Physician not found");
        }
        if (appointment.getPrepNurse() != null && !nurseRepository.existsById(appointment.getPrepNurse().getEmployeeId())) {
            throw new RuntimeException("Preparatory Nurse not found");
        }

        List<Appointment> doctorConflicts = appointmentRepository.findOverlappingPhysicianAppointments(
                appointment.getPhysician().getEmployeeId(),
                appointment.getStart(),
                appointment.getEnd());
        
        if (doctorConflicts.stream().anyMatch(a -> !a.getAppointmentId().equals(appointment.getAppointmentId()))) {
            throw new RuntimeException("Physician is already busy during this time slot");
        }

        List<Appointment> roomConflicts = appointmentRepository.findOverlappingRoomAppointments(
                appointment.getExaminationRoom(),
                appointment.getStart(),
                appointment.getEnd());
        
        if (roomConflicts.stream().anyMatch(a -> !a.getAppointmentId().equals(appointment.getAppointmentId()))) {
            throw new RuntimeException("Examination room " + appointment.getExaminationRoom() + " is already booked");
        }
    }
}
