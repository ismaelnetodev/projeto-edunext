package com.edunext.app.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edunext.app.dtos.QuestaoCadastroDTO;
import com.edunext.app.exceptions.ResourceNotFoundException;
import com.edunext.app.exceptions.UnauthorizedOperationException;
import com.edunext.app.model.Disciplina;
import com.edunext.app.model.Professor;
import com.edunext.app.model.Questao;
import com.edunext.app.repository.DisciplinaRepository;
import com.edunext.app.repository.ProfessorRepository;
import com.edunext.app.repository.QuestaoRepository;

@Service
public class QuestaoService {
    private final QuestaoRepository questaoRepository;
    private final ProfessorRepository professorRepository;
    private final DisciplinaService disciplinaService;

    public QuestaoService(QuestaoRepository questaoRepository, ProfessorRepository professorRepository, DisciplinaService disciplinaService){
        this.questaoRepository = questaoRepository;
        this.professorRepository = professorRepository;
        this.disciplinaService = disciplinaService;
    }

    @Transactional
    public Questao criarQuestao(QuestaoCadastroDTO data){
        Professor professor = getProfessorLogado();

        Disciplina disciplina = disciplinaService.buscarOuCriar(data.disciplina());

        Questao questao = new Questao();
        questao.setDisciplina(disciplina);
        questao.setAlternativas(data.alternativas());
        questao.setRespostaCorreta(data.respostaCorreta());
        questao.setEnunciado(data.enunciado());
        questao.setProfessorCriador(professor);
        questao.setTipo(data.tipo());

        return questaoRepository.save(questao);
    }

    @Transactional(readOnly = true)
    public Page<Questao> getAllQuestoes(@NonNull Pageable pageable){
        return questaoRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Questao> getAllQuestoesByDisciplina(@NonNull Long disciplinaId, Pageable pageable){
        if (!disciplinaService.existePorId(disciplinaId)){
            throw new ResourceNotFoundException("Disciplina", "id", disciplinaId);
        }
        return questaoRepository.findByDisciplinaId(disciplinaId, pageable);
    }

    @Transactional(readOnly = true)
    public Questao getQuestao(@NonNull Long id){     
        return questaoRepository.findByIdWithRelations(id).orElseThrow(() -> new ResourceNotFoundException("Questão", "id", id));
    }

    @Transactional
    public Questao updateQuestao(QuestaoCadastroDTO data, @NonNull Long id){
        Professor professor = getProfessorLogado();

        Questao questao = getQuestao(id);

        if (!questao.getProfessorCriador().getId().equals(professor.getId())){
            throw new UnauthorizedOperationException("Você só pode editar suas próprias questões.");
        }

        Disciplina disciplina = disciplinaService.buscarOuCriar(data.disciplina());

        questao.setDisciplina(disciplina);
        questao.setAlternativas(data.alternativas());
        questao.setRespostaCorreta(data.respostaCorreta());
        questao.setEnunciado(data.enunciado());
        questao.setTipo(data.tipo());

        return questaoRepository.save(questao);
    }

    @Transactional
    public void deleteQuestao(@NonNull Long id){
        Professor professor = getProfessorLogado();

        Questao questao = getQuestao(id);

        if (!questao.getProfessorCriador().getId().equals(professor.getId())) {
            throw new UnauthorizedOperationException("Você só pode deletar suas próprias questões.");
        }

        questaoRepository.deleteById(id);
    }

    @Transactional
    public Questao clonarQuestao(@NonNull Long idQuestaoOriginal){
        Professor professorClonando = getProfessorLogado();

        Questao questaoOriginal = getQuestao(idQuestaoOriginal);

        Questao novaQuestao = new Questao();

        novaQuestao.setEnunciado(questaoOriginal.getEnunciado());
        novaQuestao.setDisciplina(questaoOriginal.getDisciplina());
        novaQuestao.setAlternativas(questaoOriginal.getAlternativas());
        novaQuestao.setRespostaCorreta(questaoOriginal.getRespostaCorreta());
        novaQuestao.setTipo(questaoOriginal.getTipo());

        novaQuestao.setProfessorCriador(professorClonando);
        return questaoRepository.save(novaQuestao);
    }

    private Professor getProfessorLogado(){
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        Professor professor = (Professor) professorRepository.findByLogin(login);

        if (professor == null){
            throw new UnauthorizedOperationException("Professor não autenticado");
        }

        return professor;
    }

}
