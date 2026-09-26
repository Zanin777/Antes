package br.com.antes.repository;
import br.com.antes.domain.PreTeste;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreTesteRepository extends JpaRepository<PreTeste, Long> {
    boolean existsByExperimentoId(Long experimentoId);
    java.util.Optional<PreTeste> findByExperimentoId(Long experimentoId);
}
