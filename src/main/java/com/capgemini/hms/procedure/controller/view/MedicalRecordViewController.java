package com.capgemini.hms.procedure.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * View controller for Medical Records pages.
 *
 * Client-side JS calls:
 *   GET    /api/v1/medical-records/patient/{ssn}     → patient procedure history
 *   GET    /api/v1/medical-records/stay/{stayId}     → procedures for a stay
 *   POST   /api/v1/medical-records/procedure         → record a procedure (ADMIN, DOCTOR)
 */
@Controller
@RequestMapping("/medical-records")
public class MedicalRecordViewController {

    @GetMapping
    public String medicalRecords() {
        return "medical-records/list";
    }
}
