package com.edunext.app.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import com.edunext.app.model.Gestor;

@Repository
public interface GestorRepository extends JpaRepository<Gestor, UUID>{

    public UserDetails findByLogin(String login);
}
