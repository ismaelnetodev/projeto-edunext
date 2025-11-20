package com.edunext.app.dtos;

import java.time.LocalDateTime;

import com.edunext.app.model.AvaliacaoQualitativa;

public record AvaliacaoResponseDTO(
    Long id,
    LocalDateTime dataAvaliacao,
    String professorNome,
    String alunoNome,
    double assiduidade,
    double participacao,
    double responsabilidade,
    double sociabilidade,
    String observacao
) {
    public AvaliacaoResponseDTO(AvaliacaoQualitativa avaliacao) {
        this(
            avaliacao.getId(),
            avaliacao.getDataAvaliacao(),
            avaliacao.getProfessor().getNome(), 
            avaliacao.getAluno().getNome(),
            avaliacao.getAssiduidade(),
            avaliacao.getParticipacao(),
            avaliacao.getResponsabilidade(),
            avaliacao.getSociabilidade(),
            avaliacao.getObservacao()
        );
    }
}
