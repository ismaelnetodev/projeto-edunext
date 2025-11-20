package com.edunext.app.model;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "simulados")
public class Simulado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataInicioDisponivel;
    private LocalDateTime dataFimDisponivel;

    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    @ManyToOne
    @JoinColumn(name = "turma_id", nullable = false)
    private Turma turma;

    @ManyToMany
    @JoinTable(
        name = "simulado_questao",
        joinColumns = @JoinColumn(name = "simulado_id"),
        inverseJoinColumns = @JoinColumn(name = "questao_id")
    )
    private List<Questao> questoes;

    @ManyToOne
    @JoinColumn(name = "disciplina_id", nullable = false)
    private Disciplina disciplina;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataInicioDisponivel() {
        return dataInicioDisponivel;
    }

    public void setDataInicioDisponivel(LocalDateTime dataInicioDisponivel) {
        this.dataInicioDisponivel = dataInicioDisponivel;
    }

    public LocalDateTime getDataFimDisponivel() {
        return dataFimDisponivel;
    }

    public void setDataFimDisponivel(LocalDateTime dataFimDisponivel) {
        this.dataFimDisponivel = dataFimDisponivel;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    public List<Questao> getQuestoes() {
        return questoes;
    }

    public void setQuestoes(List<Questao> questoes) {
        this.questoes = questoes;
    }

    public Disciplina getDisciplina(){
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina){
        this.disciplina = disciplina;
    }

}
