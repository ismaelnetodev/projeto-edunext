package com.edunext.app.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "avaliacoes_qualitativas")
public class AvaliacaoQualitativa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    @Column(nullable = false)
    private LocalDateTime dataAvaliacao;

    private double assiduidade;
    private double participacao;
    private double responsabilidade;
    private double sociabilidade;

    @Lob
    private String observacao;

    public Long getId(){
        return this.id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Aluno getAluno(){
        return this.aluno;
    }

    public void setAluno(Aluno aluno){
        this.aluno = aluno;
    }

    public Professor getProfessor(){
        return this.professor;
    }

    public void setProfessor(Professor professor){
        this.professor = professor;
    }

    public LocalDateTime getDataAvaliacao(){
        return this.dataAvaliacao;
    }

    public void setDataAvaliacao(LocalDateTime dataAvaliacao){
        this.dataAvaliacao = dataAvaliacao;
    }

    public double getAssiduidade(){
        return this.assiduidade;
    }

    public void setAssiduidade(double assiduidade){
        this.assiduidade = assiduidade;
    }

    public double getParticipacao(){
        return this.participacao;
    }

    public void setParticipacao(double participacao){
        this.participacao = participacao;
    }

    public double getResponsabilidade(){
        return this.responsabilidade;
    }

    public void setResponsabilidade(double responsabilidade){
        this.responsabilidade = responsabilidade;
    }

    public double getSociabilidade(){
        return this.sociabilidade;
    }

    public void setSociabilidade(double sociabilidade){
        this.sociabilidade = sociabilidade;
    }

    public String getObservacao(){
        return this.observacao;
    }

    public void setObservacao(String observacao){
        this.observacao = observacao;
    }
}
