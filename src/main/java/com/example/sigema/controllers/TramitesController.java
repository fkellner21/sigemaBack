package com.example.sigema.controllers;

import com.example.sigema.services.ITramitesService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tramites")
@CrossOrigin(origins = "*")
public class TramitesController {

    private final ITramitesService tramitesService;

    public TramitesController(ITramitesService tramitesService){
        this.tramitesService = tramitesService;
    }
}