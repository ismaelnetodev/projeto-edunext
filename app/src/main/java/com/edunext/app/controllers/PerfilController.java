package com.edunext.app.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edunext.app.dtos.AlunoPerfilDTO;
import com.edunext.app.services.PerfilService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/perfil")
public class PerfilController {
    private final PerfilService perfilService;

    public PerfilController(PerfilService perfilService){
        this.perfilService = perfilService;
    }

    @GetMapping("/aluno")
    public ResponseEntity<AlunoPerfilDTO> getMeuPerfil() {
        AlunoPerfilDTO perfil = perfilService.getMeuPerfil();
        return ResponseEntity.ok(perfil);
    }
    
}
