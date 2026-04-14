package com.capgemini.hms.prescription.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * View controller for Prescription management pages.
 *
 * Client-side JS calls:
 *   GET    /api/v1/prescriptions/patient/{ssn}   → all prescriptions for a patient
 *   POST   /api/v1/prescriptions                 → issue new prescription (ADMIN, DOCTOR)
 */
@Controller
@RequestMapping("/prescription")
public class PrescriptionViewController {

    @GetMapping
    public String listPrescriptions() {
        return "prescription/list";
    }
}
