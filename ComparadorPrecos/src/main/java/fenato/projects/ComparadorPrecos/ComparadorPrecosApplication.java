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

import java.math.BigDecimal;

@SpringBootApplication
public class ComparadorPrecosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComparadorPrecosApplication.class, args);
    }

    @Bean
    public CommandLineRunner testarScraper(
            ScraperService scraperService, 
            ProdutoLojaRepository produtoLojaRepository, 
            HistoricoPrecoRepository historicoPrecoRepository) {
            
        return args -> {
            System.out.println("Iniciando teste completo: Scraping + Banco de Dados...");

            String urlTeste = "https://www.kabum.com.br/produto/934759/console-sony-playstation-5-com-leitor-de-discos-ssd-1tb-controle-sem-fio-dualsense"; 
            String seletorCssTeste = ".text-4xl.text-secondary-500.font-bold.transition-all.duration-500"; 

            // 1. Simula o cadastro de um produto no banco (se fosse a vida real, viria de uma tela de cadastro)
            ProdutoLoja ps5 = new ProdutoLoja();
            ps5.setUrl(urlTeste);
            ps5.setSeletorCssPreco(seletorCssTeste);
            produtoLojaRepository.save(ps5); // Salva o produto para gerar um ID

            // 2. Faz o Web Scraping
            java.math.BigDecimal precoCapturado = scraperService.extrairPreco(urlTeste, seletorCssTeste);

            if (precoCapturado != null) {
                // 3. Salva o histórico atrelado ao produto
                fenato.projects.ComparadorPrecos.model.HistoricoPreco historico = new fenato.projects.ComparadorPrecos.model.HistoricoPreco();
                historico.setValor(precoCapturado);
                historico.setDataCaptura(java.time.LocalDateTime.now()); // Pega a hora exata de agora
                historico.setProdutoLoja(ps5); // Cria a relação entre as tabelas

                historicoPrecoRepository.save(historico);

                System.out.println("=====================================");
                System.out.println("SUCESSO! Preço salvo no Banco de Dados: R$ " + precoCapturado);
                System.out.println("=====================================");
            } else {
                System.out.println("Falha ao capturar o preço.");
            }
        };
    }
}