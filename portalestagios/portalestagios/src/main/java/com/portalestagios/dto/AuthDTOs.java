package com.portalestagios.dto;

public class AuthDTOs {
  // signup
  public record SignupRequest(String nome, String email, String senha) {}

  // login
  public record LoginRequest(String email, String senha) {}

  // resposta
  public record TokenResponse(String accessToken, String tokenType) {}
}