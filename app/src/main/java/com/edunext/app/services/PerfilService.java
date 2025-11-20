package com.edunext.app.services;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edunext.app.dtos.AlunoPerfilDTO;
import com.edunext.app.model.Aluno;
import com.edunext.app.model.AvaliacaoQualitativa;
import com.edunext.app.model.SimuladoAluno;
import com.edunext.app.repository.AlunoRepository;
import com.edunext.app.repository.AvaliacaoQualitativaRepository;
import com.edunext.app.repository.SimuladoAlunoRepository;


@Service
public class PerfilService {
    private final AlunoRepository alunoRepository;
    private final AvaliacaoQualitativaRepository avaliacaoQualitativaRepository;
    private final SimuladoAlunoRepository simuladoAlunoRepository;
    
    public PerfilService(AlunoRepository alunoRepository, AvaliacaoQualitativaRepository avaliacaoQualitativaRepository, SimuladoAlunoRepository simuladoAlunoRepository){
        this.alunoRepository = alunoRepository;
        this.avaliacaoQualitativaRepository = avaliacaoQualitativaRepository;
        this.simuladoAlunoRepository = simuladoAlunoRepository;
    }

    @Transactional(readOnly = true)
    public AlunoPerfilDTO getMeuPerfil(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Aluno aluno = alunoRepository.findByMatricula(username).orElseThrow(() -> new RuntimeException("Aluno não encontrado."));

        UUID alunoId = aluno.getId();

        List<AvaliacaoQualitativa> avaliacoes = avaliacaoQualitativaRepository.findByAlunoIdOrderByDataAvaliacaoDesc(alunoId);
        List<SimuladoAluno> simulados = simuladoAlunoRepository.findByAlunoId(alunoId);

        return new AlunoPerfilDTO(aluno, avaliacoes, simulados);
        
    }

}
