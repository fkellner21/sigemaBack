package com.example.sigema.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@CrossOrigin(origins = "*")
public class HealthController {

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @GetMapping("/")
    public String home() {
        return "OK";
    }
}

