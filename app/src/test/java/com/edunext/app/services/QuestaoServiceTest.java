package com.edunext.app.services;

import com.edunext.app.dtos.QuestaoCadastroDTO;
import com.edunext.app.enums.TipoQuestao;
import com.edunext.app.exceptions.UnauthorizedOperationException;
import com.edunext.app.model.Disciplina;
import com.edunext.app.model.Professor;
import com.edunext.app.model.Questao;
import com.edunext.app.repository.ProfessorRepository;
import com.edunext.app.repository.QuestaoRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuestaoServiceTest {

    @Mock private QuestaoRepository questaoRepository;
    @Mock private ProfessorRepository professorRepository;
    @Mock private DisciplinaService disciplinaService;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks private QuestaoService questaoService;

    private Professor professorLogado;
    private Professor outroProfessor;
    private Questao questao;

    @BeforeEach
    void setup(){
        SecurityContextHolder.setContext(securityContext);

        professorLogado = new Professor();
        professorLogado.setId(UUID.randomUUID());
        professorLogado.setLogin("prof.logado");

        outroProfessor = new Professor();
        outroProfessor.setId(UUID.randomUUID());
        outroProfessor.setLogin("prof.outro");

        questao = new Questao();
        questao.setId(1L);
        questao.setEnunciado("Questão Original");
        questao.setProfessorCriador(professorLogado);
    }

    @Test
    @DisplayName("Deve atualizar questão se o professor for o dono.")
    void deveAtualizarQuestaoSeDono(){
        QuestaoCadastroDTO dto = new QuestaoCadastroDTO(
                "Novo Enunciado", "Matemática", TipoQuestao.MULTIPLA_ESCOLHA, 1.0, null, List.of("A", "B"), "A"
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("prof.logado");
        when(professorRepository.findByLogin("prof.logado")).thenReturn(professorLogado);
        when(questaoRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(questao));
        when(disciplinaService.buscarOuCriar("Matemática")).thenReturn(new Disciplina());
        when(questaoRepository.save(any(Questao.class))).thenAnswer(i -> i.getArgument(0));

        Questao atualizada = questaoService.updateQuestao(dto, 1L);

        assertEquals("Novo Enunciado", atualizada.getEnunciado());
        verify(questaoRepository).save(any(Questao.class));
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar atualizar questão de outro professor")
    void deveFalharAtualizacaoSeNaoForDono(){
        questao.setProfessorCriador(outroProfessor);

        QuestaoCadastroDTO dto = new QuestaoCadastroDTO(
                "Novo Enunciado", "Matemática", TipoQuestao.MULTIPLA_ESCOLHA, 1.0, null, List.of("A", "B"), "A"
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("prof.logado");
        when(professorRepository.findByLogin("prof.logado")).thenReturn(professorLogado);
        when(questaoRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(questao));

        assertThrows(UnauthorizedOperationException.class, () -> {
            questaoService.updateQuestao(dto, 1L);
        });

        verify(questaoRepository, never()).save(any());
    }

}
