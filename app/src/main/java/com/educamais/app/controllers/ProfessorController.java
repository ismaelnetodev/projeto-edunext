package com.educamais.app.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.educamais.app.dtos.ProfessorCadastroDTO;
import com.educamais.app.dtos.ProfessorResponseDTO;
import com.educamais.app.dtos.TurmaResponseDTO;
import com.educamais.app.model.Professor;
import com.educamais.app.model.Turma;
import com.educamais.app.services.ProfessorService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/professores")
public class ProfessorController {
    private final ProfessorService professorService;
    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @PostMapping()
    public ResponseEntity<?> criarProfessor(@RequestBody ProfessorCadastroDTO data) {
        try{
            Professor professor = professorService.cadastroProfessor(data);
            ProfessorResponseDTO professorResponse = new ProfessorResponseDTO(professor);
            return ResponseEntity.status(HttpStatus.CREATED).body(professorResponse);
        }
        catch (RuntimeException error) {
            return ResponseEntity.badRequest().body(error.getMessage());
        } 
    }

    @GetMapping()
    public ResponseEntity<Page<ProfessorResponseDTO>> getProfessores(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "nome") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Professor> professores = professorService.getProfessores(pageable);
        Page<ProfessorResponseDTO> responseDTO = professores.map(ProfessorResponseDTO::new);

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("{id}")
    public ResponseEntity<ProfessorResponseDTO> getProfessorById(@PathVariable UUID id) {
        Professor professor = professorService.getProfessor(id);
        if (professor == null) {
            return ResponseEntity.notFound().build();
        }
        ProfessorResponseDTO response = new ProfessorResponseDTO(professor);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("{id}")
    public ResponseEntity<ProfessorResponseDTO> atualizarProfessor(@PathVariable UUID id, @RequestBody ProfessorCadastroDTO data) {
        Professor professor = professorService.updateProfessor(id, data);
        if (professor == null) {
            ResponseEntity.notFound().build();
        } 
        ProfessorResponseDTO response = new ProfessorResponseDTO(professor);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> detelarProfessor(@PathVariable UUID id) {
        boolean professorDeletado = professorService.deleteProfessor(id);
        if (professorDeletado) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    
    @GetMapping("/minhas-turmas")
    public ResponseEntity<List<TurmaResponseDTO>> getMinhasTurmas() {
        List<Turma> turmas = professorService.getMinhasTurmas();
        List<TurmaResponseDTO> dtos = turmas.stream()
            .map(TurmaResponseDTO::new)
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
    


}
