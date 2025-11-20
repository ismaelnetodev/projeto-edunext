package com.edunext.app.dtos;

import java.util.List;

import com.edunext.app.enums.TipoQuestao;
import com.edunext.app.model.Questao;

public record QuestaoAlunoDTO(
    Long id,
    String enunciado,
    String imagemUrl,
    String disciplina,
    TipoQuestao tipo,
    double pontuacao,
    List<String> alternativas
) {

    public QuestaoAlunoDTO(Questao questao){
        this(questao.getId(), questao.getEnunciado(), questao.getImagemUrl(), questao.getDisciplina().getNome(), questao.getTipo(), questao.getPontuacao(), questao.getAlternativas());
    }

}
