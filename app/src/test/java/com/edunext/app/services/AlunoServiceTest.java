package com.edunext.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.edunext.app.dtos.AlunoCadastroDTO;
import com.edunext.app.exceptions.ResourceNotFoundException;
import com.edunext.app.model.Aluno;
import com.edunext.app.model.Turma;
import com.edunext.app.repository.AlunoRepository;
import com.edunext.app.repository.TurmaRepository;

@ExtendWith(MockitoExtension.class)
public class AlunoServiceTest {

    @Mock private AlunoRepository alunoRepository;

    @Mock private TurmaRepository turmaRepository;

    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private AlunoService alunoService;

    private Aluno aluno;
    private Turma turma;
    private UUID alunoId;

    @BeforeEach
    void setup(){
        alunoId = UUID.randomUUID();
        turma = new Turma("3 Ano A", 2025);
        turma.setId(1L);

        aluno = new Aluno("2025001", turma);
        aluno.setId(alunoId);
        aluno.setNome("Aluno teste");
        aluno.setPassword("senha_hash");
    }

    @Test
    @DisplayName("Deve criar aluno com sucesso")
    void deveCriarAluno() {
        AlunoCadastroDTO dto = new AlunoCadastroDTO("Aluno teste", "2025001", null, 1L);

        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
        when(alunoRepository.findByMatricula("2025001")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encoded_pass");
        when(alunoRepository.save(any(Aluno.class))).thenReturn(aluno);

        Aluno criado = alunoService.criarAluno(dto);

        assertNotNull(criado);
        assertEquals("Aluno teste", criado.getNome());
        verify(alunoRepository).save(any(Aluno.class));
    }

    @Test
    @DisplayName("Deve buscar aluno por ID com sucesso")
    void deveBuscarAlunoPorId(){
        when(alunoRepository.findByIdWithTurma(alunoId)).thenReturn(Optional.of(aluno));

        Aluno encontrado = alunoService.getAlunoById(alunoId);
        assertEquals(alunoId, encontrado.getId());
    }

    @Test
    @DisplayName("Deve lançar erro ao buscar aluno inexistente")
    void deveFalharAoBuscarAlunoInexistente(){
        UUID idAleatorio = UUID.randomUUID();
        when(alunoRepository.findByIdWithTurma(idAleatorio)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            alunoService.getAlunoById(idAleatorio);
        });
    }

    @Test
    @DisplayName("Deve atualizar aluno com sucesso")
    void deveAtualizarAluno(){
        AlunoCadastroDTO dtoAtualizacao = new AlunoCadastroDTO("Teste atualizado", "2025001", null, 1L);

        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
        when(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno));
        when(alunoRepository.save(any(Aluno.class))).thenAnswer(i -> i.getArgument(0));

        Aluno atualizado = alunoService.updateAluno(alunoId, dtoAtualizacao);

        assertEquals("Teste atualizado", atualizado.getNome());
        verify(alunoRepository).save(aluno);
    }

    @Test
    @DisplayName("Deve listar alunos paginados")
    void deveListarAlunos(){
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Aluno> page = new PageImpl<>(List.of(aluno));

        when(alunoRepository.findAll(pageable)).thenReturn(page);

        Page<Aluno> resultado = alunoService.getAlunos(pageable);

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.getTotalElements());
    }
}
