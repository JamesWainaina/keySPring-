package com.example.Keycloakdemo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class keycloak {

    @GetMapping("hello")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello Keycloak!");
    }

    @GetMapping("/admin")
    public ResponseEntity<String> sayHelloToAdmin(){
        return ResponseEntity.ok("Hello Admin");
    }

    public ResponseEntity<String> sayHelloToUser(){
        return ResponseEntity.ok("Hello User");
    }

}
