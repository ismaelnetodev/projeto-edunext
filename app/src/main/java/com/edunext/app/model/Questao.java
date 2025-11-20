package com.edunext.app.model;

import java.util.List;

import com.edunext.app.enums.TipoQuestao;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "questoes")
public class Questao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Lob
    @Column(nullable = false)
    private String enunciado;
    
    private String imagemUrl;

    @ManyToOne
    @JoinColumn(name = "disciplina_id")
    private Disciplina disciplina;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoQuestao tipo;

    @Column(nullable = false)
    private double pontuacao = 1.0;

    @ElementCollection
    private List<String> alternativas;

    @Column(nullable = false)
    private String respostaCorreta;

    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professorCriador;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public String getImagemUrl(){
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl){
        this.imagemUrl = imagemUrl;
    }

    public double getPontuacao(){
        return pontuacao;
    }

    public void setPontuacao(double pontuacao){
        this.pontuacao = pontuacao;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    public TipoQuestao getTipo() {
        return tipo;
    }

    public void setTipo(TipoQuestao tipo) {
        this.tipo = tipo;
    }

    public List<String> getAlternativas() {
        return alternativas;
    }

    public void setAlternativas(List<String> alternativas) {
        this.alternativas = alternativas;
    }

    public String getRespostaCorreta() {
        return respostaCorreta;
    }

    public void setRespostaCorreta(String respostaCorreta) {
        this.respostaCorreta = respostaCorreta;
    }

    public Professor getProfessorCriador() {
        return professorCriador;
    }

    public void setProfessorCriador(Professor professorCriador) {
        this.professorCriador = professorCriador;
    }

}
