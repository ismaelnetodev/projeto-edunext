package com.edunext.app.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edunext.app.dtos.QuestaoResumoDTO;
import com.edunext.app.dtos.RespostaSimuladoDTO;
import com.edunext.app.dtos.SimuladoAlunoResponseDTO;
import com.edunext.app.dtos.SimuladoCriarDTO;
import com.edunext.app.dtos.SimuladoGerarDTO;
import com.edunext.app.dtos.SimuladoParaFazerDTO;
import com.edunext.app.dtos.SimuladoResponseDTO;
import com.edunext.app.dtos.SimuladoResumoDTO;
import com.edunext.app.dtos.SimuladoSubmeterDTO;
import com.edunext.app.exceptions.InsufficientQuestionsException;
import com.edunext.app.exceptions.InvalidDataException;
import com.edunext.app.exceptions.ResourceNotFoundException;
import com.edunext.app.exceptions.SimuladoNotAvailableException;
import com.edunext.app.exceptions.UnauthorizedOperationException;
import com.edunext.app.model.Aluno;
import com.edunext.app.model.Disciplina;
import com.edunext.app.model.Professor;
import com.edunext.app.model.Questao;
import com.edunext.app.model.Simulado;
import com.edunext.app.model.SimuladoAluno;
import com.edunext.app.model.Turma;
import com.edunext.app.repository.AlunoRepository;
import com.edunext.app.repository.DisciplinaRepository;
import com.edunext.app.repository.ProfessorRepository;
import com.edunext.app.repository.QuestaoRepository;
import com.edunext.app.repository.SimuladoAlunoRepository;
import com.edunext.app.repository.SimuladoRepository;
import com.edunext.app.repository.TurmaRepository;

@Service
public class SimuladoService {
    private final SimuladoRepository simuladoRepository;
    private final SimuladoAlunoRepository simuladoAlunoRepository;
    private final QuestaoRepository questaoRepository;
    private final ProfessorRepository professorRepository;
    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;
    private final DisciplinaRepository disciplinaRepository;

    public SimuladoService(SimuladoRepository simuladoRepository,
                           SimuladoAlunoRepository simuladoAlunoRepository,
                           QuestaoRepository questaoRepository,
                           ProfessorRepository professorRepository,
                           AlunoRepository alunoRepository,
                           TurmaRepository turmaRepository,
                           DisciplinaRepository disciplinaRepository) {
        this.simuladoRepository = simuladoRepository;
        this.simuladoAlunoRepository = simuladoAlunoRepository;
        this.questaoRepository = questaoRepository;
        this.professorRepository = professorRepository;
        this.alunoRepository = alunoRepository;
        this.turmaRepository = turmaRepository;
        this.disciplinaRepository = disciplinaRepository;
    }

    @Transactional
    public SimuladoResponseDTO gerarSimuladoAleatorio(SimuladoGerarDTO data){
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        Professor professor = (Professor) professorRepository.findByLogin(login);
        if (professor == null) throw new UnauthorizedOperationException("Professor não autenticado");

        Turma turma = turmaRepository.findById(data.turmaId())
            .orElseThrow(() -> new ResourceNotFoundException("Turma", "id", data.turmaId()));

        Disciplina disciplina = disciplinaRepository.findById(data.disciplinaId())
        .orElseThrow(() -> new ResourceNotFoundException("Disciplina", "id", data.disciplinaId()));

        List<Questao> bancoDeQuestoes = questaoRepository
            .findByDisciplinaAndProfessorCriador(disciplina, professor);

        if (bancoDeQuestoes.size() < data.numeroQuestoes()){
            throw new InsufficientQuestionsException(data.numeroQuestoes(), bancoDeQuestoes.size());
        }

        Collections.shuffle(bancoDeQuestoes);
        List<Questao> questoesSelecionadas = bancoDeQuestoes.subList(0, data.numeroQuestoes());

        Simulado novoSimulado = new Simulado();
        novoSimulado.setTitulo(data.titulo());
        novoSimulado.setDataCriacao(LocalDateTime.now());
        novoSimulado.setProfessor(professor);
        novoSimulado.setTurma(turma);
        novoSimulado.setQuestoes(questoesSelecionadas);
        novoSimulado.setDisciplina(questoesSelecionadas.get(0).getDisciplina());
        novoSimulado.setDataInicioDisponivel(data.inicioDisponivel());
        novoSimulado.setDataFimDisponivel(data.fimDisponivel());

        Simulado simuladoSalvo = simuladoRepository.save(novoSimulado);

        return new SimuladoResponseDTO(simuladoSalvo);

    }

    @Transactional(readOnly = true)
    public List<SimuladoResponseDTO> getSimuladoParaAluno(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Aluno aluno = alunoRepository.findByMatricula(username).orElseThrow(() -> new ResourceNotFoundException("Aluno", "login", username));

        List<Simulado> simulados = simuladoRepository.findByTurmaId(aluno.getTurma().getId());

        LocalDateTime agora = LocalDateTime.now();

        List<Simulado> simuladosDisponiveis = simulados.stream()
            .filter(s -> 
                s.getDataInicioDisponivel() != null && 
                s.getDataFimDisponivel() != null &&
                s.getDataInicioDisponivel().isBefore(agora) && 
                s.getDataFimDisponivel().isAfter(agora)
            )
            .collect(Collectors.toList());

        return simuladosDisponiveis.stream()
                .map(SimuladoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SimuladoParaFazerDTO getSimuladoParaFazer(@NonNull Long simuladoId) {
        Simulado simulado = simuladoRepository.findById(simuladoId)
            .orElseThrow(() -> new ResourceNotFoundException("Simulado", "id", simuladoId));
        
        // (Lógica de segurança futura: verificar se o aluno é da turma)
        
        return new SimuladoParaFazerDTO(simulado);
    }

    @Transactional
    public SimuladoAlunoResponseDTO submeterSimulado(@NonNull Long simuladoId, SimuladoSubmeterDTO data){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Aluno aluno = alunoRepository.findByMatricula(username).orElseThrow(() -> new ResourceNotFoundException("Aluno", "login", username));

        Simulado simulado = simuladoRepository.findById(simuladoId).orElseThrow(() -> new ResourceNotFoundException("Simulado", "id", simuladoId));

        LocalDateTime agora = LocalDateTime.now();

        if (agora.isBefore(simulado.getDataInicioDisponivel())){
            throw new SimuladoNotAvailableException("O simulado ainda não está disponível");
        }

        if (agora.isAfter(simulado.getDataFimDisponivel())){
            throw new SimuladoNotAvailableException("O prazo para este simulado expirou");
        }

        boolean jaSubmeteu = simuladoAlunoRepository.existsByAlunoIdAndSimuladoId(aluno.getId(), simuladoId);

        if (jaSubmeteu){
            throw new SimuladoNotAvailableException("Este simulado já foi submetido por este aluno.");
        }

        if (!simulado.getTurma().getId().equals(aluno.getTurma().getId())) {
            throw new SimuladoNotAvailableException("Acesso Negado: Este simulado não pertence à sua turma.");
        }

        double notaFinal = 0.0;

        Map<Long, String> repostasMap = data.respostas().stream()
                .collect(Collectors.toMap(RespostaSimuladoDTO::questaoId, RespostaSimuladoDTO::respostaDada));

        for (Questao questao : simulado.getQuestoes()){
            String respostaAluno = repostasMap.get(questao.getId());
            
            if (respostaAluno != null && respostaAluno.equals(questao.getRespostaCorreta())){
                notaFinal += questao.getPontuacao();
            }
        }

        SimuladoAluno simuladoAluno = new SimuladoAluno();
        simuladoAluno.setAluno(aluno);
        simuladoAluno.setDataSubmissao(LocalDateTime.now());
        simuladoAluno.setNotaFinal(notaFinal);
        simuladoAluno.setRespostas(repostasMap);
        simuladoAluno.setSimulado(simulado);

        SimuladoAluno resultadoSalvo = simuladoAlunoRepository.save(simuladoAluno);

        return new SimuladoAlunoResponseDTO(resultadoSalvo);
    }

    @Transactional(readOnly = true)
    public List<SimuladoResponseDTO> getSimuladosDoProfessorLogado(){
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        Professor professor = (Professor) professorRepository.findByLogin(login);

        if (professor == null) throw new UnauthorizedOperationException("Professor não autenticado");

        List<Simulado> simulados = simuladoRepository.findByProfessorId(professor.getId());    
        return simulados.stream()
                .map(SimuladoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<SimuladoAlunoResponseDTO> getResultadosDoSimulado(@NonNull Long simuladoId, Pageable pageable){
        if (!simuladoRepository.existsById(simuladoId)) throw new ResourceNotFoundException("Simulado", "id", simuladoId);

        Page<SimuladoAluno> resultados = simuladoAlunoRepository.findBySimuladoId(simuladoId, pageable);

        return resultados.map(SimuladoAlunoResponseDTO::new);
    }

    @Transactional(readOnly = true)
    public SimuladoResumoDTO getResumoDoSimulado(@NonNull Long simuladoId){
        Simulado simulado = simuladoRepository.findById(simuladoId).orElseThrow(() -> new ResourceNotFoundException("Simulado", "id", simuladoId));

        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        Professor professor = (Professor) professorRepository.findByLogin(login);

        if (professor == null) throw new UnauthorizedOperationException("Professor não autenticado");
        if (!simulado.getProfessor().getId().equals(professor.getId())){
            throw new UnauthorizedOperationException("Apenas o professor criador do simulado pode ver o resumo.");
        }

        List<SimuladoAluno> resultados = simuladoAlunoRepository.findBySimuladoId(simuladoId);

        int totalAlunos = 0;
        if (simulado.getTurma() != null && simulado.getTurma().getAlunos() != null){
            totalAlunos = simulado.getTurma().getAlunos().size();
        }

        int alunosQueResponderam = resultados.size();

        double mediaNotas = 0.0;
        double maiorNota = 0.0;
        double menorNota = 0.0;

        if(!resultados.isEmpty()){
            mediaNotas = resultados.stream().mapToDouble(SimuladoAluno::getNotaFinal).average().orElse(0.0);
            maiorNota = resultados.stream().mapToDouble(SimuladoAluno::getNotaFinal).max().orElse(0.0);
            menorNota = resultados.stream().mapToDouble(SimuladoAluno::getNotaFinal).min().orElse(0.0);
        }

        double taxaParticipacaoPercent = totalAlunos == 0 ? 0.0 : (alunosQueResponderam * 100.0 / totalAlunos);

        Map<Long, Questao> questoesPorId = simulado.getQuestoes().stream()
            .collect(Collectors.toMap(q -> q.getId(), q -> q));

        Map<Long, Integer> totalRespondentesPorQuestao = new HashMap<>();
        Map<Long, Integer> totalAcertosPorQuestao = new HashMap<>();

        for (SimuladoAluno sa : resultados){
            Map<Long, String> respostas = sa.getRespostas();
            if (respostas == null) continue;
            for (Map.Entry<Long, String> entry : respostas.entrySet()){
                Long questaoId = entry.getKey();
                String respostaDada = entry.getValue();

                totalRespondentesPorQuestao.put(questaoId, totalRespondentesPorQuestao.getOrDefault(questaoId, 0) + 1);

                Questao q = questoesPorId.get(questaoId);
                if (q != null && respostaDada != null){
                    String correta = q.getRespostaCorreta();
                    if (correta != null && respostaDada.trim().equalsIgnoreCase(correta.trim())){
                        totalAcertosPorQuestao.put(questaoId, totalAcertosPorQuestao.getOrDefault(questaoId, 0) + 1);
                    }
                }
            }
        }

        List<QuestaoResumoDTO> questaoResumoList = new ArrayList<>();
        for (Questao q : simulado.getQuestoes()) {
            Long qid = q.getId();
            int responded = totalRespondentesPorQuestao.getOrDefault(qid, 0);
            int correct = totalAcertosPorQuestao.getOrDefault(qid, 0);
            double taxa = responded == 0 ? 0.0 : (correct * 100.0 / responded);

            String enunciado = q.getEnunciado();
            String enunciadoResumo = enunciado == null ? "" :
                (enunciado.length() > 120 ? enunciado.substring(0, 120) + "..." : enunciado);

            questaoResumoList.add(new QuestaoResumoDTO(qid, enunciadoResumo, responded, correct, taxa));
        }

        return new SimuladoResumoDTO(
            simulado.getId(),
            simulado.getTitulo(),
            totalAlunos,
            alunosQueResponderam,
            round(mediaNotas),
            round(maiorNota),
            round(menorNota),
            round(taxaParticipacaoPercent),
            questaoResumoList
        );

    }

    @Transactional
    public SimuladoResponseDTO criarSimuladoComQuestoes(SimuladoCriarDTO data){
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        Professor professor = (Professor) professorRepository.findByLogin(login);

        if (professor == null) throw new UnauthorizedOperationException("Professor não autenticado.");

        Turma turma = turmaRepository.findById(data.turmaId()).orElseThrow(() -> new ResourceNotFoundException("Turma", "id", data.turmaId()));

        Disciplina disciplina = disciplinaRepository.findById(data.disciplinaId()).orElseThrow(() -> new ResourceNotFoundException("Disciplina", "id", data.disciplinaId()));

        if (data.questoesId() == null || data.questoesId().isEmpty()){
            throw new InvalidDataException("Nenhuma questão selecionada");
        }

        List<Questao> questoesSelecionadas = questaoRepository.findAllById(data.questoesId());

        Simulado novoSimulado = new Simulado();
        novoSimulado.setTitulo(data.titulo());
        novoSimulado.setDataCriacao(LocalDateTime.now());
        novoSimulado.setProfessor(professor);
        novoSimulado.setTurma(turma);
        novoSimulado.setQuestoes(questoesSelecionadas);
        novoSimulado.setDisciplina(disciplina);
        novoSimulado.setDataInicioDisponivel(data.inicioDisponivel());
        novoSimulado.setDataFimDisponivel(data.fimDisponivel());

        Simulado salvo = simuladoRepository.save(novoSimulado);

        return new SimuladoResponseDTO(salvo);
        
    }

    @Transactional(readOnly = true)
    public Page<SimuladoAlunoResponseDTO> getAlunosDoSimulado(@NonNull Long simuladoId, Pageable pageable){
        Simulado simulado = simuladoRepository.findById(simuladoId).orElseThrow(() -> new ResourceNotFoundException("Simulado não encontrado."));

        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        Professor professor = (Professor) professorRepository.findByLogin(login);

        if(professor == null) throw new UnauthorizedOperationException("Professor não autenticado");
        if (!simulado.getProfessor().getId().equals(professor.getId())){
            throw new UnauthorizedOperationException("Apenas o professor criador pode ver as submissões.");
        }

        Page<SimuladoAluno> page = simuladoAlunoRepository.findBySimuladoId(simuladoId, pageable);
        return page.map(SimuladoAlunoResponseDTO::new);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
