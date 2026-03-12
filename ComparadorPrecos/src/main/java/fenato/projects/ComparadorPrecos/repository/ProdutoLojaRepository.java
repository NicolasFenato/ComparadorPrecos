package fenato.projects.ComparadorPrecos.repository;

import fenato.projects.ComparadorPrecos.model.ProdutoLoja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// Repositório: Fornece ferramentas para manipulação dos dados armazenados na DB
@Repository
public interface ProdutoLojaRepository extends JpaRepository<ProdutoLoja, Long> {

    // Método para listar produtos do tipo ProdutoLoja pelo nome ( ignoring case )
    List<ProdutoLoja> findByNomeContainingIgnoreCase(String nome);

}