package com.edunext.app.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlterarSenhaDTO(
    @NotBlank(message = "Senha antiga é obrigatória") 
    String senhaAntiga, 
    @NotBlank(message = "Nova senha é obrigatória") 
    @Size(min = 4, message = "Nova senha deve ter no mínimo 4 caracteres") 
    String novaSenha) {

}
