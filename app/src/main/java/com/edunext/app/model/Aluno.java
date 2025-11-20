package com.edunext.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Table(name = "alunos")
@Entity
public class Aluno extends User{
    @Column(unique = true)
    private String matricula;

    @ManyToOne
    @JoinColumn(name = "turma_id")
    private Turma turma;

    @Column(nullable = false)
    private boolean senhaTemporaria = true;

    public Aluno(){
        super();
    }

    public Aluno(String matricula, Turma turma){
        this.matricula = matricula;
        this.turma = turma;
        super.setLogin(matricula);
    }

    public String getMatricula(){
        return matricula;
    }

    public void setMatricula(String matricula){
        this.matricula = matricula;
    }

    public Turma getTurma(){
        return turma;
    }

    public void setTurma(Turma turma){
        this.turma = turma;
    }

    public boolean isSenhaTemporaria(){
        return senhaTemporaria;
    }

    public void setSenhaTemporaria(boolean senhaTemporaria){
        this.senhaTemporaria = senhaTemporaria;
    }
}
