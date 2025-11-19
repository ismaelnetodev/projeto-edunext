package com.educamais.app.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.educamais.app.dtos.TurmaCadastroDTO;
import com.educamais.app.model.Aluno;
import com.educamais.app.model.Professor;
import com.educamais.app.model.Turma;
import com.educamais.app.repository.ProfessorRepository;
import com.educamais.app.repository.TurmaRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final ProfessorRepository professorRepository;

    public TurmaService(TurmaRepository turmaRepository, ProfessorRepository professorRepository){
        this.turmaRepository = turmaRepository;
        this.professorRepository = professorRepository;
    }

    public Turma criarTurma(TurmaCadastroDTO data){
        if (turmaRepository.existsByNomeAndAnoLetivo(data.nome(), data.anoLetivo())){
            return null;
        }

        Turma novaTurma = new Turma();
        novaTurma.setNome(data.nome());
        novaTurma.setAnoLetivo(data.anoLetivo());

        return this.turmaRepository.save(novaTurma);
    }

    @Transactional(readOnly = true)
    public Page<Turma> listarTurmas(Pageable pageable){
        return turmaRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Turma> getTurma(Long id){
        return this.turmaRepository.findById(id);
    }

    public Turma updateTurma(Long id, TurmaCadastroDTO data){
        Optional<Turma> turmaExistenteOpcional = this.turmaRepository.findById(id);

        if (turmaExistenteOpcional.isEmpty()){
            return null;
        }

        Turma turmaExistente = turmaExistenteOpcional.get();
        turmaExistente.setNome(data.nome());
        turmaExistente.setAnoLetivo(data.anoLetivo());
        return this.turmaRepository.save(turmaExistente);
    }

    public Turma deleteTurma(Long id){
        Optional<Turma> turma = this.turmaRepository.findById(id);
        if (turma.isPresent()) {
            this.turmaRepository.deleteById(id);
            return turma.get();
        }
        return null;
    }

    @Transactional
    public Turma associarProfessor(Long turmaId, UUID professorId){
        Turma turma = turmaRepository.findById(turmaId).orElseThrow(() -> new RuntimeException("Turma não encontrada"));


        Professor professor = professorRepository.findById(professorId).orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        professor.getTurmas().add(turma);

        professorRepository.save(professor);
        return turma;
    }

    @Transactional(readOnly = true)
    public List<Aluno> getAlunosPorTurma(Long turmaId){
        Turma turma = turmaRepository.findById(turmaId).orElseThrow(() -> new RuntimeException("Turma não encontrada"));
        return turma.getAlunos();
    }

    @Transactional(readOnly = true)
    public List<Turma> buscarTurmas(String termoBusca){
        Pageable limite = PageRequest.of(0, 10);
        return turmaRepository.buscaRapidaPorNome(termoBusca, limite);
    }

}
