package com.capgemini.hms.nurse.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * View controller for Nurse management pages.
 *
 * Client-side JS calls:
 *   GET    /api/v1/nurses?page=&size=&sort=   → list (paginated)
 *   GET    /api/v1/nurses/{id}                → single nurse
 *   POST   /api/v1/nurses                     → register new nurse (ADMIN)
 *   PUT    /api/v1/nurses/{id}                → update nurse (ADMIN)
 *   DELETE /api/v1/nurses/{id}                → soft-delete (ADMIN)
 */
@Controller
@RequestMapping("/nurse")
public class NurseViewController {

    @GetMapping
    public String listNurses() {
        return "nurse/list";
    }
}
