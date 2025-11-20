package com.edunext.app.dtos;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.edunext.app.model.Aluno;
import com.edunext.app.model.AvaliacaoQualitativa;
import com.edunext.app.model.SimuladoAluno;

public record AlunoPerfilDTO(
    String nome,
    String login,
    String turmaNome,
    String fotoUrl,

    Map<String, Double> mediasQualitativas,
    List<AvaliacaoResponseDTO> historicoQualitativo,
    List<SimuladoAlunoResponseDTO> historicoSimulados
) {

    public AlunoPerfilDTO(Aluno aluno, List<AvaliacaoQualitativa> avaliacoes, List<SimuladoAluno> simulados){
        this(aluno.getNome(), aluno.getLogin(), aluno.getTurma() != null ? aluno.getTurma().getNome() : "Sem turma", aluno.getFotoUrl(), calcularMedias(avaliacoes), avaliacoes.stream().map(AvaliacaoResponseDTO::new).collect(Collectors.toList()), simulados.stream().map(SimuladoAlunoResponseDTO::new).collect(Collectors.toList()));
    }

    private static Map<String, Double> calcularMedias(List<AvaliacaoQualitativa> avaliacoes){
        if (avaliacoes == null || avaliacoes.isEmpty()){
            return Map.of("assiduidade", 0.0, "participacao", 0.0, "responsabilidade", 0.0, "sociabilidade", 0.0);
        }

        double ass = avaliacoes.stream().mapToDouble(AvaliacaoQualitativa::getAssiduidade).average().orElse(0.0);
        double par = avaliacoes.stream().mapToDouble(AvaliacaoQualitativa::getParticipacao).average().orElse(0.0);
        double res = avaliacoes.stream().mapToDouble(AvaliacaoQualitativa::getResponsabilidade).average().orElse(0.0);
        double soc = avaliacoes.stream().mapToDouble(AvaliacaoQualitativa::getSociabilidade).average().orElse(0.0);

        return Map.of("assiduidade", ass, "participacao", par, "responsabilidade", res, "sociabilidade", soc);
    }

}
