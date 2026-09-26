package br.com.antes.api;

import br.com.antes.domain.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;

/** Contratos HTTP independentes das entidades JPA. */
public final class ApiDtos {
    private ApiDtos() {}

    public record CriarDecisao(
        @NotBlank @Size(max=200) String titulo,
        @NotNull Categoria categoria,
        @NotBlank @Size(max=4000) String motivacao,
        @NotNull ExperienciaAnterior experienciaAnterior,
        @NotBlank @Size(max=64) String participanteCodigo) {}

    public record CriarExperimento(
        @NotNull @Positive Long decisaoId,
        @NotBlank @Pattern(regexp="[HC][0-9]{2}") String templateCodigo,
        @Positive Integer templateVersao,
        @NotNull LocalDate dataInicio,
        @Size(max=10000) String descricaoFinalEditada,
        @Size(max=2000) String materialProvedorFormato) {}

    public record AjustarPlano(
        @NotBlank @Size(max=10000) String descricaoFinalEditada,
        @Size(max=2000) String materialProvedorFormato,
        @NotNull LocalDate dataInicio) {}

    public record PreTesteDados(
        @NotNull @Min(0) Integer sessoesEsperadas,
        @NotNull @Min(0) Integer tempoEsperadoMinutos,
        @NotNull @DecimalMin("0.00") @Digits(integer=10,fraction=2) BigDecimal gastoEsperado,
        @NotNull @Min(1) @Max(5) Integer dificuldadeEsperada,
        @NotNull @Min(1) @Max(5) Integer satisfacaoEsperada,
        @NotNull Obstaculo obstaculoPrincipalEsperado,
        @Size(max=2000) String descricaoOutro,
        @Size(max=4000) String comoObstaculoApareceria,
        @NotNull @Min(0) @Max(10) Integer segurancaDecidirAntes,
        @NotNull Intencao intencaoAntes) {}

    public record PosTesteDados(
        @NotNull SituacaoPosTeste situacaoExperimento,
        @Size(max=4000) String motivoSeInterrompeuOuNaoIniciou,
        LocalDate dataInterrupcao,
        @NotNull @Min(0) Integer sessoesRealizadas,
        @NotNull @Min(0) Integer tempoRealizadoMinutos,
        @NotNull @DecimalMin("0.00") @Digits(integer=10,fraction=2) BigDecimal custoRealizado,
        @NotNull AvaliacaoPercebida dificuldadePercebida,
        @NotNull AvaliacaoPercebida satisfacaoPercebida,
        @NotNull Obstaculo obstaculoEncontrado,
        @Size(max=2000) String descricaoOutro,
        @Size(max=4000) String relatoExemplo,
        @NotNull @Min(0) @Max(10) Integer segurancaDecidirDepois,
        @NotNull Intencao proximoPasso,
        @Size(max=4000) String oQuePesouNaEscolha,
        @Size(max=4000) String contribuicaoPercebidaApp,
        @Size(max=4000) String contextoMudancas) {}

    public record CriarRegistroSessao(
        @NotNull LocalDate data,
        @NotNull @Min(0) Integer minutosDedicados,
        @NotNull @DecimalMin("0.00") @Digits(integer=10,fraction=2) BigDecimal gastoAdicional,
        @Size(max=4000) String notaOpcional) {}

    public record DecisaoResposta(Long id,String titulo,Categoria categoria,String motivacao,ExperienciaAnterior experienciaAnterior,String participanteCodigo) {
        public static DecisaoResposta de(Decisao d) { return new DecisaoResposta(d.getId(),d.getTitulo(),d.getCategoria(),d.getMotivacao(),d.getExperienciaAnterior(),d.getParticipanteCodigo()); }
    }
    public record TemplateResposta(String codigo,Integer versao,Categoria categoria,String titulo,String perguntaDecisao,Integer duracaoDias,Integer sessoesSugeridas,Integer minutosPorSessaoSugerido,String descricao) {
        public static TemplateResposta de(TemplateSnapshot t) { return new TemplateResposta(t.getCodigo(),t.getVersao(),t.getCategoria(),t.getTitulo(),t.getPerguntaDecisao(),t.getDuracaoDias(),t.getSessoesSugeridas(),t.getMinutosPorSessaoSugerido(),t.getDescricao()); }
        public static TemplateResposta de(Template t) { return de(TemplateSnapshot.copiar(t)); }
    }
    public record ExperimentoResposta(Long id,Long decisaoId,TemplateResposta templateSnapshot,String descricaoFinalEditada,String materialProvedorFormato,LocalDate dataInicio,LocalDate dataFimPrevista,SituacaoExperimento situacao) {
        public static ExperimentoResposta de(Experimento e) { return new ExperimentoResposta(e.getId(),e.getDecisao().getId(),TemplateResposta.de(e.getTemplateSnapshot()),e.getDescricaoFinalEditada(),e.getMaterialProvedorFormato(),e.getDataInicio(),e.getDataFimPrevista(),e.getSituacao()); }
    }
    public record PlanoNaResposta(String descricao,String materialProvedorFormato,LocalDate dataInicio,LocalDate dataFimPrevista) {}
    public record PreTesteResposta(Long id,Long experimentoId,PreTesteDados respostas,Instant dataHoraResposta,PlanoNaResposta planoNaResposta) {
        public static PreTesteResposta de(PreTeste p) { return new PreTesteResposta(p.getId(),p.getExperimento().getId(),new PreTesteDados(p.getSessoesEsperadas(),p.getTempoEsperadoMinutos(),p.getGastoEsperado(),p.getDificuldadeEsperada(),p.getSatisfacaoEsperada(),p.getObstaculoPrincipalEsperado(),p.getDescricaoOutro(),p.getComoObstaculoApareceria(),p.getSegurancaDecidirAntes(),p.getIntencaoAntes()),p.getDataHoraResposta(),new PlanoNaResposta(p.getDescricaoPlanoNaResposta(),p.getMaterialNaResposta(),p.getDataInicioNaResposta(),p.getDataFimNaResposta())); }
    }
    public record TransicaoIntencao(Intencao antes,Intencao depois,boolean mudou) {}
    public record ComparacaoResposta(Long id,Long experimentoId,Integer diferencaSessoes,Integer diferencaTempoMinutos,BigDecimal diferencaGasto,TransicaoEscala transicaoDificuldade,TransicaoEscala transicaoSatisfacao,Boolean obstaculoMudou,TransicaoIntencao transicaoIntencao,Integer mudancaSeguranca) {
        public static ComparacaoResposta de(Comparacao c) { return new ComparacaoResposta(c.getId(),c.getExperimento().getId(),c.getDiferencaSessoes(),c.getDiferencaTempoMinutos(),c.getDiferencaGasto(),c.getTransicaoDificuldade(),c.getTransicaoSatisfacao(),c.getObstaculoMudou(),new TransicaoIntencao(c.getIntencaoAntes(),c.getIntencaoDepois(),c.getIntencaoAntes()!=c.getIntencaoDepois()),c.getMudancaSeguranca()); }
    }
    public record PosTesteResposta(Long id,Long experimentoId,PosTesteDados respostas,Instant dataHoraResposta,ComparacaoResposta comparacao) {
        public static PosTesteResposta de(PosTeste p,Comparacao c) { return new PosTesteResposta(p.getId(),p.getExperimento().getId(),new PosTesteDados(p.getSituacaoExperimento(),p.getMotivoSeInterrompeuOuNaoIniciou(),p.getDataInterrupcao(),p.getSessoesRealizadas(),p.getTempoRealizadoMinutos(),p.getCustoRealizado(),p.getDificuldadePercebida(),p.getSatisfacaoPercebida(),p.getObstaculoEncontrado(),p.getDescricaoOutro(),p.getRelatoExemplo(),p.getSegurancaDecidirDepois(),p.getProximoPasso(),p.getOQuePesouNaEscolha(),p.getContribuicaoPercebidaApp(),p.getContextoMudancas()),p.getDataHoraResposta(),ComparacaoResposta.de(c)); }
    }
    public record RegistroSessaoResposta(Long id,Long experimentoId,LocalDate data,Integer minutosDedicados,BigDecimal gastoAdicional,String notaOpcional) {
        public static RegistroSessaoResposta de(RegistroSessao r) { return new RegistroSessaoResposta(r.getId(),r.getExperimento().getId(),r.getData(),r.getMinutosDedicados(),r.getGastoAdicional(),r.getNotaOpcional()); }
    }
}
