package com.edunext.app.controllers;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.edunext.app.dtos.AlunoResponseDTO;
import com.edunext.app.dtos.AssociacaoProfessorDTO;
import com.edunext.app.dtos.TurmaCadastroDTO;
import com.edunext.app.dtos.TurmaResponseDTO;
import com.edunext.app.model.Aluno;
import com.edunext.app.model.Turma;
import com.edunext.app.services.TurmaService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;




@RestController
@RequestMapping("/turmas")
public class TurmaController {
    private final TurmaService turmaService;

    public TurmaController(TurmaService turmaService){
        this.turmaService = turmaService;
    }

    @GetMapping
    public ResponseEntity<Page<TurmaResponseDTO>> listarTurmas(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "nome") String sortBy
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Turma> turmas = turmaService.listarTurmas(pageable);
        Page<TurmaResponseDTO> dtos = turmas.map(TurmaResponseDTO::new);
        return ResponseEntity.ok(dtos);        
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurmaResponseDTO> getTurma(@PathVariable Long id) {
        Turma turma = turmaService.getTurma(id);
        return ResponseEntity.ok(new TurmaResponseDTO(turma));
    }

    @PostMapping
    public ResponseEntity<?> criarTurma(@Valid @RequestBody TurmaCadastroDTO data){
        Turma turmaSalva = turmaService.criarTurma(data);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(turmaSalva.getId()).toUri();

        return ResponseEntity.created(uri).body(new TurmaResponseDTO(turmaSalva));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TurmaResponseDTO> atualizarTurma(@PathVariable Long id, @RequestBody TurmaCadastroDTO data) {
        Turma turmaAtualizada = turmaService.updateTurma(id, data);
        return ResponseEntity.ok(new TurmaResponseDTO(turmaAtualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Turma> deletarTurma(@PathVariable @NonNull Long id){
        turmaService.deleteTurma(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{turmaId}/associar-professor")
    public ResponseEntity<TurmaResponseDTO> associarProfessor(@PathVariable @NonNull Long turmaId, 
        @RequestBody AssociacaoProfessorDTO data) {
        Turma turmaAtualizada = turmaService.associarProfessor(turmaId, data.professorId());
        return ResponseEntity.ok(new TurmaResponseDTO(turmaAtualizada));
    }

    @GetMapping("/{turmaId}/alunos")
    public ResponseEntity<List<AlunoResponseDTO>> getAlunosDaTurma(@PathVariable Long turmaId) {
        List<Aluno> alunos = turmaService.getAlunosPorTurma(turmaId);
        List<AlunoResponseDTO> dtos = alunos.stream()
            .map(AlunoResponseDTO::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<List<TurmaResponseDTO>> buscarTurma(@RequestParam("termo") String termo) {
        List<Turma> turma = turmaService.buscarTurmas(termo);

        List<TurmaResponseDTO> dtos = turma.stream()
            .map(TurmaResponseDTO::new)
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
    

}
