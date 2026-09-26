package br.com.antes.repository;
import br.com.antes.domain.Experimento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperimentoRepository extends JpaRepository<Experimento, Long> {
    java.util.List<Experimento> findAllByDecisaoIdOrderByIdAsc(Long decisaoId);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select e from Experimento e where e.id = :id")
    java.util.Optional<Experimento> buscarComBloqueio(@org.springframework.data.repository.query.Param("id") Long id);
}
