package com.portalestagios.controller;

import com.portalestagios.dto.AuthDTOs.*;
import com.portalestagios.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "auth-controller")
public class AuthController {

  private final AuthService service;

  public AuthController(AuthService service) {
    this.service = service;
  }

  @PostMapping("/signup")
  @Operation(summary = "Criar usuário")
  public ResponseEntity<Void> signup(@RequestBody SignupRequest body) {
    service.signup(body);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/login")
  @Operation(summary = "Login e geração do JWT")
  public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest body) {
    return ResponseEntity.ok(service.login(body));
  }
}
