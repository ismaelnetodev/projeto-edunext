package com.edunext.app.services;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edunext.app.model.Disciplina;
import com.edunext.app.repository.DisciplinaRepository;

@Service
public class DisciplinaService {
    private DisciplinaRepository disciplinaRepository;

    public DisciplinaService(DisciplinaRepository disciplinaRepository){
        this.disciplinaRepository = disciplinaRepository;
    }

    public List<Disciplina> listarTodas(){
        return disciplinaRepository.findAll();
    }

    @Transactional
    public Disciplina buscarOuCriar(String nomeDisciplina){
        if (nomeDisciplina == null || nomeDisciplina.trim().isEmpty()){
            throw new RuntimeException("Nome da disciplina é obrigatório");
        }

        String nomeFormatado = nomeDisciplina.trim();
        return disciplinaRepository.findByNomeIgnoreCase(nomeFormatado)
                .orElseGet(() -> {
                    Disciplina nova = new Disciplina();
                    String nomeCapitalizado = Character.toUpperCase(nomeFormatado.charAt(0)) 
                                            + nomeFormatado.substring(1).toLowerCase();
                    nova.setNome(nomeCapitalizado);
                    return disciplinaRepository.save(nova);
                });
    }

    public boolean existePorId(@NonNull Long id) {
        return disciplinaRepository.existsById(id);
    }
}
