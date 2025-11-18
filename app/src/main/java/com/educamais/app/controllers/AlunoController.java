package com.educamais.app.controllers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.educamais.app.dtos.AlterarSenhaDTO;
import com.educamais.app.dtos.AlunoCadastroDTO;
import com.educamais.app.dtos.AlunoResponseDTO;
import com.educamais.app.dtos.AvaliacaoResponseDTO;
import com.educamais.app.model.Aluno;
import com.educamais.app.model.AvaliacaoQualitativa;
import com.educamais.app.services.AlunoService;
import com.educamais.app.services.AvaliacaoQualitativaService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoService alunoService;
    private final AvaliacaoQualitativaService avaliacaoQualitativaService;

    public AlunoController(AlunoService alunoService, AvaliacaoQualitativaService avaliacaoQualitativaService){
        this.alunoService = alunoService;
        this.avaliacaoQualitativaService = avaliacaoQualitativaService;
    }

    @PostMapping()
    public ResponseEntity<?> criarALuno(@Valid @RequestBody AlunoCadastroDTO data) {
        try{
            Aluno alunoSalvo = this.alunoService.criarAluno(data);
            AlunoResponseDTO responseDTO = new AlunoResponseDTO(alunoSalvo);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    

    @GetMapping
    public ResponseEntity<Page<AlunoResponseDTO>> listarAlunos(
        @RequestParam(required = false) String busca,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size, 
        @RequestParam(defaultValue = "nome") String sortBy, 
        @RequestParam(defaultValue = "asc") String sortDir
        ){
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<Aluno> alunos = alunoService.getAlunos(busca, pageable);
        Page<AlunoResponseDTO> responseDTOs = alunos.map(AlunoResponseDTO::new);

        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponseDTO> getAlunoById(@PathVariable UUID id) {
        Aluno aluno = alunoService.getAlunoById(id);

        if (aluno == null){
            return ResponseEntity.notFound().build();
        }

        AlunoResponseDTO responseDTO = new AlunoResponseDTO(aluno);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlunoResponseDTO> updateAluno(@PathVariable UUID id, @Valid @RequestBody AlunoCadastroDTO data) {
        Aluno alunoAtualizado = this.alunoService.updateAluno(id, data);

        if (alunoAtualizado == null){
            return ResponseEntity.notFound().build();
        }
        AlunoResponseDTO responseDTO = new AlunoResponseDTO(alunoAtualizado);
        return ResponseEntity.ok().body(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAluno(@PathVariable UUID id){
        Aluno alunoDeletado = alunoService.deleteAluno(id);

        if (alunoDeletado == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{alunoId}/avaliacoes")
    public ResponseEntity<List<AvaliacaoResponseDTO>> getHistoricoDoAluno(@PathVariable UUID alunoId) {
        try {
            List<AvaliacaoQualitativa> historico = avaliacaoQualitativaService.getHistoricoDoAluno(alunoId);

            List<AvaliacaoResponseDTO> dtos = historico.stream()
                .map(AvaliacaoResponseDTO::new)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(dtos);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("{id}/resetar-senha")
    public ResponseEntity<String> resetarSenha(@PathVariable UUID id) {
        try {
            String senhaTemporaria = alunoService.resetarSenha(id);
            return ResponseEntity.ok("Senha redefinida. Senha temporária gerada: " + senhaTemporaria);
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/alterar-senha")
    public ResponseEntity<String> alterarSenha(@Valid @RequestBody AlterarSenhaDTO data, Authentication authentication) {
        try{
            alunoService.alterarSenha(authentication.getName(), data.senhaAntiga(), data.novaSenha());
            return ResponseEntity.ok("Senha alterada com sucesso.");
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
