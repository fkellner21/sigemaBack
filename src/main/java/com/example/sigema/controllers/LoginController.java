package com.example.sigema.controllers;

import com.example.sigema.models.CustomUserDetails;
import com.example.sigema.models.JWTResponse;
import com.example.sigema.models.LoginDTO;
import com.example.sigema.utilidades.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public LoginController(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername().replaceAll("[./-]", ""), request.getPassword()));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String token = jwtUtils.generateToken(userDetails);

        return ResponseEntity.ok(new JWTResponse(token,"ROLE_" + userDetails.getRol().name(), userDetails.getIdUnidad(), userDetails.getIdUsuario()));
    }
}
