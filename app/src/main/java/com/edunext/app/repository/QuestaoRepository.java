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

import com.edunext.app.model.Disciplina;
import com.edunext.app.model.Professor;
import com.edunext.app.model.Questao;

@Repository
public interface QuestaoRepository extends JpaRepository<Questao, Long> {

    @EntityGraph(attributePaths = {"disciplina", "professorCriador"})
    @NonNull
    Page<Questao> findAll(@NonNull Pageable pageable);

    @EntityGraph(attributePaths = {"disciplina", "professorCriador"})
    @Query("SELECT q FROM Questao q WHERE q.id = :id")
    Optional<Questao> findByIdWithRelations(@Param("id") Long id);

    @EntityGraph(attributePaths = {"disciplina", "professorCriador"})
    Page<Questao> findByDisciplinaId(Long disciplinaId, Pageable pageable);

    @EntityGraph(attributePaths = {"disciplina", "professorCriador"})
    Page<Questao> findByProfessorCriadorId(UUID professorId, Pageable pageable);

    List<Questao> findByDisciplinaAndProfessorCriador(Disciplina disciplina, Professor professor);
}
