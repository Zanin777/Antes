package br.com.antes.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "pre_testes")
@org.hibernate.annotations.Immutable
public class PreTeste extends Entidade {
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "experimento_id", nullable = false, unique = true, updatable = false)
    private Experimento experimento;
    @Column(name = "sessoes_esperadas", nullable = false, updatable = false)
    private Integer sessoesEsperadas;
    @Column(name = "tempo_esperado_minutos", nullable = false, updatable = false)
    private Integer tempoEsperadoMinutos;
    @Column(name = "gasto_esperado", nullable = false, updatable = false, precision = 12, scale = 2)
    private BigDecimal gastoEsperado;
    @Column(name = "dificuldade_esperada", nullable = false, updatable = false)
    private Integer dificuldadeEsperada;
    @Column(name = "satisfacao_esperada", nullable = false, updatable = false)
    private Integer satisfacaoEsperada;
    @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "obstaculo_principal_esperado", nullable = false, updatable = false)
    private Obstaculo obstaculoPrincipalEsperado;
    @Column(name = "descricao_outro", nullable = true, updatable = false, length = 2000)
    private String descricaoOutro;
    @Column(name = "como_obstaculo_apareceria", nullable = true, updatable = false, length = 4000)
    private String comoObstaculoApareceria;
    @Column(name = "seguranca_decidir_antes", nullable = false, updatable = false)
    private Integer segurancaDecidirAntes;
    @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "intencao_antes", nullable = false, updatable = false)
    private Intencao intencaoAntes;
    @Column(name = "data_hora_resposta", nullable = false, updatable = false)
    private Instant dataHoraResposta;
    @Column(name = "descricao_plano_na_resposta", nullable = false, updatable = false, length = 10000)
    private String descricaoPlanoNaResposta;
    @Column(name = "material_na_resposta", nullable = true, updatable = false, length = 2000)
    private String materialNaResposta;
    @Column(name = "data_inicio_na_resposta", nullable = false, updatable = false)
    private LocalDate dataInicioNaResposta;
    @Column(name = "data_fim_na_resposta", nullable = false, updatable = false)
    private LocalDate dataFimNaResposta;

    protected PreTeste() {}
    public PreTeste(Experimento experimento, Integer sessoesEsperadas, Integer tempoEsperadoMinutos, BigDecimal gastoEsperado, Integer dificuldadeEsperada, Integer satisfacaoEsperada, Obstaculo obstaculoPrincipalEsperado, String descricaoOutro, String comoObstaculoApareceria, Integer segurancaDecidirAntes, Intencao intencaoAntes, Instant dataHoraResposta, String descricaoPlanoNaResposta, String materialNaResposta, LocalDate dataInicioNaResposta, LocalDate dataFimNaResposta) {
        this.experimento = experimento;
        this.sessoesEsperadas = sessoesEsperadas;
        this.tempoEsperadoMinutos = tempoEsperadoMinutos;
        this.gastoEsperado = gastoEsperado;
        this.dificuldadeEsperada = dificuldadeEsperada;
        this.satisfacaoEsperada = satisfacaoEsperada;
        this.obstaculoPrincipalEsperado = obstaculoPrincipalEsperado;
        this.descricaoOutro = descricaoOutro;
        this.comoObstaculoApareceria = comoObstaculoApareceria;
        this.segurancaDecidirAntes = segurancaDecidirAntes;
        this.intencaoAntes = intencaoAntes;
        this.dataHoraResposta = dataHoraResposta;
        this.descricaoPlanoNaResposta = descricaoPlanoNaResposta;
        this.materialNaResposta = materialNaResposta;
        this.dataInicioNaResposta = dataInicioNaResposta;
        this.dataFimNaResposta = dataFimNaResposta;
    }
    public Experimento getExperimento() { return experimento; }
    public Integer getSessoesEsperadas() { return sessoesEsperadas; }
    public Integer getTempoEsperadoMinutos() { return tempoEsperadoMinutos; }
    public BigDecimal getGastoEsperado() { return gastoEsperado; }
    public Integer getDificuldadeEsperada() { return dificuldadeEsperada; }
    public Integer getSatisfacaoEsperada() { return satisfacaoEsperada; }
    public Obstaculo getObstaculoPrincipalEsperado() { return obstaculoPrincipalEsperado; }
    public String getDescricaoOutro() { return descricaoOutro; }
    public String getComoObstaculoApareceria() { return comoObstaculoApareceria; }
    public Integer getSegurancaDecidirAntes() { return segurancaDecidirAntes; }
    public Intencao getIntencaoAntes() { return intencaoAntes; }
    public Instant getDataHoraResposta() { return dataHoraResposta; }
    public String getDescricaoPlanoNaResposta() { return descricaoPlanoNaResposta; }
    public String getMaterialNaResposta() { return materialNaResposta; }
    public LocalDate getDataInicioNaResposta() { return dataInicioNaResposta; }
    public LocalDate getDataFimNaResposta() { return dataFimNaResposta; }
}
