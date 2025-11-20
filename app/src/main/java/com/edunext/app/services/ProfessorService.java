package com.edunext.app.services;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edunext.app.dtos.ProfessorCadastroDTO;
import com.edunext.app.enums.Roles;
import com.edunext.app.exceptions.ResourceAlreadyExistsException;
import com.edunext.app.exceptions.ResourceNotFoundException;
import com.edunext.app.exceptions.UnauthorizedOperationException;
import com.edunext.app.model.Professor;
import com.edunext.app.model.Turma;
import com.edunext.app.repository.ProfessorRepository;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfessorService (ProfessorRepository professorRepository, PasswordEncoder passwordEncoder) {
        this.professorRepository = professorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Cadastrar professor
    @Transactional
    public Professor cadastroProfessor (ProfessorCadastroDTO data) {

        if (professorRepository.findByLogin(data.login()) != null){
            throw new ResourceAlreadyExistsException("Professor", "login", data.login());
        }

        String senhaCriptografada = passwordEncoder.encode(data.password());
        Professor professor = new Professor();
        professor.setNome(data.nome());
        professor.setLogin(data.login());
        professor.setPassword(senhaCriptografada);
        professor.setRole(Roles.PROFESSOR); 
        return professorRepository.save(professor);
    }

    // Retorna todos os professores
    @Transactional(readOnly = true)
    public Page<Professor> getProfessores(@NonNull Pageable pageable) {
        return professorRepository.findAll(pageable);
    } 

    // Retornar um professor por ID
    @Transactional(readOnly = true)
    public Professor getProfessor(@NonNull UUID id) {
        return professorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Professor", "id", id));
    }
    
    // Atualizar professor
    @Transactional
    public Professor updateProfessor(@NonNull UUID id, ProfessorCadastroDTO data) {
        Professor professor = getProfessor(id);

        professor.setNome(data.nome());
        professor.setLogin(data.login());

        String senhaCriptografada = passwordEncoder.encode(data.password());
        professor.setPassword(senhaCriptografada);
        return professorRepository.save(professor);
    }

    // Deletar professor
    @Transactional
    public void deleteProfessor(@NonNull UUID id) {
        if (!professorRepository.existsById(id)){
            throw new ResourceNotFoundException("Professor", "id", id);
        }
        professorRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Turma> getMinhasTurmas(){
        Professor professor = getProfessorLogado();
        return professor.getTurmas();
    }

    private Professor getProfessorLogado(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login;

        if (authentication.getPrincipal() instanceof UserDetails){
            login = ((UserDetails) authentication.getPrincipal()).getUsername();
        } else {
            login = authentication.getPrincipal().toString();
        }

        Professor professor = (Professor) professorRepository.findByLogin(login);

        if (professor == null) throw new UnauthorizedOperationException("Professor não autenticado");

        return professor;
    }
}
