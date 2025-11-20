package com.edunext.app.model;

import java.util.List;

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
    private List<Turma> turmas;

    public List<Turma> getTurmas(){
        return turmas;
    }

    public void setTurmas(List<Turma> turmas){
        this.turmas = turmas;
    }
    
}
