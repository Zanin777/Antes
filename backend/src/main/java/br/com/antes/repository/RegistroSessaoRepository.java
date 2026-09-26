package br.com.antes.repository;
import br.com.antes.domain.RegistroSessao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroSessaoRepository extends JpaRepository<RegistroSessao, Long> {
    java.util.List<RegistroSessao> findAllByExperimentoIdOrderByDataAscIdAsc(Long experimentoId);
}
