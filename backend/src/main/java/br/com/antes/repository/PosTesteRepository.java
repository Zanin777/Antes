package br.com.antes.repository;
import br.com.antes.domain.PosTeste;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PosTesteRepository extends JpaRepository<PosTeste, Long> {
    boolean existsByExperimentoId(Long experimentoId);
    java.util.Optional<PosTeste> findByExperimentoId(Long experimentoId);
}
