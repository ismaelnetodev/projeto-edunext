package com.edunext.app.dtos;

import java.util.UUID;

import com.edunext.app.model.Aluno;

public record AlunoResponseDTO(UUID id, String nome, String login, String matricula, String turmaNome, String fotoUrl) {
    public AlunoResponseDTO(Aluno aluno){
        this(aluno.getId(), aluno.getNome(), aluno.getLogin(), aluno.getMatricula(), aluno.getTurma() != null ? aluno.getTurma().getNome() : "Sem turma", aluno.getFotoUrl());
    }
}
