package com.edunext.app.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edunext.app.dtos.TurmaCadastroDTO;
import com.edunext.app.exceptions.ResourceAlreadyExistsException;
import com.edunext.app.exceptions.ResourceNotFoundException;
import com.edunext.app.model.Aluno;
import com.edunext.app.model.Professor;
import com.edunext.app.model.Turma;
import com.edunext.app.repository.ProfessorRepository;
import com.edunext.app.repository.TurmaRepository;

@Service
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final ProfessorRepository professorRepository;

    public TurmaService(TurmaRepository turmaRepository, ProfessorRepository professorRepository){
        this.turmaRepository = turmaRepository;
        this.professorRepository = professorRepository;
    }

    @Transactional
    public Turma criarTurma(TurmaCadastroDTO data){
        if (turmaRepository.existsByNomeAndAnoLetivo(data.nome(), data.anoLetivo())){
            throw new ResourceAlreadyExistsException("Turma", "nome e ano letivo", data.nome() + " - " + data.anoLetivo());
        }

        Turma novaTurma = new Turma();
        novaTurma.setNome(data.nome());
        novaTurma.setAnoLetivo(data.anoLetivo());

        return turmaRepository.save(novaTurma);
    }

    @Transactional(readOnly = true)
    public Page<Turma> listarTurmas(@NonNull Pageable pageable){
        return turmaRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Turma getTurma(Long id){
        return turmaRepository.findByIdWithRelations(id).orElseThrow(() -> new ResourceNotFoundException("Turma", "id", id));
    }

    @Transactional
    public Turma updateTurma(Long id, TurmaCadastroDTO data){
        Turma turmaExistente = getTurma(id);

        turmaExistente.setNome(data.nome());
        turmaExistente.setAnoLetivo(data.anoLetivo());

        return turmaRepository.save(turmaExistente);
    }

    @Transactional
    public void deleteTurma(@NonNull Long id){
        if (!turmaRepository.existsById(id)){
            throw new ResourceNotFoundException("Turma", "id", id);
        }

        turmaRepository.deleteById(id);
    }

    @Transactional
    public Turma associarProfessor(@NonNull Long turmaId, @NonNull UUID professorId){
        Turma turma = turmaRepository.findByIdWithRelations(turmaId).orElseThrow(() -> new ResourceNotFoundException("Turma", "id", turmaId));

        Professor professor = professorRepository.findById(professorId).orElseThrow(() -> new ResourceNotFoundException("Professor", "id", professorId));

        if (professor.getTurmas() == null){
            professor.setTurmas(new ArrayList<>());
        }

        if (!professor.getTurmas().contains(turma)){ 
            professor.getTurmas().add(turma);
        }

        professorRepository.save(professor);
        return turma;
    }

    @Transactional(readOnly = true)
    public List<Aluno> getAlunosPorTurma(Long turmaId){
        Turma turma = getTurma(turmaId);

        return turma.getAlunos();
    }

    @Transactional(readOnly = true)
    public List<Turma> buscarTurmas(String termoBusca){
        Pageable limite = PageRequest.of(0, 10);
        return turmaRepository.buscaRapidaPorNome(termoBusca, limite);
    }

}
