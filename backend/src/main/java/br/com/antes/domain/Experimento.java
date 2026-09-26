package br.com.antes.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "experimentos")
public class Experimento extends Entidade {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "decisao_id", nullable = false)
    private Decisao decisao;
    @Embedded
    private TemplateSnapshot templateSnapshot;
    @Column(name = "descricao_final_editada", nullable = false, length = 10000)
    private String descricaoFinalEditada;
    @Column(name = "material_provedor_formato", nullable = true, length = 2000)
    private String materialProvedorFormato;
    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;
    @Column(name = "data_fim_prevista", nullable = false)
    private LocalDate dataFimPrevista;
    @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "situacao", nullable = false)
    private SituacaoExperimento situacao;

    protected Experimento() {}
    public Experimento(Decisao decisao, TemplateSnapshot templateSnapshot, String descricaoFinalEditada, String materialProvedorFormato, LocalDate dataInicio, LocalDate dataFimPrevista, SituacaoExperimento situacao) {
        this.decisao = decisao;
        this.templateSnapshot = templateSnapshot;
        this.descricaoFinalEditada = descricaoFinalEditada;
        this.materialProvedorFormato = materialProvedorFormato;
        this.dataInicio = dataInicio;
        this.dataFimPrevista = dataFimPrevista;
        this.situacao = situacao;
    }
    public Decisao getDecisao() { return decisao; }
    public TemplateSnapshot getTemplateSnapshot() { return templateSnapshot; }
    public String getDescricaoFinalEditada() { return descricaoFinalEditada; }
    public String getMaterialProvedorFormato() { return materialProvedorFormato; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFimPrevista() { return dataFimPrevista; }
    public SituacaoExperimento getSituacao() { return situacao; }

    @Version private long versaoRegistro;
    public void ajustarPlano(String descricao, String material, LocalDate inicio) {
        if (situacao != SituacaoExperimento.PLANEJADO) throw new IllegalStateException("Plano já iniciado");
        descricaoFinalEditada = descricao; materialProvedorFormato = material;
        dataInicio = inicio; dataFimPrevista = inicio.plusDays(6);
    }
    public void iniciar() { situacao = SituacaoExperimento.EM_ANDAMENTO; }
    public void encerrar(SituacaoPosTeste resultado) {
        situacao = switch (resultado) {
            case A, B -> SituacaoExperimento.CONCLUIDO;
            case C -> SituacaoExperimento.INTERROMPIDO;
            case D -> SituacaoExperimento.NAO_INICIADO;
        };
    }
    public boolean encerrado() { return situacao == SituacaoExperimento.CONCLUIDO || situacao == SituacaoExperimento.INTERROMPIDO || situacao == SituacaoExperimento.NAO_INICIADO; }
}
