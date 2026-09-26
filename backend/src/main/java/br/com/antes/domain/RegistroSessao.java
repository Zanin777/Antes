package br.com.antes.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "registros_sessao")
@org.hibernate.annotations.Immutable
public class RegistroSessao extends Entidade {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "experimento_id", nullable = false, updatable = false)
    private Experimento experimento;
    @Column(name = "data", nullable = false, updatable = false)
    private LocalDate data;
    @Column(name = "minutos_dedicados", nullable = false, updatable = false)
    private Integer minutosDedicados;
    @Column(name = "gasto_adicional", nullable = false, updatable = false, precision = 12, scale = 2)
    private BigDecimal gastoAdicional;
    @Column(name = "nota_opcional", nullable = true, updatable = false, length = 4000)
    private String notaOpcional;

    protected RegistroSessao() {}
    public RegistroSessao(Experimento experimento, LocalDate data, Integer minutosDedicados, BigDecimal gastoAdicional, String notaOpcional) {
        this.experimento = experimento;
        this.data = data;
        this.minutosDedicados = minutosDedicados;
        this.gastoAdicional = gastoAdicional;
        this.notaOpcional = notaOpcional;
    }
    public Experimento getExperimento() { return experimento; }
    public LocalDate getData() { return data; }
    public Integer getMinutosDedicados() { return minutosDedicados; }
    public BigDecimal getGastoAdicional() { return gastoAdicional; }
    public String getNotaOpcional() { return notaOpcional; }
}
