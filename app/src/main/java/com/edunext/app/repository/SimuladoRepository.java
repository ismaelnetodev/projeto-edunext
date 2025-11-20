package com.edunext.app.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.edunext.app.model.Simulado;

@Repository
public interface SimuladoRepository extends JpaRepository<Simulado, Long>{

    @EntityGraph(attributePaths = {"questoes", "turma", "professor", "disciplina"})
    @Query("SELECT s FROM Simulado s WHERE s.id = :id")
    Optional<Simulado> findByIdWithQuestoes(@Param("id") Long id);

    @EntityGraph(attributePaths = {"questoes", "professor", "disciplina"})
    List<Simulado> findByTurmaId(Long turmaId);

    @EntityGraph(attributePaths = {"questoes", "turma", "disciplina"})
    List<Simulado> findByProfessorId(UUID professorId);
}
