package fenato.projects.ComparadorPrecos.repository;

import fenato.projects.ComparadorPrecos.model.ProdutoLoja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProdutoLojaRepository extends JpaRepository<ProdutoLoja, Long> {

    List<ProdutoLoja> findByNomeContainingIgnoreCase(String nome);

}