package com.edunext.app.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.edunext.app.dtos.ProfessorCadastroDTO;
import com.edunext.app.dtos.ProfessorResponseDTO;
import com.edunext.app.dtos.TurmaResponseDTO;
import com.edunext.app.model.Professor;
import com.edunext.app.model.Turma;
import com.edunext.app.services.ProfessorService;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
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
        Professor professor = professorService.cadastroProfessor(data);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(professor.getId())
            .toUri();
        return ResponseEntity.created(uri).body(new ProfessorResponseDTO(professor));
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
    public ResponseEntity<ProfessorResponseDTO> getProfessorById(@PathVariable @NonNull UUID id) {
        Professor professor = professorService.getProfessor(id);
        return ResponseEntity.ok(new ProfessorResponseDTO(professor));
    }

    @PutMapping("{id}")
    public ResponseEntity<ProfessorResponseDTO> atualizarProfessor(@PathVariable @NonNull UUID id, @RequestBody ProfessorCadastroDTO data) {
        Professor professor = professorService.updateProfessor(id, data);
        return ResponseEntity.ok(new ProfessorResponseDTO(professor));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> detelarProfessor(@PathVariable @NonNull UUID id) {
        professorService.deleteProfessor(id);
        return ResponseEntity.noContent().build();
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
