package br.com.antes.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "templates")
@org.hibernate.annotations.Immutable
public class Template extends Entidade {
    @Column(name = "codigo", nullable = false, updatable = false, length = 10)
    private String codigo;
    @Column(name = "versao", nullable = false, updatable = false)
    private Integer versao;
    @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "categoria", nullable = false, updatable = false)
    private Categoria categoria;
    @Column(name = "titulo", nullable = false, updatable = false, length = 200)
    private String titulo;
    @Column(name = "pergunta_decisao", nullable = false, updatable = false, length = 1000)
    private String perguntaDecisao;
    @Column(name = "duracao_dias", nullable = false, updatable = false)
    private Integer duracaoDias;
    @Column(name = "sessoes_sugeridas", nullable = false, updatable = false)
    private Integer sessoesSugeridas;
    @Column(name = "minutos_por_sessao_sugerido", nullable = false, updatable = false)
    private Integer minutosPorSessaoSugerido;
    @Column(name = "descricao", nullable = false, updatable = false, length = 10000)
    private String descricao;

    protected Template() {}
    public Template(String codigo, Integer versao, Categoria categoria, String titulo, String perguntaDecisao, Integer duracaoDias, Integer sessoesSugeridas, Integer minutosPorSessaoSugerido, String descricao) {
        this.codigo = codigo;
        this.versao = versao;
        this.categoria = categoria;
        this.titulo = titulo;
        this.perguntaDecisao = perguntaDecisao;
        this.duracaoDias = duracaoDias;
        this.sessoesSugeridas = sessoesSugeridas;
        this.minutosPorSessaoSugerido = minutosPorSessaoSugerido;
        this.descricao = descricao;
    }
    public String getCodigo() { return codigo; }
    public Integer getVersao() { return versao; }
    public Categoria getCategoria() { return categoria; }
    public String getTitulo() { return titulo; }
    public String getPerguntaDecisao() { return perguntaDecisao; }
    public Integer getDuracaoDias() { return duracaoDias; }
    public Integer getSessoesSugeridas() { return sessoesSugeridas; }
    public Integer getMinutosPorSessaoSugerido() { return minutosPorSessaoSugerido; }
    public String getDescricao() { return descricao; }
}
