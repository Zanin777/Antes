package br.com.antes.domain;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Embeddable
public class TemplateSnapshot {
    @Column(name = "template_codigo", nullable = false, updatable = false, length = 10)
    private String codigo;
    @Column(name = "template_versao", nullable = false, updatable = false)
    private Integer versao;
    @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "template_categoria", nullable = false, updatable = false)
    private Categoria categoria;
    @Column(name = "template_titulo", nullable = false, updatable = false, length = 200)
    private String titulo;
    @Column(name = "template_pergunta_decisao", nullable = false, updatable = false, length = 1000)
    private String perguntaDecisao;
    @Column(name = "template_duracao_dias", nullable = false, updatable = false)
    private Integer duracaoDias;
    @Column(name = "template_sessoes_sugeridas", nullable = false, updatable = false)
    private Integer sessoesSugeridas;
    @Column(name = "template_minutos_por_sessao_sugerido", nullable = false, updatable = false)
    private Integer minutosPorSessaoSugerido;
    @Column(name = "template_descricao", nullable = false, updatable = false, length = 10000)
    private String descricao;
    protected TemplateSnapshot() {}
    public static TemplateSnapshot copiar(Template template) {
        var snapshot = new TemplateSnapshot();
        snapshot.codigo = template.getCodigo();
        snapshot.versao = template.getVersao();
        snapshot.categoria = template.getCategoria();
        snapshot.titulo = template.getTitulo();
        snapshot.perguntaDecisao = template.getPerguntaDecisao();
        snapshot.duracaoDias = template.getDuracaoDias();
        snapshot.sessoesSugeridas = template.getSessoesSugeridas();
        snapshot.minutosPorSessaoSugerido = template.getMinutosPorSessaoSugerido();
        snapshot.descricao = template.getDescricao();
        return snapshot;
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
