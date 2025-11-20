package com.edunext.app.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.edunext.app.dtos.QuestaoCadastroDTO;
import com.edunext.app.dtos.QuestaoResponseDTO;
import com.edunext.app.model.Questao;
import com.edunext.app.services.QuestaoService;

import jakarta.validation.Valid;

import java.net.URI;

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
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/questoes")
public class QuestaoController {

    private final QuestaoService questaoService;

    public QuestaoController(QuestaoService questaoService){
        this.questaoService = questaoService;
    }

    @GetMapping()
    public ResponseEntity<Page<QuestaoResponseDTO>> getAllQuestoes(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "enunciado") String sortBy,
        @RequestParam(required = false) Long disciplinaId
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));

        Page<Questao> questoes;
        if (disciplinaId != null){
            questoes = questaoService.getAllQuestoesByDisciplina(disciplinaId, pageable);
        } else {
            questoes = questaoService.getAllQuestoes(pageable);
        }

        Page<QuestaoResponseDTO> responseDTO = questoes.map(QuestaoResponseDTO::new);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("{id}")
    public ResponseEntity<QuestaoResponseDTO> getQuestao(@PathVariable @NonNull Long id) {
        Questao questao = questaoService.getQuestao(id);
        return ResponseEntity.ok(new QuestaoResponseDTO(questao));
    }

    @PostMapping()
    public ResponseEntity<?> criarQuestao(@Valid @RequestBody QuestaoCadastroDTO data) {
        Questao questao = questaoService.criarQuestao(data);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(questao.getId())
            .toUri();
        
        return ResponseEntity.created(uri).body(new QuestaoResponseDTO(questao));
    }
    
    @PutMapping("{id}")
    public ResponseEntity<QuestaoResponseDTO> updateQuestao(@PathVariable @NonNull Long id, @RequestBody QuestaoCadastroDTO data) {
        Questao questao = questaoService.updateQuestao(data, id);
        return ResponseEntity.ok(new QuestaoResponseDTO(questao));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletarQuestao(@PathVariable @NonNull Long id){
        questaoService.deleteQuestao(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/clone")
    public ResponseEntity<QuestaoResponseDTO> clonarQuestao(@PathVariable @NonNull Long id) {
        Questao questaoClonada = questaoService.clonarQuestao(id);
        
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/questoes/{id}")
            .buildAndExpand(questaoClonada.getId())
            .toUri();
        
        return ResponseEntity.created(uri).body(new QuestaoResponseDTO(questaoClonada));
    }
    

}
