package com.project.back_end.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.back_end.model.Admin;
import com.project.back_end.service.Service;

@RestController
@RequestMapping("${api.path}" + "admin")
public class AdminController {

    private final Service service;

    public AdminController(Service service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> adminLogin(
            @RequestBody Admin admin) {

        return service.validateAdmin(admin);
    }
}
