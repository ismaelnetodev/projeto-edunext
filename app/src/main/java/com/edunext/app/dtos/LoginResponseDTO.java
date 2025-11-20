package com.edunext.app.dtos;

public record LoginResponseDTO(String token, String role, boolean senhaTemporaria) {

}
