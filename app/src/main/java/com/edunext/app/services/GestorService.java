package com.edunext.app.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.edunext.app.dtos.GestorCadastroDTO;
import com.edunext.app.enums.Roles;
import com.edunext.app.model.Gestor;
import com.edunext.app.repository.GestorRepository;

@Service
public class GestorService {

    private final GestorRepository gestorRepository;
    private final PasswordEncoder passwordEncoder;

    public GestorService(GestorRepository gestorRepository, PasswordEncoder passwordEncoder){
        this.gestorRepository = gestorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Gestor cadastrarGestor(GestorCadastroDTO data){
        String senhaCriptografada = passwordEncoder.encode(data.senha());
        Gestor gestor = new Gestor();

        gestor.setNome(data.nome());
        gestor.setLogin(data.login());
        gestor.setPassword(senhaCriptografada);
        gestor.setRole(Roles.GESTOR);
        return this.gestorRepository.save(gestor);
    }

    public List<Gestor> getGestores(){
        List<Gestor> gestores = gestorRepository.findAll();
        return gestores;
    }

    public Gestor getGestor(@NonNull UUID gestorId){
        Optional<Gestor> gestor = gestorRepository.findById(gestorId);

        if (gestor.isEmpty()) return null;

        return gestor.get();
    }

    public Gestor atualizaGestor(GestorCadastroDTO data, @NonNull UUID gestorId){
        Optional<Gestor> gestor = gestorRepository.findById(gestorId);

        if (gestor.isEmpty()) return null;

        Gestor gestorAtualizado = gestor.get();
        gestorAtualizado.setLogin(data.login());
        gestorAtualizado.setNome(data.nome());
        String senhaCriptograda = passwordEncoder.encode(data.senha());
        gestorAtualizado.setPassword(senhaCriptograda);
        return this.gestorRepository.save(gestorAtualizado);
    }

    public Gestor deleteGestor(@NonNull UUID id) {
        Optional<Gestor> gestor = gestorRepository.findById(id);
        if (gestor.isPresent()) {
            gestorRepository.deleteById(id);
            return gestor.get();
        }
        return null;
    }

}
