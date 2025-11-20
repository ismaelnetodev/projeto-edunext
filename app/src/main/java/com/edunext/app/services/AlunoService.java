package com.edunext.app.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edunext.app.dtos.AlunoCadastroDTO;
import com.edunext.app.enums.Roles;
import com.edunext.app.exceptions.InvalidPasswordException;
import com.edunext.app.exceptions.ResourceAlreadyExistsException;
import com.edunext.app.exceptions.ResourceNotFoundException;
import com.edunext.app.model.Aluno;
import com.edunext.app.model.Turma;
import com.edunext.app.repository.AlunoRepository;
import com.edunext.app.repository.TurmaRepository;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;
    private final PasswordEncoder passwordEncoder;

    public AlunoService(AlunoRepository alunoRepository, TurmaRepository turmaRepository, PasswordEncoder passwordEncoder){
        this.alunoRepository = alunoRepository;
        this.turmaRepository = turmaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Aluno criarAluno(AlunoCadastroDTO data){
        Turma turma = turmaRepository.findById(data.turmaId()).orElseThrow(() -> new ResourceNotFoundException("Turma", "id", data.turmaId()));

        if (alunoRepository.findByMatricula(data.matricula()).isPresent()){
            throw new ResourceAlreadyExistsException("Aluno", "matrícula", data.matricula());
        }

        String senhaGerada = "A@" + data.matricula().substring(Math.max(0, data.matricula().length() - 4));

        Aluno novoAluno = new Aluno();
        novoAluno.setNome(data.nome());
        novoAluno.setLogin(data.matricula());
        novoAluno.setPassword(passwordEncoder.encode(senhaGerada));
        novoAluno.setMatricula(data.matricula());
        novoAluno.setRole(Roles.ALUNO);
        novoAluno.setEnabled(true);
        novoAluno.setFotoUrl(data.fotoUrl());
        novoAluno.setTurma(turma);
        novoAluno.setSenhaTemporaria(true);

        return alunoRepository.save(novoAluno);
    }

    @Transactional
    public Page<Aluno> getAlunos(@NonNull Pageable pageable){
        return alunoRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Aluno getAlunoById(UUID id){
        return alunoRepository.findByIdWithTurma(id)
            .orElseThrow(() -> new ResourceNotFoundException("Aluno", "id", id));
    }

    @Transactional
    public Aluno updateAluno(@NonNull UUID id, AlunoCadastroDTO data){
        Turma turma = turmaRepository.findById(data.turmaId()).orElseThrow(() -> new ResourceNotFoundException("Turma", "id", data.turmaId()));

        Aluno alunoExistente = alunoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Aluno", "id", id));

        alunoExistente.setNome(data.nome());
        alunoExistente.setMatricula(data.matricula());
        alunoExistente.setLogin(data.matricula());
        alunoExistente.setTurma(turma);

        return this.alunoRepository.save(alunoExistente);
    }

    @Transactional
    public void deleteAluno(@NonNull UUID id){
        if (!alunoRepository.existsById(id)){
            throw new ResourceNotFoundException("Aluno", "id", id);
        }

        alunoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Aluno> buscarAlunos(String termoBusca){
        Pageable limite = PageRequest.of(0, 10);
        return alunoRepository.buscaRapidaPorNome(termoBusca, limite);
    }

    public void alterarSenha(String login, String senhaAntiga, String novaSenha){
        Aluno aluno = alunoRepository.findByLogin(login)
            .orElseThrow(() -> new ResourceNotFoundException("Aluno", "login", login));

        if (!passwordEncoder.matches(senhaAntiga, aluno.getPassword())){
            throw new InvalidPasswordException();
        }

        aluno.setPassword(passwordEncoder.encode(novaSenha));
        aluno.setSenhaTemporaria(false);
        alunoRepository.save(aluno);
    }

    public String resetarSenha(@NonNull UUID alunoId){
        Aluno aluno = alunoRepository.findById(alunoId)
            .orElseThrow(() -> new ResourceNotFoundException("Aluno", "id", alunoId));

        String senhaTemporaria = "A@" + aluno.getMatricula().substring(Math.max(0, aluno.getMatricula().length() - 4));
        aluno.setPassword(passwordEncoder.encode(senhaTemporaria));
        aluno.setSenhaTemporaria(true);
        alunoRepository.save(aluno);
        return senhaTemporaria;
    }

}
