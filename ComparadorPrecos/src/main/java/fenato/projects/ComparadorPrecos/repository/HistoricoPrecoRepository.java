package fenato.projects.ComparadorPrecos.repository;

import fenato.projects.ComparadorPrecos.model.HistoricoPreco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricoPrecoRepository extends JpaRepository<HistoricoPreco, Long> {
}