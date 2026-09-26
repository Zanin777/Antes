package br.com.antes.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "decisoes")
public class Decisao extends Entidade {
    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;
    @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "categoria", nullable = false)
    private Categoria categoria;
    @Column(name = "motivacao", nullable = false, length = 4000)
    private String motivacao;
    @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "experiencia_anterior", nullable = false)
    private ExperienciaAnterior experienciaAnterior;
    @Column(name = "participante_codigo", nullable = false, length = 64)
    private String participanteCodigo;

    protected Decisao() {}
    public Decisao(String titulo, Categoria categoria, String motivacao, ExperienciaAnterior experienciaAnterior, String participanteCodigo) {
        this.titulo = titulo;
        this.categoria = categoria;
        this.motivacao = motivacao;
        this.experienciaAnterior = experienciaAnterior;
        this.participanteCodigo = participanteCodigo;
    }
    public String getTitulo() { return titulo; }
    public Categoria getCategoria() { return categoria; }
    public String getMotivacao() { return motivacao; }
    public ExperienciaAnterior getExperienciaAnterior() { return experienciaAnterior; }
    public String getParticipanteCodigo() { return participanteCodigo; }
}
