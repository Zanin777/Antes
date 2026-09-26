package br.com.antes.service;

import br.com.antes.api.ApiDtos.*;
import br.com.antes.domain.*;
import br.com.antes.repository.*;
import java.time.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PosTesteService {
    private final ExperimentoService experimentos;
    private final PreTesteRepository preTestes;
    private final PosTesteRepository posTestes;
    private final ComparacaoRepository comparacoes;
    private final ComparacaoService calculadora;
    private final Clock clock;
    public PosTesteService(ExperimentoService experimentos,PreTesteRepository preTestes,PosTesteRepository posTestes,ComparacaoRepository comparacoes,ComparacaoService calculadora,Clock clock) {
        this.experimentos=experimentos;this.preTestes=preTestes;this.posTestes=posTestes;this.comparacoes=comparacoes;this.calculadora=calculadora;this.clock=clock;
    }
    @Transactional
    public PosTesteResposta criar(Long id,PosTesteDados r) {
        var e=experimentos.obterBloqueado(id);
        if(e.encerrado()||posTestes.existsByExperimentoId(id)) throw RegraNegocioException.conflito("O pós-teste já foi respondido; o experimento está encerrado");
        var pre=preTestes.findByExperimentoId(id).orElseThrow(()->RegraNegocioException.conflito("É necessário responder o pré-teste primeiro"));
        validar(e,pre,r);
        var p=new PosTeste(e,r.situacaoExperimento(),r.motivoSeInterrompeuOuNaoIniciou(),r.dataInterrupcao(),r.sessoesRealizadas(),r.tempoRealizadoMinutos(),r.custoRealizado(),r.dificuldadePercebida(),r.satisfacaoPercebida(),r.obstaculoEncontrado(),r.descricaoOutro(),r.relatoExemplo(),r.segurancaDecidirDepois(),r.proximoPasso(),r.oQuePesouNaEscolha(),r.contribuicaoPercebidaApp(),r.contextoMudancas(),clock.instant());
        p=posTestes.save(p);
        var c=comparacoes.save(calculadora.calcular(pre,p));
        e.encerrar(r.situacaoExperimento());
        return PosTesteResposta.de(p,c);
    }
    private void validar(Experimento e,PreTeste pre,PosTesteDados r) {
        PreTesteService.validarOutro(r.obstaculoEncontrado(),r.descricaoOutro());
        var situacao=r.situacaoExperimento();
        if((situacao==SituacaoPosTeste.C||situacao==SituacaoPosTeste.D) && (r.motivoSeInterrompeuOuNaoIniciou()==null||r.motivoSeInterrompeuOuNaoIniciou().isBlank()))
            throw RegraNegocioException.invalida("Informe o motivo da interrupção ou de não ter iniciado");
        if(situacao==SituacaoPosTeste.D) {
            if(r.sessoesRealizadas()!=0 || r.dificuldadePercebida()!=AvaliacaoPercebida.NAO_SE_APLICA || r.satisfacaoPercebida()!=AvaliacaoPercebida.NAO_SE_APLICA)
                throw RegraNegocioException.invalida("D exige zero sessões e NAO_SE_APLICA em dificuldade e satisfação");
        } else {
            if(r.sessoesRealizadas()==0 || !r.dificuldadePercebida().aplicavel() || !r.satisfacaoPercebida().aplicavel())
                throw RegraNegocioException.invalida("A, B e C exigem sessões realizadas e notas de 1 a 5");
            if(situacao==SituacaoPosTeste.A && r.sessoesRealizadas()<pre.getSessoesEsperadas())
                throw RegraNegocioException.invalida("A exige realizar a quantidade de sessões planejada ou mais");
            if(situacao==SituacaoPosTeste.B && r.sessoesRealizadas()>=pre.getSessoesEsperadas())
                throw RegraNegocioException.invalida("B exige realizar parte das sessões, abaixo da quantidade prevista");
        }
        if(r.dataInterrupcao()!=null && (situacao!=SituacaoPosTeste.C || r.dataInterrupcao().isBefore(e.getDataInicio()) || r.dataInterrupcao().isAfter(e.getDataFimPrevista()) || r.dataInterrupcao().isAfter(LocalDate.now(clock))))
            throw RegraNegocioException.invalida("A data de interrupção só se aplica a C e deve estar na janela do experimento, sem ser futura");
    }
    @Transactional(readOnly=true)
    public PosTesteResposta consultar(Long id) {
        experimentos.obter(id);
        var p=posTestes.findByExperimentoId(id).orElseThrow(()->RegraNegocioException.ausente("Pós-teste"));
        var c=comparacoes.findByExperimentoId(id).orElseThrow(()->RegraNegocioException.conflito("Comparação não disponível"));
        return PosTesteResposta.de(p,c);
    }
}
