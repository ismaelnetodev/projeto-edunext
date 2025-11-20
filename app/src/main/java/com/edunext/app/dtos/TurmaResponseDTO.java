package com.edunext.app.dtos;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.edunext.app.model.Turma;

public record TurmaResponseDTO(
    Long id,
    String nome,
    int anoLetivo,
    List<String> nomesProfessores,
    int numeroDeAlunos
) {
    public TurmaResponseDTO(Turma turma){
        this (
            turma.getId(),
            turma.getNome(),
            turma.getAnoLetivo(),

            turma.getProfessores() != null ?
                turma.getProfessores().stream()
                    .map(professor -> professor.getNome())
                    .collect(Collectors.toList()) :
                new ArrayList<>(),
            
            turma.getAlunos() != null ?
                turma.getAlunos().size() :
                0
        );
    }
}
