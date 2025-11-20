package com.edunext.app.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edunext.app.dtos.SimuladoAlunoResponseDTO;
import com.edunext.app.dtos.SimuladoCriarDTO;
import com.edunext.app.dtos.SimuladoGerarDTO;
import com.edunext.app.dtos.SimuladoParaFazerDTO;
import com.edunext.app.dtos.SimuladoResponseDTO;
import com.edunext.app.dtos.SimuladoResumoDTO;
import com.edunext.app.dtos.SimuladoSubmeterDTO;
import com.edunext.app.services.SimuladoService;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;




@RestController
@RequestMapping("/simulados")
public class SimuladoController {

    private final SimuladoService simuladoService;

    public SimuladoController(SimuladoService simuladoService){
        this.simuladoService = simuladoService;
    }

    @PostMapping("/gerar-aleatorio")
    public ResponseEntity<?> gerarSimulado(@RequestBody SimuladoGerarDTO data) {
        try {
            SimuladoResponseDTO novoSimulado = simuladoService.gerarSimuladoAleatorio(data);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoSimulado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<SimuladoResponseDTO>> getSimuladoParaAluno() {
        List<SimuladoResponseDTO> dtos = simuladoService.getSimuladoParaAluno();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{simuladoId}/fazer")
    public ResponseEntity<SimuladoParaFazerDTO> getSimuladoParaFazer(@PathVariable Long simuladoId) {
        try {
            SimuladoParaFazerDTO dto = simuladoService.getSimuladoParaFazer(simuladoId);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{simuladoId}/submeter")
    public ResponseEntity<?> submeterSimulado(@PathVariable Long simuladoId, @RequestBody SimuladoSubmeterDTO data) {
        try{
            SimuladoAlunoResponseDTO resultadoDTO = simuladoService.submeterSimulado(simuladoId, data);
            return ResponseEntity.ok(resultadoDTO);
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/professor/meus-simulados")
    public ResponseEntity<List<SimuladoResponseDTO>> getSimuladosDoProfessor() {
        List<SimuladoResponseDTO> dtos = simuladoService.getSimuladosDoProfessorLogado();
        
        return ResponseEntity.ok(dtos);
    }


    @GetMapping("/{simuladoId}/resultados")
    public ResponseEntity<?> getResultadoDoSimulado(
        @PathVariable Long simuladoId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SimuladoAlunoResponseDTO> resultados = simuladoService.getResultadosDoSimulado(simuladoId, pageable);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/{simuladoId}/resumo")
    public ResponseEntity<?> getResumoSimulado(@PathVariable Long simuladoId) {
        try{
            SimuladoResumoDTO resumo = simuladoService.getResumoDoSimulado(simuladoId);
            return ResponseEntity.ok(resumo);
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
    
    @GetMapping("/{simuladoId}/alunos")
    public Page<SimuladoAlunoResponseDTO> getAlunosDoSimulado(@PathVariable Long simuladoId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return simuladoService.getAlunosDoSimulado(simuladoId, pageable);
    }

    @PostMapping("/criar")
    public ResponseEntity<SimuladoResponseDTO> criarSimulado(@RequestBody SimuladoCriarDTO data) {
        return ResponseEntity.ok(simuladoService.criarSimuladoComQuestoes(data));
    }
    
    

}
