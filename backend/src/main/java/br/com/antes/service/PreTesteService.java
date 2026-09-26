package br.com.antes.service;

import br.com.antes.api.ApiDtos.*;
import br.com.antes.domain.*;
import br.com.antes.repository.PreTesteRepository;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PreTesteService {
    private final ExperimentoService experimentos;
    private final PreTesteRepository preTestes;
    private final Clock clock;
    public PreTesteService(ExperimentoService experimentos,PreTesteRepository preTestes,Clock clock) { this.experimentos=experimentos;this.preTestes=preTestes;this.clock=clock; }
    @Transactional
    public PreTesteResposta criar(Long id,PreTesteDados r) {
        var e=experimentos.obterBloqueado(id);
        if(preTestes.existsByExperimentoId(id)) throw RegraNegocioException.conflito("O pré-teste já foi respondido e não pode ser reescrito");
        if(e.getSituacao()!=SituacaoExperimento.PLANEJADO) throw RegraNegocioException.conflito("Responda o pré-teste antes de iniciar o experimento");
        validarOutro(r.obstaculoPrincipalEsperado(),r.descricaoOutro());
        var p=new PreTeste(e,r.sessoesEsperadas(),r.tempoEsperadoMinutos(),r.gastoEsperado(),r.dificuldadeEsperada(),r.satisfacaoEsperada(),r.obstaculoPrincipalEsperado(),r.descricaoOutro(),r.comoObstaculoApareceria(),r.segurancaDecidirAntes(),r.intencaoAntes(),clock.instant(),e.getDescricaoFinalEditada(),e.getMaterialProvedorFormato(),e.getDataInicio(),e.getDataFimPrevista());
        return PreTesteResposta.de(preTestes.save(p));
    }
    @Transactional(readOnly=true)
    public PreTesteResposta consultar(Long id) {
        experimentos.obter(id);
        return PreTesteResposta.de(preTestes.findByExperimentoId(id).orElseThrow(()->RegraNegocioException.ausente("Pré-teste")));
    }
    static void validarOutro(Obstaculo obstaculo,String descricao) {
        if(obstaculo==Obstaculo.O9 && (descricao==null||descricao.isBlank())) throw RegraNegocioException.invalida("Descreva o obstáculo quando selecionar O9 (Outro)");
    }
}
