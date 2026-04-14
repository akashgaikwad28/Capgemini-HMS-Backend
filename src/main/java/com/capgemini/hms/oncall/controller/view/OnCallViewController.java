package com.capgemini.hms.oncall.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * View controller for On-Call Shift management pages.
 *
 * Client-side JS calls:
 *   POST   /api/v1/shifts                          → assign shift (ADMIN, NURSE)
 *   GET    /api/v1/shifts/nurse/{id}               → all shifts for a nurse
 *   GET    /api/v1/shifts/block/{floor}/{code}     → coverage for a block/floor
 */
@Controller
@RequestMapping("/shift")
public class OnCallViewController {

    @GetMapping
    public String onCallShifts() {
        return "oncall/list";
    }
}
