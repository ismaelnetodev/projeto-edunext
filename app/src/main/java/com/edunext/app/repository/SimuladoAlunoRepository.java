package com.edunext.app.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edunext.app.model.SimuladoAluno;

@Repository
public interface SimuladoAlunoRepository extends JpaRepository<SimuladoAluno, Long>{
    List<SimuladoAluno> findByAlunoId(UUID alunoId);
    Page<SimuladoAluno> findBySimuladoId(Long simuladoId, Pageable pageable);
    List<SimuladoAluno> findBySimuladoId(Long simuladoId);

    boolean existsByAlunoIdAndSimuladoId(UUID alunoId, Long simuladoId);
}
