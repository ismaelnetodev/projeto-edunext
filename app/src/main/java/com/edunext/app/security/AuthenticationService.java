package com.edunext.app.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.edunext.app.repository.AlunoRepository;
import com.edunext.app.repository.GestorRepository;
import com.edunext.app.repository.ProfessorRepository;

@Service
public class AuthenticationService implements UserDetailsService{

    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;
    private final GestorRepository gestorRepository;

    public AuthenticationService(AlunoRepository alunoRepository, ProfessorRepository professorRepository, GestorRepository gestorRepository){
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
        this.gestorRepository = gestorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = alunoRepository.findByMatricula(username).orElse(null);

        if (user == null){
            user = this.professorRepository.findByLogin(username);
        }

        if (user == null){
            user = this.gestorRepository.findByLogin(username);
        }

        if (user == null){
            throw new UsernameNotFoundException(username);
        }

        return user;
    }

}
