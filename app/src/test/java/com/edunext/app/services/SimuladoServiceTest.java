package com.edunext.app.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.edunext.app.dtos.RespostaSimuladoDTO;
import com.edunext.app.dtos.SimuladoAlunoResponseDTO;
import com.edunext.app.dtos.SimuladoGerarDTO;
import com.edunext.app.dtos.SimuladoSubmeterDTO;
import com.edunext.app.exceptions.InsufficientQuestionsException;
import com.edunext.app.exceptions.SimuladoNotAvailableException;
import com.edunext.app.model.*;
import com.edunext.app.repository.*;

@ExtendWith(MockitoExtension.class)
class SimuladoServiceTest {

    @Mock private SimuladoRepository simuladoRepository;
    @Mock private SimuladoAlunoRepository simuladoAlunoRepository;
    @Mock private QuestaoRepository questaoRepository;
    @Mock private ProfessorRepository professorRepository;
    @Mock private AlunoRepository alunoRepository;
    @Mock private TurmaRepository turmaRepository;
    @Mock private DisciplinaRepository disciplinaRepository;

    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private SimuladoService simuladoService;

    private Professor professor;
    private Aluno aluno;
    private Turma turma;
    private Disciplina disciplina;
    private Simulado simulado;

    @BeforeEach
    void setup() {
        SecurityContextHolder.setContext(securityContext);
        
        turma = new Turma("3 Ano", 2025);
        turma.setId(1L);

        professor = new Professor();
        professor.setId(UUID.randomUUID());
        professor.setLogin("prof.teste");

        aluno = new Aluno("2024001", turma);
        aluno.setId(UUID.randomUUID());
        aluno.setLogin("aluno.teste");

        disciplina = new Disciplina();
        disciplina.setId(10L);
        disciplina.setNome("Matemática");
        
        simulado = new Simulado();
        simulado.setId(100L);
        simulado.setTurma(turma);
        simulado.setDataInicioDisponivel(LocalDateTime.now().minusHours(1));
        simulado.setDataFimDisponivel(LocalDateTime.now().plusHours(1));   
        simulado.setQuestoes(new ArrayList<>());
    }

    @Test
    @DisplayName("Deve gerar simulado com sucesso quando houver questões suficientes")
    void deveGerarSimuladoComSucesso() {
        SimuladoGerarDTO dto = new SimuladoGerarDTO(
            "Prova 1", turma.getId(), disciplina.getId(), 2, null, null
        );

        Questao q1 = new Questao(); q1.setProfessorCriador(professor); q1.setDisciplina(disciplina);
        Questao q2 = new Questao(); q2.setProfessorCriador(professor); q2.setDisciplina(disciplina);
        Questao q3 = new Questao(); q3.setProfessorCriador(professor); q3.setDisciplina(disciplina);

        List<Questao> bancoQuestoes = new ArrayList<>(List.of(q1, q2, q3));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("prof.teste");
        when(professorRepository.findByLogin("prof.teste")).thenReturn(professor);
        when(turmaRepository.findById(turma.getId())).thenReturn(Optional.of(turma));
        when(disciplinaRepository.findById(disciplina.getId())).thenReturn(Optional.of(disciplina));
        when(questaoRepository.findByDisciplinaAndProfessorCriador(disciplina, professor)).thenReturn(bancoQuestoes);
        
        when(simuladoRepository.save(any(Simulado.class))).thenAnswer(i -> i.getArgument(0));

        var resultado = simuladoService.gerarSimuladoAleatorio(dto);

        assertNotNull(resultado);
        assertEquals("Prova 1", resultado.titulo());
        assertEquals(2, resultado.questoes().size()); 
        verify(simuladoRepository).save(any(Simulado.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se não houver questões suficientes")
    void deveFalharSeQuestoesInsuficientes() {
        SimuladoGerarDTO dto = new SimuladoGerarDTO(
            "Prova 1", turma.getId(), disciplina.getId(), 5, null, null
        );

        List<Questao> bancoQuestoes = new ArrayList<>(List.of(new Questao()));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("prof.teste");
        when(professorRepository.findByLogin("prof.teste")).thenReturn(professor);
        when(turmaRepository.findById(any())).thenReturn(Optional.of(turma));
        when(disciplinaRepository.findById(any())).thenReturn(Optional.of(disciplina));
        when(questaoRepository.findByDisciplinaAndProfessorCriador(any(), any())).thenReturn(bancoQuestoes);

        assertThrows(InsufficientQuestionsException.class, () -> {
            simuladoService.gerarSimuladoAleatorio(dto);
        });
    }

    @Test
    @DisplayName("Deve submeter simulado e calcular nota corretamente")
    void deveSubmeterSimuladoComSucesso() {
        Questao q1 = new Questao();
        q1.setId(10L);
        q1.setRespostaCorreta("A");
        q1.setPontuacao(2.0);

        Questao q2 = new Questao();
        q2.setId(20L);
        q2.setRespostaCorreta("B");
        q2.setPontuacao(2.0);

        simulado.setQuestoes(List.of(q1, q2));

        List<RespostaSimuladoDTO> respostas = List.of(
            new RespostaSimuladoDTO(10L, "A"),
            new RespostaSimuladoDTO(20L, "C")
        );
        SimuladoSubmeterDTO submeterDTO = new SimuladoSubmeterDTO(respostas);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("aluno.teste");
        when(alunoRepository.findByMatricula("aluno.teste")).thenReturn(Optional.of(aluno));
        when(simuladoRepository.findById(simulado.getId())).thenReturn(Optional.of(simulado));
        when(simuladoAlunoRepository.existsByAlunoIdAndSimuladoId(aluno.getId(), simulado.getId())).thenReturn(false);
        
        when(simuladoAlunoRepository.save(any(SimuladoAluno.class))).thenAnswer(invocation -> {
            SimuladoAluno salvo = invocation.getArgument(0);
            salvo.setId(99L);
            return salvo;
        });

        SimuladoAlunoResponseDTO resultado = simuladoService.submeterSimulado(simulado.getId(), submeterDTO);

        assertNotNull(resultado);
        assertEquals(2.0, resultado.notaFinal()); 
    }

    @Test
    @DisplayName("Não deve permitir submeter simulado se o prazo já acabou")
    void deveBloquearSubmissaoAtrasada() {
        simulado.setDataFimDisponivel(LocalDateTime.now().minusMinutes(10));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("aluno.teste");
        when(alunoRepository.findByMatricula("aluno.teste")).thenReturn(Optional.of(aluno));
        when(simuladoRepository.findById(simulado.getId())).thenReturn(Optional.of(simulado));

        SimuladoNotAvailableException ex = assertThrows(SimuladoNotAvailableException.class, () -> {
            simuladoService.submeterSimulado(simulado.getId(), new SimuladoSubmeterDTO(Collections.emptyList()));
        });

        assertEquals("O prazo para este simulado expirou", ex.getMessage());
    }
}