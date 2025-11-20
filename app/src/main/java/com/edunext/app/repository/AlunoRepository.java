package com.edunext.app.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.edunext.app.model.Aluno;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, UUID>{

    @EntityGraph(attributePaths = {"turma"})
    @NonNull
    Page<Aluno> findAll(@NonNull Pageable pageable);

    @EntityGraph(attributePaths = {"turma"})
    @Query("SELECT a FROM Aluno a WHERE a.id = :id")
    Optional<Aluno> findByIdWithTurma(@Param("id") UUID id);

    Optional<Aluno> findByLogin(String login);
    Optional<Aluno> findByMatricula(String matricula);
    
    @Query("SELECT a FROM Aluno a WHERE UPPER(a.nome) LIKE UPPER(CONCAT(:termoBusca, '%'))")
    List<Aluno> buscaRapidaPorNome(@Param("termoBusca") String termoBusca, Pageable pageable);

}
