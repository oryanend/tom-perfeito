package com.oryanend.tom_perfeito_api.controllers;

import com.oryanend.tom_perfeito_api.services.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/status")
public class StatusController {
    @Autowired
    private StatusService service;

    @GetMapping
    public Map<String, Object> getStatus() {
        return service.getStatus();
    }
}
