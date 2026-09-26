package br.com.antes.service;

import br.com.antes.api.ApiDtos.*;
import br.com.antes.domain.RegistroSessao;
import br.com.antes.repository.*;
import java.time.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistroSessaoService {
    private final ExperimentoService experimentos;
    private final PreTesteRepository preTestes;
    private final RegistroSessaoRepository registros;
    private final Clock clock;
    public RegistroSessaoService(ExperimentoService experimentos,PreTesteRepository preTestes,RegistroSessaoRepository registros,Clock clock) { this.experimentos=experimentos;this.preTestes=preTestes;this.registros=registros;this.clock=clock; }
    @Transactional
    public RegistroSessaoResposta criar(Long id,CriarRegistroSessao r) {
        var e=experimentos.obterBloqueado(id);
        if(e.encerrado()) throw RegraNegocioException.conflito("O diário já foi encerrado com o pós-teste");
        if(!preTestes.existsByExperimentoId(id)) throw RegraNegocioException.conflito("Responda o pré-teste antes do diário");
        if(r.data().isBefore(e.getDataInicio())||r.data().isAfter(e.getDataFimPrevista())||r.data().isAfter(LocalDate.now(clock)))
            throw RegraNegocioException.invalida("O registro deve estar na janela do experimento e não pode ser futuro");
        var registro=registros.save(new RegistroSessao(e,r.data(),r.minutosDedicados(),r.gastoAdicional(),r.notaOpcional()));
        e.iniciar();
        return RegistroSessaoResposta.de(registro);
    }
    @Transactional(readOnly=true)
    public List<RegistroSessaoResposta> listar(Long id) {
        experimentos.obter(id);
        return registros.findAllByExperimentoIdOrderByDataAscIdAsc(id).stream().map(RegistroSessaoResposta::de).toList();
    }
}
