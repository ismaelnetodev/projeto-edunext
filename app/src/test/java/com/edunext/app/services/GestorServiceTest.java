package com.edunext.app.services;

import com.edunext.app.dtos.GestorCadastroDTO;
import com.edunext.app.model.Gestor;
import com.edunext.app.repository.GestorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GestorServiceTest {

    @Mock private GestorRepository gestorRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private GestorService gestorService;

    @Test
    @DisplayName("Deve criptografar a senha antes de salvar o gestor")
    void deveCriptografarSenha(){
        GestorCadastroDTO dto = new GestorCadastroDTO("Admin", "admin", "senha123");

        when(passwordEncoder.encode("senha123")).thenReturn("hash_seguro_abc");

        gestorService.cadastrarGestor(dto);

        ArgumentCaptor<Gestor> gestorCaptor = ArgumentCaptor.forClass(Gestor.class);
        verify(gestorRepository).save(gestorCaptor.capture());

        Gestor gestorSalvo = gestorCaptor.getValue();
        assertEquals("hash_seguro_abc", gestorSalvo.getPassword());
        assertEquals("Admin", gestorSalvo.getNome());
    }

}
