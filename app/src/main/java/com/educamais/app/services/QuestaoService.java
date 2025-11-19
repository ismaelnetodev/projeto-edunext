package com.educamais.app.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.educamais.app.dtos.QuestaoCadastroDTO;
import com.educamais.app.exceptions.BusinessException;
import com.educamais.app.exceptions.ResourceNotFoundException;
import com.educamais.app.exceptions.UnauthorizedOperationException;
import com.educamais.app.model.Disciplina;
import com.educamais.app.model.Professor;
import com.educamais.app.model.Questao;
import com.educamais.app.repository.DisciplinaRepository;
import com.educamais.app.repository.ProfessorRepository;
import com.educamais.app.repository.QuestaoRepository;

@Service
public class QuestaoService {
    private final QuestaoRepository questaoRepository;
    private final ProfessorRepository professorRepository;
    private final DisciplinaRepository disciplinaRepository;

    public QuestaoService(QuestaoRepository questaoRepository, ProfessorRepository professorRepository, DisciplinaRepository disciplinaRepository){
        this.questaoRepository = questaoRepository;
        this.professorRepository = professorRepository;
        this.disciplinaRepository = disciplinaRepository;
    }

    @Transactional
    public Questao criarQuestao(QuestaoCadastroDTO data){
        Professor professor = getProfessorLogado();
        Disciplina disciplina = resolveDisciplina(data.disciplina());

        Questao questao = new Questao();
        questao.setDisciplina(disciplina);
        questao.setAlternativas(data.alternativas());
        questao.setRespostaCorreta(data.respostaCorreta());
        questao.setEnunciado(data.enunciado());
        questao.setProfessorCriador(professor);
        questao.setTipo(data.tipo());

        return this.questaoRepository.save(questao);
    }

    @Transactional(readOnly = true)
    public Page<Questao> getAllQuestoes(Pageable pageable){
        return questaoRepository.findAll(pageable);
    }

    @Transactional
    public Page<Questao> getAllQuestoesByDisciplina(Long disciplinaId, Pageable pageable){
        boolean exists = disciplinaRepository.existsById(disciplinaId);

        if (!exists){
            throw new ResourceNotFoundException("Disciplina", "id", disciplinaId);
        }

        return questaoRepository.findByDisciplinaId(disciplinaId, pageable);
    }

    @Transactional(readOnly = true)
    public Questao getQuestao(Long id){     
        return questaoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Questão", "id", id));
    }

    @Transactional
    public Questao updateQuestao(QuestaoCadastroDTO data, Long id){
        Professor professor = getProfessorLogado();

        Questao questao = questaoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Questão", "id", id));

        if (!questao.getProfessorCriador().getId().equals(professor.getId())){
            throw new UnauthorizedOperationException("Você só pode editar suas próprias questões.");
        }

        Disciplina disciplina = resolveDisciplina(data.disciplina());

        questao.setDisciplina(disciplina);
        questao.setAlternativas(data.alternativas());
        questao.setRespostaCorreta(data.respostaCorreta());
        questao.setEnunciado(data.enunciado());
        questao.setTipo(data.tipo());

        return this.questaoRepository.save(questao);
    }

    @Transactional
    public boolean deleteQuestao(Long id){
        Professor professor = getProfessorLogado();

        Questao questao = questaoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Questão", "id", id));

        if (!questao.getProfessorCriador().getId().equals(professor.getId())) {
            throw new UnauthorizedOperationException("Você só pode deletar suas próprias questões.");
        }

        questaoRepository.deleteById(id);
        return true;
    }

    @Transactional
    public Questao clonarQuestao(Long idQuestaoOriginal){
        Professor professorClonando = getProfessorLogado();

        Questao questaoOriginal = questaoRepository.findById(idQuestaoOriginal).orElseThrow(() -> new ResourceNotFoundException("Questão", "id", idQuestaoOriginal));

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

    private Disciplina resolveDisciplina(String nomeDisciplina){
        if (nomeDisciplina == null || nomeDisciplina.trim().isEmpty()){
            throw new RuntimeException("Nome da disciplina é obrigatório.");
        }

        return disciplinaRepository.findByNomeIgnoreCase(nomeDisciplina.trim())
            .orElseGet(() -> {
                Disciplina nova = new Disciplina();
                nova.setNome(Character.toUpperCase(nomeDisciplina.trim().charAt(0)) + nomeDisciplina.trim().substring(1).toLowerCase());
                return disciplinaRepository.save(nova);
            });
    }
}
