package br.com.antes.repository;
import br.com.antes.domain.Decisao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DecisaoRepository extends JpaRepository<Decisao, Long> {

}
