package com.capgemini.hms.room.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * View controller for Block management pages.
 *
 * Client-side JS calls:
 *   GET    /api/v1/blocks                          → list all 
 *   POST   /api/v1/blocks                          → create block (ADMIN)
 *   DELETE /api/v1/blocks?floor=&code=             → delete block (ADMIN)
 */
@Controller
@RequestMapping("/block")
public class BlockViewController {

    @GetMapping
    public String listBlocks() {
        return "block/list";
    }
}
