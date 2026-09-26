package br.com.antes.service;

import br.com.antes.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.assertj.core.api.Assertions.*;
import static br.com.antes.service.Fixtures.*;

class ComparacaoServiceTest {
    private final ComparacaoService service=new ComparacaoService(null,null);

    @ParameterizedTest
    @CsvSource({"2,NIVEL_4,false,PIOROU", "4,NIVEL_2,false,MELHOROU",
        "2,NIVEL_4,true,MELHOROU", "4,NIVEL_2,true,PIOROU",
        "3,NIVEL_3,true,MANTEVE", "3,NIVEL_3,false,MANTEVE",
        "1,NAO_SE_APLICA,true,NAO_SE_APLICA", "5,NAO_SE_APLICA,false,NAO_SE_APLICA"})
    void interpretaDirecaoDaEscalaSemTratarAusenciaComoZero(int esperado,AvaliacaoPercebida realizado,boolean maiorMelhor,TransicaoEscala resultado) {
        assertThat(ComparacaoService.transicao(esperado,realizado,maiorMelhor)).isEqualTo(resultado);
    }

    @Test void calculaDiferencasEConservaAsDuasIntencoes() {
        var e=experimento();
        var resultado=service.calcular(pre(e),pos(e,posDados(SituacaoPosTeste.B,2,AvaliacaoPercebida.NIVEL_4,AvaliacaoPercebida.NIVEL_4,null)));
        assertThat(resultado.getDiferencaSessoes()).isEqualTo(-1);
        assertThat(resultado.getDiferencaTempoMinutos()).isEqualTo(15);
        assertThat(resultado.getDiferencaGasto()).isEqualByComparingTo("5.25");
        assertThat(resultado.getMudancaSeguranca()).isEqualTo(3);
        assertThat(resultado.getIntencaoAntes()).isEqualTo(Intencao.CONTINUAR);
        assertThat(resultado.getIntencaoDepois()).isEqualTo(Intencao.ADAPTAR);
        assertThat(resultado.getObstaculoMudou()).isTrue();
    }
}
