package com.educamais.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.educamais.app.model.Turma;

@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long>{

    @EntityGraph(attributePaths = {"professores", "alunos"})
    Page<Turma> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"professores", "alunos"})
    @Query("SELECT t FROM Turma t WHERE t.id = :id")
    Optional<Turma> findByIdWithRelations(@Param("id") Long id);    

    @Query("SELECT DISTINCT t FROM Turma t " +
           "LEFT JOIN FETCH t.professores " +
           "LEFT JOIN FETCH t.alunos")
    List<Turma> findAllWithRelations();

    boolean existsByNomeAndAnoLetivo(String nome, int anoLetivo);

    @Query("SELECT a FROM Turma a WHERE UPPER(a.nome) LIKE UPPER(CONCAT(:termoBusca, '%'))")
    List<Turma> buscaRapidaPorNome(@Param("termoBusca") String termoBusca, Pageable pageable);

}
