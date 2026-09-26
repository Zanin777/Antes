package br.com.antes;

import br.com.antes.domain.*;
import br.com.antes.service.Fixtures;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.Clock;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(AntesApiTest.RelogioTeste.class)
class AntesApiTest {
    @TestConfiguration static class RelogioTeste {
        @Bean @Primary Clock relogioFixo() { return Fixtures.CLOCK; }
    }
    @Autowired MockMvc api;
    @Autowired ObjectMapper json;
    @Autowired JdbcTemplate banco;

    @BeforeEach void limparDadosDeTeste() {
        for(String tabela:new String[]{"comparacoes","registros_sessao","pos_testes","pre_testes","experimentos","decisoes"})
            banco.update("delete from "+tabela);
    }

    @Test void migracoesCarregamOsOitoTemplatesETodosOsCamposDoSnapshot() throws Exception {
        api.perform(get("/templates")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(8));
        long id=novoExperimento("HOBBY","H01");
        JsonNode template=null;
        for(var item:ler(api.perform(get("/templates")).andReturn()))
            if(item.get("codigo").asText().equals("H01")) template=item;
        assertThat(template).isNotNull();
        JsonNode experimento=ler(api.perform(get("/experimentos/"+id)).andReturn());
        assertThat(experimento.get("templateSnapshot")).isEqualTo(template);
        assertThat(experimento.get("dataFimPrevista").asText()).isEqualTo("2026-10-02");
        String original=template.get("descricao").asText();
        try {
            banco.update("update templates set descricao=? where codigo='H01' and versao=1","Novo conteúdo de catálogo");
            api.perform(get("/experimentos/"+id)).andExpect(jsonPath("$.templateSnapshot.descricao").value(original));
            long novo=novoExperimento("HOBBY","H01");
            api.perform(get("/experimentos/"+novo)).andExpect(jsonPath("$.templateSnapshot.descricao").value("Novo conteúdo de catálogo"));
        } finally {
            banco.update("update templates set descricao=? where codigo='H01' and versao=1",original);
        }
    }

    @Test void rejeitaCategoriaIncompativelSemPersistirExperimento() throws Exception {
        long decisao=novaDecisao("CURSO");
        enviar("/experimentos",experimentoDados(decisao,"H01")).andExpect(status().isBadRequest());
        assertThat(contar("experimentos")).isZero();
    }

    @Test void preservaPreTesteAposAjusteDoPlanoERejeitaReescrita() throws Exception {
        long id=novoExperimento("HOBBY","H01");
        enviarPre(id).andExpect(status().isCreated());
        String original=api.perform(get(base(id)+"/pre-teste")).andReturn().getResponse().getContentAsString();
        api.perform(patch(base(id)).contentType(MediaType.APPLICATION_JSON).content("""
            {"descricaoFinalEditada":"Plano adaptado","materialProvedorFormato":"Outro material","dataInicio":"2026-09-27"}
            """)).andExpect(status().isOk()).andExpect(jsonPath("$.dataFimPrevista").value("2026-10-03"));
        api.perform(get(base(id)+"/pre-teste")).andExpect(content().json(original));
        enviarPre(id).andExpect(status().isConflict());
        api.perform(put(base(id)+"/pre-teste").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Fixtures.preDados()))).andExpect(status().isMethodNotAllowed());
        assertThat(contar("pre_testes")).isEqualTo(1);
    }

    @Test void exigeIntencaoAntesEValidaEscalasEDinheiro() throws Exception {
        long id=novoExperimento("HOBBY","H01");
        ObjectNode dados=json.valueToTree(Fixtures.preDados());
        dados.remove("intencaoAntes");
        enviar(base(id)+"/pre-teste",dados).andExpect(status().isBadRequest());
        dados=json.valueToTree(Fixtures.preDados());dados.put("gastoEsperado",-1);
        enviar(base(id)+"/pre-teste",dados).andExpect(status().isBadRequest());
        dados=json.valueToTree(Fixtures.preDados());dados.put("dificuldadeEsperada",0);
        enviar(base(id)+"/pre-teste",dados).andExpect(status().isBadRequest());
        assertThat(contar("pre_testes")).isZero();
    }

    @Test void diarioExigePreBloqueiaAjustesESuportaPreparacaoSemPratica() throws Exception {
        long id=novoExperimento("HOBBY","H01");
        var registro=json.readTree("""
            {"data":"2026-09-26","minutosDedicados":10,"gastoAdicional":5.25,"notaOpcional":"Só preparei o material"}
            """);
        enviar(base(id)+"/registros-sessao",registro).andExpect(status().isConflict());
        enviarPre(id);
        enviar(base(id)+"/registros-sessao",registro).andExpect(status().isCreated());
        api.perform(get(base(id))).andExpect(jsonPath("$.situacao").value("EM_ANDAMENTO"));
        api.perform(patch(base(id)).contentType(MediaType.APPLICATION_JSON).content("""
            {"descricaoFinalEditada":"Outro plano","dataInicio":"2026-09-26"}
            """)).andExpect(status().isConflict());
        enviar(base(id)+"/pos-teste",posD()).andExpect(status().isCreated());
        api.perform(get(base(id))).andExpect(jsonPath("$.situacao").value("NAO_INICIADO"));
        enviar(base(id)+"/registros-sessao",registro).andExpect(status().isConflict());
    }

    @Test void naoIniciadoPersisteAusenciaExplicitaEComparacaoNaMesmaOperacao() throws Exception {
        long id=novoExperimento("CURSO","C01");enviarPre(id);
        enviar(base(id)+"/pos-teste",posD()).andExpect(status().isCreated())
            .andExpect(jsonPath("$.respostas.dificuldadePercebida").value("NAO_SE_APLICA"))
            .andExpect(jsonPath("$.comparacao.transicaoDificuldade").value("NAO_SE_APLICA"))
            .andExpect(jsonPath("$.comparacao.diferencaSessoes").value(-3))
            .andExpect(jsonPath("$.comparacao.diferencaGasto").value(5.25));
        assertThat(banco.queryForObject("select dificuldade_percebida from pos_testes where experimento_id=?",String.class,id)).isEqualTo("NAO_SE_APLICA");
        api.perform(get(base(id)+"/comparacao")).andExpect(status().isOk())
            .andExpect(jsonPath("$.transicaoIntencao.antes").value("CONTINUAR"))
            .andExpect(jsonPath("$.transicaoIntencao.depois").value("ADAPTAR"))
            .andExpect(jsonPath("$.transicaoIntencao.mudou").value(true));
        enviar(base(id)+"/pos-teste",posD()).andExpect(status().isConflict());
        api.perform(post(base(id)+"/comparacao")).andExpect(status().isMethodNotAllowed());
        assertThat(contar("comparacoes")).isEqualTo(1);
    }

    @Test void dRejeitaNumerosZeroESessoesSemGravarDadosParciais() throws Exception {
        long id=novoExperimento("HOBBY","H01");enviarPre(id);
        ObjectNode dados=json.valueToTree(posD());dados.put("dificuldadePercebida",3);
        enviar(base(id)+"/pos-teste",dados).andExpect(status().isBadRequest());
        dados=json.valueToTree(posD());dados.put("satisfacaoPercebida",0);
        enviar(base(id)+"/pos-teste",dados).andExpect(status().isBadRequest());
        dados=json.valueToTree(posD());dados.put("sessoesRealizadas",1);
        enviar(base(id)+"/pos-teste",dados).andExpect(status().isBadRequest());
        dados=json.valueToTree(posD());dados.putNull("satisfacaoPercebida");
        enviar(base(id)+"/pos-teste",dados).andExpect(status().isBadRequest());
        assertThat(contar("pos_testes")).isZero();assertThat(contar("comparacoes")).isZero();
        api.perform(get(base(id)+"/comparacao")).andExpect(status().isConflict());
    }

    @Test void fluxoConcluidoCalculaDirecaoCorretaDasEscalas() throws Exception {
        long id=novoExperimento("HOBBY","H02");enviarPre(id);
        enviar(base(id)+"/pos-teste",Fixtures.posDados(SituacaoPosTeste.A,3,AvaliacaoPercebida.NIVEL_4,AvaliacaoPercebida.NIVEL_5,null))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.respostas.dificuldadePercebida").value(4))
            .andExpect(jsonPath("$.comparacao.transicaoDificuldade").value("PIOROU"))
            .andExpect(jsonPath("$.comparacao.transicaoSatisfacao").value("MELHOROU"))
            .andExpect(jsonPath("$.comparacao.mudancaSeguranca").value(3));
        api.perform(get(base(id))).andExpect(jsonPath("$.situacao").value("CONCLUIDO"));
        api.perform(get("/decisoes/"+ler(api.perform(get(base(id))).andReturn()).get("decisaoId").asLong()+"/experimentos"))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(id));
    }

    @Test void falhaAoGravarComparacaoDesfazPosTesteEEstado() throws Exception {
        long id=novoExperimento("HOBBY","H01");enviarPre(id);
        // Falha real no banco depois do INSERT do pós; testa o limite da transação.
        banco.execute("alter table comparacoes add constraint falha_proposital_teste check (diferenca_sessoes <> 0)");
        try {
            enviar(base(id)+"/pos-teste",Fixtures.posDados(SituacaoPosTeste.A,3,AvaliacaoPercebida.NIVEL_2,AvaliacaoPercebida.NIVEL_4,null))
                .andExpect(status().isConflict());
            assertThat(contar("pos_testes")).isZero();assertThat(contar("comparacoes")).isZero();
            api.perform(get(base(id))).andExpect(jsonPath("$.situacao").value("PLANEJADO"));
        } finally { banco.execute("alter table comparacoes drop constraint falha_proposital_teste"); }
    }

    @Test void enviosConcorrentesNaoCriamDoisPreTestes() throws Exception {
        long id=novoExperimento("HOBBY","H01");
        var inicio=new CountDownLatch(1);
        try(var executor=Executors.newFixedThreadPool(2)) {
            Callable<Integer> envio=()->{inicio.await();return enviarPre(id).andReturn().getResponse().getStatus();};
            var um=executor.submit(envio);var dois=executor.submit(envio);inicio.countDown();
            assertThat(new Integer[]{um.get(15,TimeUnit.SECONDS),dois.get(15,TimeUnit.SECONDS)}).containsExactlyInAnyOrder(201,409);
        }
        assertThat(contar("pre_testes")).isEqualTo(1);
    }

    @Test void rejeitaCamposDesconhecidosERecursosInexistentes() throws Exception {
        api.perform(get("/experimentos/999999")).andExpect(status().isNotFound());
        long decisao=novaDecisao("HOBBY");
        ObjectNode dados=experimentoDados(decisao,"H01");dados.put("situacao","CONCLUIDO");
        enviar("/experimentos",dados).andExpect(status().isBadRequest());
        dados=experimentoDados(decisao,"H01");dados.put("templateVersao",999);
        enviar("/experimentos",dados).andExpect(status().isNotFound());
    }

    private long novaDecisao(String categoria) throws Exception {
        var dados=json.createObjectNode().put("titulo","Experimentar atividade").put("categoria",categoria).put("motivacao","Decidir se continuo").put("experienciaAnterior","NENHUMA").put("participanteCodigo","P001");
        return ler(enviar("/decisoes",dados).andExpect(status().isCreated()).andReturn()).get("id").asLong();
    }
    private long novoExperimento(String categoria,String codigo) throws Exception {
        return ler(enviar("/experimentos",experimentoDados(novaDecisao(categoria),codigo)).andExpect(status().isCreated()).andReturn()).get("id").asLong();
    }
    private ObjectNode experimentoDados(long id,String codigo) {
        return json.createObjectNode().put("decisaoId",id).put("templateCodigo",codigo).put("dataInicio","2026-09-26").put("descricaoFinalEditada","Plano original");
    }
    private ResultActions enviarPre(long id) throws Exception { return enviar(base(id)+"/pre-teste",Fixtures.preDados()); }
    private Object posD() { return Fixtures.posDados(SituacaoPosTeste.D,0,AvaliacaoPercebida.NAO_SE_APLICA,AvaliacaoPercebida.NAO_SE_APLICA,"Preparei materiais, mas não pratiquei"); }
    private ResultActions enviar(String uri,Object dados) throws Exception { return api.perform(post(uri).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(dados))); }
    private JsonNode ler(MvcResult r) throws Exception { return json.readTree(r.getResponse().getContentAsString()); }
    private int contar(String tabela) { return banco.queryForObject("select count(*) from "+tabela,Integer.class); }
    private String base(long id) { return "/experimentos/"+id; }
}
