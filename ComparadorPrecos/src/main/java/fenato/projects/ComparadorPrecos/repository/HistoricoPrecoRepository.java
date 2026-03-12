package fenato.projects.ComparadorPrecos.repository;

import fenato.projects.ComparadorPrecos.model.HistoricoPreco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// Repositório: Fornece ferramentas para manipulação dos dados armazenados na DB
@Repository
public interface HistoricoPrecoRepository extends JpaRepository<HistoricoPreco, Long> {

    /* Lista o Histórico de preço do tipo HistoricoPreco de um determinado produto
        e ordena pela data de captura de modo decrescente ( mais recente -> mais antigo) */
    List<HistoricoPreco> findByProdutoLojaIdOrderByDataCapturaDesc(Long produtoLojaId);

}