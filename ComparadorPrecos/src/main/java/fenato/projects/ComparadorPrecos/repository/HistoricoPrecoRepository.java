package fenato.projects.ComparadorPrecos.repository;

import fenato.projects.ComparadorPrecos.model.HistoricoPreco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistoricoPrecoRepository extends JpaRepository<HistoricoPreco, Long> {

    List<HistoricoPreco> findByProdutoLojaIdOrderByDataCapturaDesc(Long produtoLojaId);

}