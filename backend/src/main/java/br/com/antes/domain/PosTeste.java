package br.com.antes.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "pos_testes")
@org.hibernate.annotations.Immutable
public class PosTeste extends Entidade {
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "experimento_id", nullable = false, unique = true, updatable = false)
    private Experimento experimento;
    @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "situacao_experimento", nullable = false, updatable = false)
    private SituacaoPosTeste situacaoExperimento;
    @Column(name = "motivo_se_interrompeu_ou_nao_iniciou", nullable = true, updatable = false, length = 4000)
    private String motivoSeInterrompeuOuNaoIniciou;
    @Column(name = "data_interrupcao", nullable = true, updatable = false)
    private LocalDate dataInterrupcao;
    @Column(name = "sessoes_realizadas", nullable = false, updatable = false)
    private Integer sessoesRealizadas;
    @Column(name = "tempo_realizado_minutos", nullable = false, updatable = false)
    private Integer tempoRealizadoMinutos;
    @Column(name = "custo_realizado", nullable = false, updatable = false, precision = 12, scale = 2)
    private BigDecimal custoRealizado;
    @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "dificuldade_percebida", nullable = false, updatable = false)
    private AvaliacaoPercebida dificuldadePercebida;
    @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "satisfacao_percebida", nullable = false, updatable = false)
    private AvaliacaoPercebida satisfacaoPercebida;
    @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "obstaculo_encontrado", nullable = false, updatable = false)
    private Obstaculo obstaculoEncontrado;
    @Column(name = "descricao_outro", nullable = true, updatable = false, length = 2000)
    private String descricaoOutro;
    @Column(name = "relato_exemplo", nullable = true, updatable = false, length = 4000)
    private String relatoExemplo;
    @Column(name = "seguranca_decidir_depois", nullable = false, updatable = false)
    private Integer segurancaDecidirDepois;
    @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "proximo_passo", nullable = false, updatable = false)
    private Intencao proximoPasso;
    @Column(name = "o_que_pesou_na_escolha", nullable = true, updatable = false, length = 4000)
    private String oQuePesouNaEscolha;
    @Column(name = "contribuicao_percebida_app", nullable = true, updatable = false, length = 4000)
    private String contribuicaoPercebidaApp;
    @Column(name = "contexto_mudancas", nullable = true, updatable = false, length = 4000)
    private String contextoMudancas;
    @Column(name = "data_hora_resposta", nullable = false, updatable = false)
    private Instant dataHoraResposta;

    protected PosTeste() {}
    public PosTeste(Experimento experimento, SituacaoPosTeste situacaoExperimento, String motivoSeInterrompeuOuNaoIniciou, LocalDate dataInterrupcao, Integer sessoesRealizadas, Integer tempoRealizadoMinutos, BigDecimal custoRealizado, AvaliacaoPercebida dificuldadePercebida, AvaliacaoPercebida satisfacaoPercebida, Obstaculo obstaculoEncontrado, String descricaoOutro, String relatoExemplo, Integer segurancaDecidirDepois, Intencao proximoPasso, String oQuePesouNaEscolha, String contribuicaoPercebidaApp, String contextoMudancas, Instant dataHoraResposta) {
        this.experimento = experimento;
        this.situacaoExperimento = situacaoExperimento;
        this.motivoSeInterrompeuOuNaoIniciou = motivoSeInterrompeuOuNaoIniciou;
        this.dataInterrupcao = dataInterrupcao;
        this.sessoesRealizadas = sessoesRealizadas;
        this.tempoRealizadoMinutos = tempoRealizadoMinutos;
        this.custoRealizado = custoRealizado;
        this.dificuldadePercebida = dificuldadePercebida;
        this.satisfacaoPercebida = satisfacaoPercebida;
        this.obstaculoEncontrado = obstaculoEncontrado;
        this.descricaoOutro = descricaoOutro;
        this.relatoExemplo = relatoExemplo;
        this.segurancaDecidirDepois = segurancaDecidirDepois;
        this.proximoPasso = proximoPasso;
        this.oQuePesouNaEscolha = oQuePesouNaEscolha;
        this.contribuicaoPercebidaApp = contribuicaoPercebidaApp;
        this.contextoMudancas = contextoMudancas;
        this.dataHoraResposta = dataHoraResposta;
    }
    public Experimento getExperimento() { return experimento; }
    public SituacaoPosTeste getSituacaoExperimento() { return situacaoExperimento; }
    public String getMotivoSeInterrompeuOuNaoIniciou() { return motivoSeInterrompeuOuNaoIniciou; }
    public LocalDate getDataInterrupcao() { return dataInterrupcao; }
    public Integer getSessoesRealizadas() { return sessoesRealizadas; }
    public Integer getTempoRealizadoMinutos() { return tempoRealizadoMinutos; }
    public BigDecimal getCustoRealizado() { return custoRealizado; }
    public AvaliacaoPercebida getDificuldadePercebida() { return dificuldadePercebida; }
    public AvaliacaoPercebida getSatisfacaoPercebida() { return satisfacaoPercebida; }
    public Obstaculo getObstaculoEncontrado() { return obstaculoEncontrado; }
    public String getDescricaoOutro() { return descricaoOutro; }
    public String getRelatoExemplo() { return relatoExemplo; }
    public Integer getSegurancaDecidirDepois() { return segurancaDecidirDepois; }
    public Intencao getProximoPasso() { return proximoPasso; }
    public String getOQuePesouNaEscolha() { return oQuePesouNaEscolha; }
    public String getContribuicaoPercebidaApp() { return contribuicaoPercebidaApp; }
    public String getContextoMudancas() { return contextoMudancas; }
    public Instant getDataHoraResposta() { return dataHoraResposta; }
}
