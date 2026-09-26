package br.com.antes.service;

import br.com.antes.api.ApiDtos.*;
import br.com.antes.domain.*;
import java.math.BigDecimal;
import java.time.*;
import org.springframework.test.util.ReflectionTestUtils;

public final class Fixtures {
    public static final Clock CLOCK=Clock.fixed(Instant.parse("2026-09-26T15:00:00Z"),ZoneId.of("America/Sao_Paulo"));
    public static final LocalDate INICIO=LocalDate.of(2026,9,26);
    public static Template template() { return new Template("H01",1,Categoria.HOBBY,"Guitarra","Quero aprender?",7,3,20,"Praticar três vezes"); }
    public static Decisao decisao() { var d=new Decisao("Tocar guitarra",Categoria.HOBBY,"Experimentar",ExperienciaAnterior.NENHUMA,"P001");ReflectionTestUtils.setField(d,"id",1L);return d; }
    public static Experimento experimento() {
        var e=new Experimento(decisao(),TemplateSnapshot.copiar(template()),"Plano original","Instrumento emprestado",INICIO,INICIO.plusDays(6),SituacaoExperimento.PLANEJADO);
        ReflectionTestUtils.setField(e,"id",1L);return e;
    }
    public static PreTesteDados preDados() { return new PreTesteDados(3,60,new BigDecimal("10.50"),2,4,Obstaculo.O1,null,null,4,Intencao.CONTINUAR); }
    public static PreTeste pre(Experimento e) { var r=preDados();return new PreTeste(e,r.sessoesEsperadas(),r.tempoEsperadoMinutos(),r.gastoEsperado(),r.dificuldadeEsperada(),r.satisfacaoEsperada(),r.obstaculoPrincipalEsperado(),null,null,r.segurancaDecidirAntes(),r.intencaoAntes(),CLOCK.instant(),e.getDescricaoFinalEditada(),e.getMaterialProvedorFormato(),e.getDataInicio(),e.getDataFimPrevista()); }
    public static PosTesteDados posDados(SituacaoPosTeste situacao,int sessoes,AvaliacaoPercebida d,AvaliacaoPercebida s,String motivo) {
        return new PosTesteDados(situacao,motivo,null,sessoes,75,new BigDecimal("15.75"),d,s,Obstaculo.O4,null,"Conteúdo novo",7,Intencao.ADAPTAR,"Menos tempo","A comparação ajudou","Não houve");
    }
    public static PosTeste pos(Experimento e,PosTesteDados r) { return new PosTeste(e,r.situacaoExperimento(),r.motivoSeInterrompeuOuNaoIniciou(),r.dataInterrupcao(),r.sessoesRealizadas(),r.tempoRealizadoMinutos(),r.custoRealizado(),r.dificuldadePercebida(),r.satisfacaoPercebida(),r.obstaculoEncontrado(),r.descricaoOutro(),r.relatoExemplo(),r.segurancaDecidirDepois(),r.proximoPasso(),r.oQuePesouNaEscolha(),r.contribuicaoPercebidaApp(),r.contextoMudancas(),CLOCK.instant()); }
}
