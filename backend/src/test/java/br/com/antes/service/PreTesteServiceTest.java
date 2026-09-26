package br.com.antes.service;

import br.com.antes.api.ApiDtos.*;
import br.com.antes.domain.*;
import br.com.antes.repository.*;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PreTesteServiceTest {
    ExperimentoService experimentos;
    PreTesteRepository repository;
    PreTesteService service;
    Experimento e;
    @BeforeEach void preparar() {
        experimentos=mock(ExperimentoService.class);repository=mock(PreTesteRepository.class);
        service=new PreTesteService(experimentos,repository,Fixtures.CLOCK);e=Fixtures.experimento();
        when(experimentos.obterBloqueado(1L)).thenReturn(e);
    }
    @Test void recusaSegundoPreTesteSemSobrescrever() {
        when(repository.existsByExperimentoId(1L)).thenReturn(true);
        assertThatThrownBy(()->service.criar(1L,Fixtures.preDados())).isInstanceOf(RegraNegocioException.class).hasMessageContaining("não pode ser reescrito");
        verify(repository,never()).save(any());
    }
    @Test void guardaPlanoNaRespostaMesmoSeExperimentoForAjustado() {
        when(repository.save(any())).thenAnswer(i->i.getArgument(0));
        var r=service.criar(1L,Fixtures.preDados());
        e.ajustarPlano("Plano alterado","Outro material",Fixtures.INICIO.plusDays(1));
        assertThat(r.planoNaResposta().descricao()).isEqualTo("Plano original");
        assertThat(r.planoNaResposta().dataInicio()).isEqualTo(Fixtures.INICIO);
        assertThat(r.respostas().intencaoAntes()).isEqualTo(Intencao.CONTINUAR);
        assertThat(r.dataHoraResposta()).isEqualTo(Fixtures.CLOCK.instant());
    }
    @Test void recusaPreTesteDepoisDoInicio() {
        e.iniciar();
        assertThatThrownBy(()->service.criar(1L,Fixtures.preDados())).isInstanceOf(RegraNegocioException.class);
        verify(repository,never()).save(any());
    }
    @Test void outroExigeDescricao() {
        var r=new PreTesteDados(3,60,java.math.BigDecimal.ZERO,2,4,Obstaculo.O9," ",null,4,Intencao.NAO_SEI);
        assertThatThrownBy(()->service.criar(1L,r)).hasMessageContaining("O9");
    }
}
