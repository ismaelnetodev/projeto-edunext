package com.edunext.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.edunext.app.model.Turma;

@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long>{

    @NonNull
    Page<Turma> findAll(@NonNull Pageable pageable);

    @EntityGraph(attributePaths = {"professores", "alunos"})
    @Query("SELECT t FROM Turma t WHERE t.id = :id")
    Optional<Turma> findByIdWithRelations(@Param("id") Long id);

    @EntityGraph(attributePaths = {"professores", "alunos"})
    @Query("SELECT t FROM Turma t WHERE UPPER(t.nome) LIKE UPPER(CONCAT(:termoBusca, '%'))")
    List<Turma> buscaRapidaPorNome(@Param("termoBusca") String termoBusca, Pageable pageable);

    boolean existsByNomeAndAnoLetivo(String nome, int anoLetivo);

 

}
