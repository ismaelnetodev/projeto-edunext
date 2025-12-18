package com.edunext.app.controllers;

import com.edunext.app.dtos.SimuladoGerarDTO;
import com.edunext.app.dtos.SimuladoResponseDTO;
import com.edunext.app.services.SimuladoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SimuladoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private SimuladoService simuladoService;

    @Test
    @DisplayName("Deve retornar 201 Created ao gerar simulado como Professor")
    @WithMockUser(username = "prof.teste", roles = {"PROFESSOR"})
    void deveGerarSimulado() throws  Exception {
        SimuladoGerarDTO dto = new SimuladoGerarDTO(
                "Prova Teste", 1L, 10L, 5, LocalDateTime.now(), LocalDateTime.now().plusHours(2)
        );

        SimuladoResponseDTO responseMock = new SimuladoResponseDTO(
                100L, "Prova Teste", "Prof Teste", "Turma A", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>()
        );

        when(simuladoService.gerarSimuladoAleatorio(any())).thenReturn(responseMock);

        mockMvc.perform(post("/simulados/gerar-aleatorio")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.titulo").value("Prova Teste"));
    }

    @Test
    @DisplayName("Deve retornar 403 Forbidden se Aluno tentar gerar simulado")
    @WithMockUser(username = "aluno.teste", roles = {"ALUNO"})
    void deveNegarAcessoAluno() throws Exception {
        SimuladoGerarDTO dto = new SimuladoGerarDTO(
                "Prova Hack", 1L, 10L, 5, null, null
        );

        mockMvc.perform(post("/simulados/gerar-aleatorio")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

}
