package com.edunext.app.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edunext.app.dtos.AvaliacaoCadastroDTO;
import com.edunext.app.exceptions.ResourceNotFoundException;
import com.edunext.app.exceptions.UnauthorizedOperationException;
import com.edunext.app.model.Aluno;
import com.edunext.app.model.AvaliacaoQualitativa;
import com.edunext.app.model.Professor;
import com.edunext.app.repository.AlunoRepository;
import com.edunext.app.repository.AvaliacaoQualitativaRepository;
import com.edunext.app.repository.ProfessorRepository;


@Service
public class AvaliacaoQualitativaService {
    private final AvaliacaoQualitativaRepository avaliacaoQualitativaRepository;
    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;

    public AvaliacaoQualitativaService(
        AvaliacaoQualitativaRepository avaliacaoQualitativaRepository,
        AlunoRepository alunoRepository,
        ProfessorRepository professorRepository
    ){
        this.avaliacaoQualitativaRepository = avaliacaoQualitativaRepository;
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
    }


    public AvaliacaoQualitativa registrarAvaliacao(AvaliacaoCadastroDTO data){
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        Professor professor = (Professor) professorRepository.findByLogin(login);

        if (professor == null) throw new UnauthorizedOperationException("Professor não autenticado");

        Aluno aluno = alunoRepository.findById(data.alunoId()).orElseThrow(() -> new RuntimeException("Aluno não encontrado."));

        AvaliacaoQualitativa novaAvaliacao = new AvaliacaoQualitativa();
        novaAvaliacao.setAluno(aluno);
        novaAvaliacao.setProfessor(professor);
        novaAvaliacao.setDataAvaliacao(LocalDateTime.now());
        novaAvaliacao.setAssiduidade(data.assiduidade());
        novaAvaliacao.setParticipacao(data.participacao());
        novaAvaliacao.setResponsabilidade(data.responsabilidade());
        novaAvaliacao.setSociabilidade(data.sociabilidade());
        novaAvaliacao.setObservacao(data.observacao());

        return avaliacaoQualitativaRepository.save(novaAvaliacao);
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoQualitativa> getHistoricoDoAluno(@NonNull UUID alunoId){
        if (!alunoRepository.existsById(alunoId)) throw new ResourceNotFoundException("Aluno", "id", alunoId);

        return avaliacaoQualitativaRepository.findByAlunoIdOrderByDataAvaliacaoDesc(alunoId);
    }

}
