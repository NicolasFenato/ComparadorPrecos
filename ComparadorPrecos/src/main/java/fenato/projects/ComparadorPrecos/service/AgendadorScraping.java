package fenato.projects.ComparadorPrecos.service;

import fenato.projects.ComparadorPrecos.model.HistoricoPreco;
import fenato.projects.ComparadorPrecos.model.ProdutoLoja;
import fenato.projects.ComparadorPrecos.repository.HistoricoPrecoRepository;
import fenato.projects.ComparadorPrecos.repository.ProdutoLojaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgendadorScraping {

    private final ProdutoLojaRepository produtoLojaRepository;
    private final HistoricoPrecoRepository historicoPrecoRepository;
    private final ScraperService scraperService;

    // Construtor para o Spring injetar as dependências
    public AgendadorScraping(ProdutoLojaRepository produtoLojaRepository, 
                             HistoricoPrecoRepository historicoPrecoRepository, 
                             ScraperService scraperService) {
        this.produtoLojaRepository = produtoLojaRepository;
        this.historicoPrecoRepository = historicoPrecoRepository;
        this.scraperService = scraperService;
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "America/Sao_Paulo")
    public void atualizarPrecosAutomaticamente() {
        System.out.println("\n[WORKER] Iniciando varredura de preços: " + LocalDateTime.now());

        // 1. Busca todos os produtos que estão na tabela produto_loja
        List<ProdutoLoja> produtos = produtoLojaRepository.findAll();

        // 2. Faz um loop (repetição) passando por cada produto encontrado
        for (ProdutoLoja produto : produtos) {
            System.out.println("-> Verificando: " + produto.getUrl());

            // 3. Usa o nosso serviço do Jsoup para capturar o preço na internet
            BigDecimal precoCapturado = scraperService.extrairPreco(produto.getUrl(), produto.getSeletorCssPreco());

            // 4. Se achou o preço, salva no histórico!
            if (precoCapturado != null) {
                HistoricoPreco historico = new HistoricoPreco();
                historico.setValor(precoCapturado);
                historico.setDataCaptura(LocalDateTime.now());
                historico.setProdutoLoja(produto);

                historicoPrecoRepository.save(historico);
                System.out.println("   [SUCESSO] Novo preço salvo: R$ " + precoCapturado);
            } else {
                System.out.println("   [ERRO] Não foi possível capturar o preço.");
            }
        }
        System.out.println("[WORKER] Varredura finalizada. Dormindo até o próximo ciclo...\n");
    }
}