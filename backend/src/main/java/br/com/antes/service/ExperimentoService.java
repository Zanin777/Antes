package br.com.antes.service;

import br.com.antes.api.ApiDtos.*;
import br.com.antes.domain.*;
import br.com.antes.repository.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExperimentoService {
    private final DecisaoRepository decisoes;
    private final TemplateRepository templates;
    private final ExperimentoRepository experimentos;
    public ExperimentoService(DecisaoRepository decisoes,TemplateRepository templates,ExperimentoRepository experimentos) {
        this.decisoes=decisoes; this.templates=templates; this.experimentos=experimentos;
    }
    @Transactional
    public ExperimentoResposta criar(CriarExperimento r) {
        var decisao=decisoes.findById(r.decisaoId()).orElseThrow(()->RegraNegocioException.ausente("Decisão"));
        var template=(r.templateVersao()==null?templates.findFirstByCodigoOrderByVersaoDesc(r.templateCodigo()):templates.findByCodigoAndVersao(r.templateCodigo(),r.templateVersao()))
            .orElseThrow(()->RegraNegocioException.ausente("Template"));
        if(decisao.getCategoria()!=template.getCategoria()) throw RegraNegocioException.invalida("A categoria da decisão não corresponde à categoria do template");
        var descricao=r.descricaoFinalEditada()==null||r.descricaoFinalEditada().isBlank()?template.getDescricao():r.descricaoFinalEditada().strip();
        var experimento=new Experimento(decisao,TemplateSnapshot.copiar(template),descricao,r.materialProvedorFormato(),r.dataInicio(),r.dataInicio().plusDays(6),SituacaoExperimento.PLANEJADO);
        return ExperimentoResposta.de(experimentos.save(experimento));
    }
    @Transactional
    public ExperimentoResposta ajustar(Long id,AjustarPlano r) {
        var e=obterBloqueado(id);
        if(e.getSituacao()!=SituacaoExperimento.PLANEJADO) throw RegraNegocioException.conflito("O plano só pode ser ajustado enquanto PLANEJADO");
        e.ajustarPlano(r.descricaoFinalEditada().strip(),r.materialProvedorFormato(),r.dataInicio());
        return ExperimentoResposta.de(e);
    }
    @Transactional(readOnly=true)
    public ExperimentoResposta consultar(Long id) { return ExperimentoResposta.de(obter(id)); }
    @Transactional(readOnly=true)
    public List<ExperimentoResposta> listar(Long decisaoId) {
        if(!decisoes.existsById(decisaoId)) throw RegraNegocioException.ausente("Decisão");
        return experimentos.findAllByDecisaoIdOrderByIdAsc(decisaoId).stream().map(ExperimentoResposta::de).toList();
    }
    @Transactional(readOnly=true)
    public List<TemplateResposta> catalogo() { return templates.findAllByOrderByCodigoAscVersaoDesc().stream().map(TemplateResposta::de).toList(); }

    public Experimento obter(Long id) { return experimentos.findById(id).orElseThrow(()->RegraNegocioException.ausente("Experimento")); }
    // Chamado dentro da transação do service que está gravando. Serializa pré, pós e diário por experimento.
    public Experimento obterBloqueado(Long id) { return experimentos.buscarComBloqueio(id).orElseThrow(()->RegraNegocioException.ausente("Experimento")); }
}
