package com.edunext.app.model;

import java.time.LocalDateTime;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;;

@Entity
@Table(name = "simulados_alunos")
public class SimuladoAluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "simulado_id", nullable = false)
    private Simulado simulado;

    private LocalDateTime dataInicio;
    private LocalDateTime dataSubmissao;

    private double notaFinal;

    @ElementCollection
    @CollectionTable(name = "simulado_aluno_respostas", joinColumns = @JoinColumn(name = "simulado_aluno_id"))
    @MapKeyColumn(name = "questao_id")
    @Column(name = "resposta_dada")
    private Map<Long, String> respostas;

    public SimuladoAluno(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public Simulado getSimulado() {
        return simulado;
    }

    public void setSimulado(Simulado simulado) {
        this.simulado = simulado;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataSubmissao() {
        return dataSubmissao;
    }

    public void setDataSubmissao(LocalDateTime dataSubmissao) {
        this.dataSubmissao = dataSubmissao;
    }

    public double getNotaFinal() {
        return notaFinal;
    }

    public void setNotaFinal(double notaFinal) {
        this.notaFinal = notaFinal;
    }

    public Map<Long, String> getRespostas() {
        return respostas;
    }

    public void setRespostas(Map<Long, String> respostas) {
        this.respostas = respostas;
    }

}
