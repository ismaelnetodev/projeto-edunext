package com.educamais.app.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.educamais.app.dtos.QuestaoCadastroDTO;
import com.educamais.app.dtos.QuestaoResponseDTO;
import com.educamais.app.model.Questao;
import com.educamais.app.services.QuestaoService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/questao")
public class QuestaoController {

    private final QuestaoService questaoService;

    public QuestaoController(QuestaoService questaoService){
        this.questaoService = questaoService;
    }

    @GetMapping()
    public ResponseEntity<Page<QuestaoResponseDTO>> getAllQuestoes(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "nome") String sortBy,
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
    public ResponseEntity<QuestaoResponseDTO> getQuestao(@PathVariable Long id) {
        Questao questao = questaoService.getQuestao(id);

        if (questao == null) return ResponseEntity.notFound().build();

        QuestaoResponseDTO questaoResponse = new QuestaoResponseDTO(questao);
        return ResponseEntity.ok().body(questaoResponse);
    }

    @PostMapping()
    public ResponseEntity<?> criarQuestao(@Valid @RequestBody QuestaoCadastroDTO data) {
        try{
            Questao questao = questaoService.criarQuestao(data);
            QuestaoResponseDTO questaoResponse = new QuestaoResponseDTO(questao);
            return ResponseEntity.status(HttpStatus.CREATED).body(questaoResponse);
        } catch (RuntimeException err){
            return ResponseEntity.badRequest().body(err.getMessage());
        }
    }
    
    @PutMapping("{id}")
    public ResponseEntity<QuestaoResponseDTO> updateQuestao(@PathVariable Long id, @RequestBody QuestaoCadastroDTO data) {
        Questao questao = questaoService.updateQuestao(data, id);
        if (questao == null) return ResponseEntity.notFound().build();

        QuestaoResponseDTO questaoResponse = new QuestaoResponseDTO(questao);
        return ResponseEntity.ok().body(questaoResponse);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletarQuestao(@PathVariable Long id){
        boolean questaoDeletada = questaoService.deleteQuestao(id);
        if (questaoDeletada) return ResponseEntity.noContent().build();

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/clone")
    public ResponseEntity<QuestaoResponseDTO> clonarQuestao(@PathVariable Long id) {
        try{
            Questao questaoClonada = questaoService.clonarQuestao(id);
            return ResponseEntity.status(HttpStatus.CREATED).body(new QuestaoResponseDTO(questaoClonada));
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(null);
        }
    }
    

}
