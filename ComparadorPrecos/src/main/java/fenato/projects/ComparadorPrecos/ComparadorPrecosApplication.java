package fenato.projects.ComparadorPrecos;

import fenato.projects.ComparadorPrecos.repository.HistoricoPrecoRepository;
import fenato.projects.ComparadorPrecos.repository.ProdutoLojaRepository;
import fenato.projects.ComparadorPrecos.model.ProdutoLoja;
import fenato.projects.ComparadorPrecos.model.HistoricoPreco;
import fenato.projects.ComparadorPrecos.service.ScraperService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.math.BigDecimal;

@SpringBootApplication
@EnableScheduling
public class ComparadorPrecosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComparadorPrecosApplication.class, args);
    }

    @Bean
    public CommandLineRunner testarNovaBusca(
            fenato.projects.ComparadorPrecos.service.ScraperService scraperService,
            fenato.projects.ComparadorPrecos.repository.ProdutoLojaRepository produtoRepo,
            fenato.projects.ComparadorPrecos.repository.HistoricoPrecoRepository historicoRepo) {
        
        return args -> {
            System.out.println("\n--- INICIANDO CAPTURA EM MASSA ---");
            // Agora estamos enviando os repositórios junto para ele poder salvar!
            //scraperService.varrerBuscaMercadoLivre("playstation 5", produtoRepo, historicoRepo); 
            System.out.println("--- FIM DA CAPTURA EM MASSA ---\n");
        };
    }
    
}