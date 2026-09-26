package br.com.antes.service;

import br.com.antes.api.ApiDtos.*;
import br.com.antes.domain.Decisao;
import br.com.antes.repository.DecisaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DecisaoService {
    private final DecisaoRepository decisoes;
    public DecisaoService(DecisaoRepository decisoes) { this.decisoes=decisoes; }
    @Transactional
    public DecisaoResposta criar(CriarDecisao r) {
        return DecisaoResposta.de(decisoes.save(new Decisao(r.titulo().strip(),r.categoria(),r.motivacao().strip(),r.experienciaAnterior(),r.participanteCodigo().strip())));
    }
    @Transactional(readOnly=true)
    public DecisaoResposta consultar(Long id) {
        return DecisaoResposta.de(decisoes.findById(id).orElseThrow(()->RegraNegocioException.ausente("Decisão")));
    }
}
