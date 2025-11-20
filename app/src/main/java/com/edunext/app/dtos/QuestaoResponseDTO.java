package com.edunext.app.dtos;

import java.util.List;

import com.edunext.app.enums.TipoQuestao;
import com.edunext.app.model.Questao;

public record QuestaoResponseDTO(
    Long id,
    String enunciado,
    String disciplina,
    TipoQuestao tipo,
    double pontuacao,
    List<String> alternativas,
    String respostaCorreta,
    String nomeProfessor
) {

    public QuestaoResponseDTO(Questao questao){
        this(
            questao.getId(),
            questao.getEnunciado(),
            questao.getDisciplina() != null ? questao.getDisciplina().getNome() : null,
            questao.getTipo(),
            questao.getPontuacao(),
            questao.getAlternativas(),
            questao.getRespostaCorreta(),
            questao.getProfessorCriador().getNome()
        );
    }

}
