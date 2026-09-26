package br.com.antes.api;

import br.com.antes.api.ApiDtos.*;
import br.com.antes.service.*;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AntesController {
    private final DecisaoService decisoes;
    private final ExperimentoService experimentos;
    private final PreTesteService preTestes;
    private final PosTesteService posTestes;
    private final RegistroSessaoService registros;
    private final ComparacaoService comparacoes;
    public AntesController(DecisaoService decisoes,ExperimentoService experimentos,PreTesteService preTestes,PosTesteService posTestes,RegistroSessaoService registros,ComparacaoService comparacoes) {
        this.decisoes=decisoes;this.experimentos=experimentos;this.preTestes=preTestes;this.posTestes=posTestes;this.registros=registros;this.comparacoes=comparacoes;
    }
    @PostMapping("/decisoes")
    public ResponseEntity<DecisaoResposta> criarDecisao(@RequestBody @Valid CriarDecisao dados) {
        var r=decisoes.criar(dados);return criado("/decisoes/"+r.id(),r);
    }
    @GetMapping("/decisoes/{id}")
    public DecisaoResposta decisao(@PathVariable Long id) { return decisoes.consultar(id); }
    @GetMapping("/templates")
    public List<TemplateResposta> catalogo() { return experimentos.catalogo(); }
    @PostMapping("/experimentos")
    public ResponseEntity<ExperimentoResposta> criarExperimento(@RequestBody @Valid CriarExperimento dados) {
        var r=experimentos.criar(dados);return criado("/experimentos/"+r.id(),r);
    }
    @GetMapping("/experimentos/{id}")
    public ExperimentoResposta experimento(@PathVariable Long id) { return experimentos.consultar(id); }
    @PatchMapping("/experimentos/{id}")
    public ExperimentoResposta ajustarPlano(@PathVariable Long id,@RequestBody @Valid AjustarPlano dados) { return experimentos.ajustar(id,dados); }
    @GetMapping("/decisoes/{id}/experimentos")
    public List<ExperimentoResposta> listar(@PathVariable Long id) { return experimentos.listar(id); }
    @PostMapping("/experimentos/{id}/pre-teste")
    public ResponseEntity<PreTesteResposta> criarPreTeste(@PathVariable Long id,@RequestBody @Valid PreTesteDados dados) {
        return criado("/experimentos/"+id+"/pre-teste",preTestes.criar(id,dados));
    }
    @GetMapping("/experimentos/{id}/pre-teste")
    public PreTesteResposta preTeste(@PathVariable Long id) { return preTestes.consultar(id); }
    @PostMapping("/experimentos/{id}/registros-sessao")
    public ResponseEntity<RegistroSessaoResposta> criarRegistro(@PathVariable Long id,@RequestBody @Valid CriarRegistroSessao dados) {
        return criado("/experimentos/"+id+"/registros-sessao",registros.criar(id,dados));
    }
    @GetMapping("/experimentos/{id}/registros-sessao")
    public List<RegistroSessaoResposta> registros(@PathVariable Long id) { return registros.listar(id); }
    @PostMapping("/experimentos/{id}/pos-teste")
    public ResponseEntity<PosTesteResposta> criarPosTeste(@PathVariable Long id,@RequestBody @Valid PosTesteDados dados) {
        return criado("/experimentos/"+id+"/pos-teste",posTestes.criar(id,dados));
    }
    @GetMapping("/experimentos/{id}/pos-teste")
    public PosTesteResposta posTeste(@PathVariable Long id) { return posTestes.consultar(id); }
    @GetMapping("/experimentos/{id}/comparacao")
    public ComparacaoResposta comparacao(@PathVariable Long id) { return comparacoes.consultar(id); }

    private static <T> ResponseEntity<T> criado(String local,T corpo) { return ResponseEntity.created(URI.create(local)).body(corpo); }
}
