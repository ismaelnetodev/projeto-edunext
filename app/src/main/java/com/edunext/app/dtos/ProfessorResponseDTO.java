package com.edunext.app.dtos;

import java.util.UUID;

import com.edunext.app.model.Professor;

public record ProfessorResponseDTO( UUID id, String nome, String login) {
    public ProfessorResponseDTO(Professor professor){
        this(professor.getId(), professor.getNome(), professor.getLogin());
    }

    
}
