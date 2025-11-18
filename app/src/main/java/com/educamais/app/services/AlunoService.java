package com.educamais.app.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import com.educamais.app.dtos.AlunoCadastroDTO;
import com.educamais.app.enums.Roles;
import com.educamais.app.exceptions.InvalidPasswordException;
import com.educamais.app.exceptions.ResourceAlreadyExistsException;
import com.educamais.app.exceptions.ResourceNotFoundException;
import com.educamais.app.model.Aluno;
import com.educamais.app.model.Turma;
import com.educamais.app.repository.AlunoRepository;
import com.educamais.app.repository.TurmaRepository;

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

    public Page<Aluno> getAlunos(String termoBusca, Pageable pageable){
        if (termoBusca == null || termoBusca.trim().isEmpty()){
            return alunoRepository.findAll(pageable);
        } else {
            return alunoRepository.buscaRapidaPorNome(termoBusca, pageable);
        }
    }

    public Aluno getAlunoById(UUID id){
        return alunoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Aluno", "id", id));
    }

    public Aluno updateAluno(UUID id, AlunoCadastroDTO data){
        Turma turma = turmaRepository.findById(data.turmaId()).orElseThrow(() -> new ResourceNotFoundException("Turma", "id", data.turmaId()));

        Aluno alunoExistente = alunoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Aluno", "id", id));

        alunoExistente.setNome(data.nome());
        alunoExistente.setMatricula(data.matricula());
        alunoExistente.setLogin(data.matricula());
        alunoExistente.setTurma(turma);

        return this.alunoRepository.save(alunoExistente);
    }

    public Aluno deleteAluno(UUID id){
        Optional<Aluno> aluno = this.alunoRepository.findById(id);
        if (aluno.isPresent()){
            this.alunoRepository.deleteById(id);
            return aluno.get();
        }

        return null;
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

    public String resetarSenha(UUID alunoId){
        Aluno aluno = alunoRepository.findById(alunoId)
            .orElseThrow(() -> new ResourceNotFoundException("Aluno", "id", alunoId));

        String senhaTemporaria = "A@" + aluno.getMatricula().substring(Math.max(0, aluno.getMatricula().length() - 4));
        aluno.setPassword(passwordEncoder.encode(senhaTemporaria));
        aluno.setSenhaTemporaria(true);
        alunoRepository.save(aluno);
        return senhaTemporaria;
    }

}
