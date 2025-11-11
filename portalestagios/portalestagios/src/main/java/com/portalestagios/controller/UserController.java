// src/main/java/com/portalestagios/controller/UserController.java
package com.portalestagios.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

  @GetMapping("/me")
  public Map<String, Object> me(Authentication auth) {
    return Map.of(
      "username", auth.getName(),
      "roles", auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())
    );
  }
}