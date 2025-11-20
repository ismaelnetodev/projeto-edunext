package com.edunext.app.model;

import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "professores")
public class Professor extends User{
    @ManyToMany
    @JoinTable(
        name = "professor_turma",
        joinColumns = @JoinColumn(name = "professor_id"),
        inverseJoinColumns = @JoinColumn(name = "turma_id")
    )
    private Set<Turma> turmas;

    public Set<Turma> getTurmas(){
        return turmas;
    }

    public void setTurmas(Set<Turma> turmas){
        this.turmas = turmas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Professor professor = (Professor) o;
        return Objects.equals(getId(), professor.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
    
}
