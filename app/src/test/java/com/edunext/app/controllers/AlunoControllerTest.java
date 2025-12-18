package com.edunext.app.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.edunext.app.dtos.AlunoCadastroDTO;
import com.edunext.app.exceptions.ResourceNotFoundException;
import com.edunext.app.model.Aluno;
import com.edunext.app.model.Turma;
import com.edunext.app.services.AlunoService;
import com.edunext.app.services.AvaliacaoQualitativaService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class AlunoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean 
    private AlunoService alunoService;
    
    @MockitoBean 
    private AvaliacaoQualitativaService avaliacaoQualitativaService;

    @Test
    @DisplayName("GET /alunos - Deve retornar 200 e lista vazia para ADMIN")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void listarAlunosComoAdmin() throws Exception {
        Page<Aluno> pageVazia = new PageImpl<>(List.of());
        when(alunoService.getAlunos(any(Pageable.class))).thenReturn(pageVazia);

        mockMvc.perform(get("/alunos"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /alunos - Deve retornar 403 Forbidden para ALUNO")
    @WithMockUser(username = "aluno", roles = {"ALUNO"})
    void listarAlunosSendoAluno() throws Exception {
        mockMvc.perform(get("/alunos"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /alunos - Deve criar aluno (201) se for ADMIN")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void criarAlunoComSucesso() throws Exception {
        AlunoCadastroDTO dto = new AlunoCadastroDTO("Novo Aluno", "2024001", null, 1L);
        
        Aluno alunoMock = new Aluno("2024001", new Turma());
        alunoMock.setId(UUID.randomUUID());
        alunoMock.setNome("Novo Aluno");

        when(alunoService.criarAluno(any(AlunoCadastroDTO.class))).thenReturn(alunoMock);

        mockMvc.perform(post("/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Novo Aluno"));
    }

    @Test
    @DisplayName("GET /alunos/{id} - Deve retornar 404 se não encontrar")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void buscarAlunoInexistente() throws Exception {
        UUID id = UUID.randomUUID();
        when(alunoService.getAlunoById(id)).thenReturn(null);

        mockMvc.perform(get("/alunos/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /alunos/{id} - Deve deletar aluno se for ADMIN")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deletarAluno() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(alunoService).deleteAluno(id);

        mockMvc.perform(delete("/alunos/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /alunos/{id} - Deve retornar 404 se tentar deletar aluno inexistente")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deletarAlunoInexistente() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new ResourceNotFoundException("Aluno", "id", id)).when(alunoService).deleteAluno(id);

        mockMvc.perform(delete("/alunos/{id}", id))
                .andExpect(status().isNotFound());
    }
}