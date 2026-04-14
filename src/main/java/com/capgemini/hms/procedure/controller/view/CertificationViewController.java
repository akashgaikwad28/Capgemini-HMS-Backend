package com.capgemini.hms.procedure.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * View controller for Physician Certification management pages.
 *
 * Client-side JS calls:
 *   POST   /api/v1/certifications                        → certify physician (ADMIN)
 *   GET    /api/v1/certifications/physician/{id}         → physician's certifications
 */
@Controller
@RequestMapping("/certification")
public class CertificationViewController {

    @GetMapping
    public String certifications() {
        return "certification/list";
    }
}
