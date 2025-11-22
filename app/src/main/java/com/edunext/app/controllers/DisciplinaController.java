package com.edunext.app.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edunext.app.model.Disciplina;
import com.edunext.app.services.DisciplinaService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/disciplinas")
public class DisciplinaController {
    private final DisciplinaService disciplinaService;

    public DisciplinaController(DisciplinaService disciplinaService){
        this.disciplinaService = disciplinaService;
    }

    @GetMapping
    public ResponseEntity<List<Disciplina>> getAllDisciplinas() {
        List<Disciplina> disciplinas = disciplinaService.listarTodas();
        return ResponseEntity.ok(disciplinas);
    }
    
}
