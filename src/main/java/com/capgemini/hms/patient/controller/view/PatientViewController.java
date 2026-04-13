package com.capgemini.hms.patient.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * View controller for Patient management pages.
 *
 * Client-side JS calls:
 *   GET    /api/v1/patients?page=&size=&sort=         → list (paginated)
 *   GET    /api/v1/patients/search?query=&page=&size= → search
 *   GET    /api/v1/patients/{ssn}                     → single patient
 *   POST   /api/v1/patients                           → register
 *   PUT    /api/v1/patients/{ssn}                     → update
 *   DELETE /api/v1/patients/{ssn}                     → soft-delete
 */
@Controller
@RequestMapping("/patient")
public class PatientViewController {

    @GetMapping
    public String listPatients() {
        return "patient/list";
    }
}
