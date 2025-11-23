package com.edunext.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.edunext.app.dtos.AlunoCadastroDTO;
import com.edunext.app.enums.Roles;
import com.edunext.app.model.Aluno;
import com.edunext.app.model.Turma;
import com.edunext.app.repository.AlunoRepository;
import com.edunext.app.repository.TurmaRepository;

@ExtendWith(MockitoExtension.class)
public class AlunoServiceTest {

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private TurmaRepository turmaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AlunoService alunoService;

    @Test
    @DisplayName("Deve criar um aluno com sucesso quando a turma existe e matrícula é nova")
    void deveCriarAlunoComSucesso(){
        Long turmaId = 1L;
        Turma turmaMock = new Turma("1° Ano A", 2025);
        turmaMock.setId(turmaId);

        AlunoCadastroDTO dto = new AlunoCadastroDTO("João Silva", "2025001", null, turmaId);

        when(turmaRepository.findById(any())).thenReturn(Optional.of(turmaMock));
        when(alunoRepository.save(any(Aluno.class))).thenAnswer(invocation -> {
            Aluno alunoSalvo = invocation.getArgument(0);
            alunoSalvo.setId(UUID.randomUUID());
            return alunoSalvo;
        });

        Aluno alunoCriado = alunoService.criarAluno(dto);

        assertNotNull(alunoCriado);
        assertEquals("João Silva", alunoCriado.getNome());
        assertEquals("2025001", alunoCriado.getMatricula());
        assertEquals(Roles.ALUNO, alunoCriado.getRole());
        assertEquals(turmaMock, alunoCriado.getTurma());

        verify(alunoRepository).save(any(Aluno.class));
    }

}
