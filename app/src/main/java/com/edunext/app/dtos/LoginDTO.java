package com.edunext.app.dtos;

import jakarta.validation.constraints.NotBlank;

public record LoginDTO(
    @NotBlank(message = "Login é obrigatório")
    String login, 
    @NotBlank(message = "Senha é obrigatória")
    String password
) {}
