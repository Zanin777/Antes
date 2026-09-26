package br.com.antes.repository;
import br.com.antes.domain.Comparacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComparacaoRepository extends JpaRepository<Comparacao, Long> {
    boolean existsByExperimentoId(Long experimentoId);
    java.util.Optional<Comparacao> findByExperimentoId(Long experimentoId);
}
