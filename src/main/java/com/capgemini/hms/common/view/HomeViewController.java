package com.capgemini.hms.common.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * View controller for the Admin Dashboard page.
 * Data is loaded client-side via fetch → GET /api/v1/dashboard/summary
 *                                      → GET /api/v1/dashboard/departments
 */
@Controller
public class HomeViewController {

    @GetMapping("/home")
    public String dashboard() {
        return "dashboard/index";
    }
}
