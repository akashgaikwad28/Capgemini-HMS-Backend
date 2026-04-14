package com.capgemini.hms.medication.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * View controller for Medication (Formulary) management.
 *
 * Client-side JS calls:
 *   GET    /api/v1/medications?page=&size=&sort=           → list all (paginated)
 *   GET    /api/v1/medications/{code}                      → single medication
 *   GET    /api/v1/medications/search?query=&page=&size=   → search by name/brand
 *   POST   /api/v1/medications                             → add medication (ADMIN)
 *   PUT    /api/v1/medications/{code}                      → update medication (ADMIN)
 *   DELETE /api/v1/medications/{code}                      → soft-delete (ADMIN)
 */
@Controller
@RequestMapping("/medication")
public class MedicationViewController {

    @GetMapping
    public String listMedications() {
        return "medication/list";
    }
}
