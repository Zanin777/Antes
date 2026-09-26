package br.com.antes.service;

import br.com.antes.api.ApiDtos.ComparacaoResposta;
import br.com.antes.domain.*;
import br.com.antes.repository.ComparacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ComparacaoService {
    private final ComparacaoRepository comparacoes;
    private final ExperimentoService experimentos;
    public ComparacaoService(ComparacaoRepository comparacoes,ExperimentoService experimentos) { this.comparacoes=comparacoes;this.experimentos=experimentos; }
    public Comparacao calcular(PreTeste pre,PosTeste pos) {
        return new Comparacao(pos.getExperimento(),pos.getSessoesRealizadas()-pre.getSessoesEsperadas(),pos.getTempoRealizadoMinutos()-pre.getTempoEsperadoMinutos(),pos.getCustoRealizado().subtract(pre.getGastoEsperado()),transicao(pre.getDificuldadeEsperada(),pos.getDificuldadePercebida(),false),transicao(pre.getSatisfacaoEsperada(),pos.getSatisfacaoPercebida(),true),pre.getObstaculoPrincipalEsperado()!=pos.getObstaculoEncontrado(),pre.getIntencaoAntes(),pos.getProximoPasso(),pos.getSegurancaDecidirDepois()-pre.getSegurancaDecidirAntes());
    }
    static TransicaoEscala transicao(int esperado,AvaliacaoPercebida realidade,boolean maiorMelhor) {
        if(!realidade.aplicavel()) return TransicaoEscala.NAO_SE_APLICA;
        if(esperado==realidade.nota()) return TransicaoEscala.MANTEVE;
        return (realidade.nota()>esperado)==maiorMelhor?TransicaoEscala.MELHOROU:TransicaoEscala.PIOROU;
    }
    @Transactional(readOnly=true)
    public ComparacaoResposta consultar(Long id) {
        experimentos.obter(id);
        return ComparacaoResposta.de(comparacoes.findByExperimentoId(id).orElseThrow(()->RegraNegocioException.conflito("A comparação só está disponível depois do pós-teste")));
    }
}
