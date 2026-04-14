package com.capgemini.hms.procedure.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * View controller for Procedure catalog management pages.
 *
 * Client-side JS calls:
 *   GET    /api/v1/procedures?page=&size=&sort=           → list all (paginated)
 *   GET    /api/v1/procedures/{code}                      → single procedure
 *   GET    /api/v1/procedures/search?query=&page=&size=   → search by name
 *   POST   /api/v1/procedures                             → add procedure (ADMIN)
 *   PUT    /api/v1/procedures/{code}                      → update procedure (ADMIN)
 *   DELETE /api/v1/procedures/{code}                      → soft-delete (ADMIN)
 */
@Controller
@RequestMapping("/procedure")
public class ProcedureViewController {

    @GetMapping
    public String listProcedures() {
        return "procedure/list";
    }
}
