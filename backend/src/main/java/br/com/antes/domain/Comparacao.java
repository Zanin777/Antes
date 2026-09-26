package br.com.antes.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "comparacoes")
@org.hibernate.annotations.Immutable
public class Comparacao extends Entidade {
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "experimento_id", nullable = false, unique = true, updatable = false)
    private Experimento experimento;
    @Column(name = "diferenca_sessoes", nullable = false, updatable = false)
    private Integer diferencaSessoes;
    @Column(name = "diferenca_tempo_minutos", nullable = false, updatable = false)
    private Integer diferencaTempoMinutos;
    @Column(name = "diferenca_gasto", nullable = false, updatable = false, precision = 12, scale = 2)
    private BigDecimal diferencaGasto;
    @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "transicao_dificuldade", nullable = false, updatable = false)
    private TransicaoEscala transicaoDificuldade;
    @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "transicao_satisfacao", nullable = false, updatable = false)
    private TransicaoEscala transicaoSatisfacao;
    @Column(name = "obstaculo_mudou", nullable = false, updatable = false)
    private Boolean obstaculoMudou;
    @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "intencao_antes", nullable = false, updatable = false)
    private Intencao intencaoAntes;
    @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "intencao_depois", nullable = false, updatable = false)
    private Intencao intencaoDepois;
    @Column(name = "mudanca_seguranca", nullable = false, updatable = false)
    private Integer mudancaSeguranca;

    protected Comparacao() {}
    public Comparacao(Experimento experimento, Integer diferencaSessoes, Integer diferencaTempoMinutos, BigDecimal diferencaGasto, TransicaoEscala transicaoDificuldade, TransicaoEscala transicaoSatisfacao, Boolean obstaculoMudou, Intencao intencaoAntes, Intencao intencaoDepois, Integer mudancaSeguranca) {
        this.experimento = experimento;
        this.diferencaSessoes = diferencaSessoes;
        this.diferencaTempoMinutos = diferencaTempoMinutos;
        this.diferencaGasto = diferencaGasto;
        this.transicaoDificuldade = transicaoDificuldade;
        this.transicaoSatisfacao = transicaoSatisfacao;
        this.obstaculoMudou = obstaculoMudou;
        this.intencaoAntes = intencaoAntes;
        this.intencaoDepois = intencaoDepois;
        this.mudancaSeguranca = mudancaSeguranca;
    }
    public Experimento getExperimento() { return experimento; }
    public Integer getDiferencaSessoes() { return diferencaSessoes; }
    public Integer getDiferencaTempoMinutos() { return diferencaTempoMinutos; }
    public BigDecimal getDiferencaGasto() { return diferencaGasto; }
    public TransicaoEscala getTransicaoDificuldade() { return transicaoDificuldade; }
    public TransicaoEscala getTransicaoSatisfacao() { return transicaoSatisfacao; }
    public Boolean getObstaculoMudou() { return obstaculoMudou; }
    public Intencao getIntencaoAntes() { return intencaoAntes; }
    public Intencao getIntencaoDepois() { return intencaoDepois; }
    public Integer getMudancaSeguranca() { return mudancaSeguranca; }
}
