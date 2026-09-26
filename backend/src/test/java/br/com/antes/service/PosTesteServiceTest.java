package br.com.antes.service;

import br.com.antes.domain.*;
import br.com.antes.repository.*;
import java.util.Optional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PosTesteServiceTest {
    ExperimentoService experimentos;PreTesteRepository pre;PosTesteRepository pos;ComparacaoRepository comparacoes;
    PosTesteService service;Experimento e;
    @BeforeEach void preparar() {
        experimentos=mock(ExperimentoService.class);pre=mock(PreTesteRepository.class);pos=mock(PosTesteRepository.class);comparacoes=mock(ComparacaoRepository.class);
        e=Fixtures.experimento();when(experimentos.obterBloqueado(1L)).thenReturn(e);when(pre.findByExperimentoId(1L)).thenReturn(Optional.of(Fixtures.pre(e)));
        service=new PosTesteService(experimentos,pre,pos,comparacoes,new ComparacaoService(comparacoes,experimentos),Fixtures.CLOCK);
    }
    void habilitarGravacao() { when(pos.save(any())).thenAnswer(i->i.getArgument(0));when(comparacoes.save(any())).thenAnswer(i->i.getArgument(0)); }
    @Test void aceitaDComCustoETempoDePreparacaoENaoConverteAusenciaEmZero() {
        habilitarGravacao();
        var r=service.criar(1L,Fixtures.posDados(SituacaoPosTeste.D,0,AvaliacaoPercebida.NAO_SE_APLICA,AvaliacaoPercebida.NAO_SE_APLICA,"Não consegui instrumento"));
        assertThat(e.getSituacao()).isEqualTo(SituacaoExperimento.NAO_INICIADO);
        assertThat(r.respostas().custoRealizado()).isEqualByComparingTo("15.75");
        assertThat(r.respostas().tempoRealizadoMinutos()).isEqualTo(75);
        assertThat(r.comparacao().transicaoDificuldade()).isEqualTo(TransicaoEscala.NAO_SE_APLICA);
        assertThat(r.comparacao().transicaoSatisfacao()).isEqualTo(TransicaoEscala.NAO_SE_APLICA);
        assertThat(r.comparacao().diferencaGasto()).isEqualByComparingTo("5.25");
        assertThat(r.comparacao().diferencaSessoes()).isEqualTo(-3);
    }
    @ParameterizedTest @CsvSource({"0,NIVEL_1,NAO_SE_APLICA","0,NAO_SE_APLICA,NIVEL_5","1,NAO_SE_APLICA,NAO_SE_APLICA"})
    void rejeitaDInconsistente(int sessoes,AvaliacaoPercebida dificuldade,AvaliacaoPercebida satisfacao) {
        assertThatThrownBy(()->service.criar(1L,Fixtures.posDados(SituacaoPosTeste.D,sessoes,dificuldade,satisfacao,"Motivo"))).hasMessageContaining("D exige");
        verify(pos,never()).save(any());verify(comparacoes,never()).save(any());
    }
    @ParameterizedTest @CsvSource({"A,2,NIVEL_2,NIVEL_4","B,3,NIVEL_2,NIVEL_4","B,0,NIVEL_2,NIVEL_4","C,1,NAO_SE_APLICA,NIVEL_4","A,3,NIVEL_2,NAO_SE_APLICA"})
    void rejeitaABCInconsistentes(SituacaoPosTeste s,int qtd,AvaliacaoPercebida d,AvaliacaoPercebida sat) {
        assertThatThrownBy(()->service.criar(1L,Fixtures.posDados(s,qtd,d,sat,"Motivo"))).isInstanceOf(RegraNegocioException.class);
        verify(pos,never()).save(any());
    }
    @ParameterizedTest @CsvSource({"A,3,CONCLUIDO","B,2,CONCLUIDO","C,1,INTERROMPIDO"})
    void aceitaABCConsistentes(SituacaoPosTeste s,int qtd,SituacaoExperimento estado) {
        habilitarGravacao();var r=service.criar(1L,Fixtures.posDados(s,qtd,AvaliacaoPercebida.NIVEL_4,AvaliacaoPercebida.NIVEL_5,"Motivo"));
        assertThat(e.getSituacao()).isEqualTo(estado);
        assertThat(r.comparacao().transicaoDificuldade()).isEqualTo(TransicaoEscala.PIOROU);
        assertThat(r.comparacao().transicaoSatisfacao()).isEqualTo(TransicaoEscala.MELHOROU);
        assertThat(r.comparacao().transicaoIntencao().mudou()).isTrue();
        assertThat(r.comparacao().mudancaSeguranca()).isEqualTo(3);
    }
    @Test void rejeitaDsemMotivo() { assertThatThrownBy(()->service.criar(1L,Fixtures.posDados(SituacaoPosTeste.D,0,AvaliacaoPercebida.NAO_SE_APLICA,AvaliacaoPercebida.NAO_SE_APLICA," "))).hasMessageContaining("motivo"); }
    @Test void rejeitaCSemMotivo() { assertThatThrownBy(()->service.criar(1L,Fixtures.posDados(SituacaoPosTeste.C,1,AvaliacaoPercebida.NIVEL_2,AvaliacaoPercebida.NIVEL_4,null))).hasMessageContaining("motivo"); }
    @Test void rejeitaPosSemPre() {
        when(pre.findByExperimentoId(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(()->service.criar(1L,Fixtures.posDados(SituacaoPosTeste.A,3,AvaliacaoPercebida.NIVEL_2,AvaliacaoPercebida.NIVEL_4,null))).hasMessageContaining("pré-teste");
    }
    @Test void rejeitaDuplicacaoDoPos() {
        when(pos.existsByExperimentoId(1L)).thenReturn(true);
        assertThatThrownBy(()->service.criar(1L,Fixtures.posDados(SituacaoPosTeste.A,3,AvaliacaoPercebida.NIVEL_2,AvaliacaoPercebida.NIVEL_4,null))).hasMessageContaining("encerrado");
        verify(pos,never()).save(any());
    }
}
