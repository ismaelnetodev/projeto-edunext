package com.educamais.app.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.educamais.app.dtos.ProfessorCadastroDTO;
import com.educamais.app.enums.Roles;
import com.educamais.app.model.Professor;
import com.educamais.app.model.Turma;
import com.educamais.app.repository.ProfessorRepository;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfessorService (ProfessorRepository professorRepository, PasswordEncoder passwordEncoder) {
        this.professorRepository = professorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Cadastrar professor
    public Professor cadastroProfessor (ProfessorCadastroDTO data) {
        String senhaCriptografada = passwordEncoder.encode(data.password());
        Professor professor = new Professor();
        professor.setNome(data.nome());
        professor.setLogin(data.login());
        professor.setPassword(senhaCriptografada);
        professor.setRole(Roles.PROFESSOR); 
        return professorRepository.save(professor);
    }

    // Retorna todos os professores
    public Page<Professor> getProfessores(Pageable pageable) {
        return professorRepository.findAll(pageable);
    } 

    // Retornar um professor por ID
    public Professor getProfessor(UUID id) {
        Optional<Professor> professor = professorRepository.findById(id);
        if (professor.isEmpty()) return null;
        return professor.get();
    }
    
    // Atualizar professor
    public Professor updateProfessor(UUID id, ProfessorCadastroDTO data) {
        Optional<Professor> professor = professorRepository.findById(id);

        if (professor.isEmpty()) return null;

        Professor professorAtualizado = professor.get();
        professorAtualizado.setNome(data.nome());
        professorAtualizado.setLogin(data.login());

        String senhaCriptografada = passwordEncoder.encode(data.password());

        professorAtualizado.setPassword(senhaCriptografada);
        return this.professorRepository.save(professorAtualizado);
    }

    // Deletar professor
    public boolean deleteProfessor(UUID id) {
        Optional<Professor> professor = professorRepository.findById(id);
        if (professor.isPresent()) {
            professorRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<Turma> getMinhasTurmas(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login;

        if (authentication.getPrincipal() instanceof UserDetails){
            login = ((UserDetails) authentication.getPrincipal()).getUsername();
        }else{
            login = authentication.getPrincipal().toString();
        }

        Professor professor = (Professor) professorRepository.findByLogin(login);

        if (professor == null){
            throw new RuntimeException("Professor não encontrado");
        }

        return professor.getTurmas();
    }
}
