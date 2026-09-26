package br.com.antes.repository;
import br.com.antes.domain.Template;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateRepository extends JpaRepository<Template, Long> {
    java.util.Optional<Template> findFirstByCodigoOrderByVersaoDesc(String codigo);
    java.util.Optional<Template> findByCodigoAndVersao(String codigo, Integer versao);
    java.util.List<Template> findAllByOrderByCodigoAscVersaoDesc();
}
