package com.edunext.app.model;

import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "turmas")
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private int anoLetivo;

    @OneToMany(mappedBy = "turma")
    private Set<Aluno> alunos;

    @ManyToMany(mappedBy = "turmas")
    private Set<Professor> professores;

    public Set<Professor> getProfessores() {
        return professores;
    }

    public void setProfessores(Set<Professor> professores) {
        this.professores = professores;
    }

    public Turma(){}

    public Turma(String nome, int anoLetivo){
        this.nome = nome;
        this.anoLetivo = anoLetivo;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getNome(){
        return nome;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public int getAnoLetivo(){
        return anoLetivo;
    }

    public void setAnoLetivo(int anoLetivo){
        this.anoLetivo = anoLetivo;
    }

    public Set<Aluno> getAlunos(){
        return alunos;
    }

    public void setAlunos(Set<Aluno> alunos){
        this.alunos = alunos;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Turma turma = (Turma) o;
        return Objects.equals(id, turma.id);
    }

    @Override
    public int hashCode(){
        return Objects.hash(id);
    }
    
}
