package fenato.projects.ComparadorPrecos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import fenato.projects.ComparadorPrecos.service.ScraperService;
import fenato.projects.ComparadorPrecos.model.HistoricoPreco;
import fenato.projects.ComparadorPrecos.model.ProdutoLoja;
import fenato.projects.ComparadorPrecos.repository.HistoricoPrecoRepository;
import fenato.projects.ComparadorPrecos.repository.ProdutoLojaRepository;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Avisa que esta classe vai responder requisições web devolvendo JSON
@RequestMapping("/api/produtos") // Todas as URLs dessa classe vão começar com /api/produtos
@CrossOrigin(origins = "*") // Permite que o seu futuro Front-end ou App consiga acessar essa API sem bloqueios de segurança
public class ProdutoController {
    @Autowired
    private final ProdutoLojaRepository produtoLojaRepository;
    @Autowired
    private final HistoricoPrecoRepository historicoPrecoRepository;

    @Autowired
    private ScraperService scraperService;

    public ProdutoController(ProdutoLojaRepository produtoLojaRepository, HistoricoPrecoRepository historicoPrecoRepository) {
        this.produtoLojaRepository = produtoLojaRepository;
        this.historicoPrecoRepository = historicoPrecoRepository;
    }

    // Endpoint 1: Retorna a lista de todos os produtos cadastrados
    @GetMapping
    public List<ProdutoLoja> listarProdutos() {
        return produtoLojaRepository.findAll();
    }

    // Endpoint 2: Retorna o histórico de preços de um produto específico
    // Exemplo de uso: acessar http://localhost:8080/api/produtos/1/historico
    @GetMapping("/{id}/historico")
    public List<HistoricoPreco> obterHistorico(@PathVariable Long id) {
        return historicoPrecoRepository.findByProdutoLojaIdOrderByDataCapturaDesc(id);
    }

    @GetMapping("/buscar")
    public List<ProdutoLoja> buscarPorNome(@RequestParam String nome) {
        System.out.println("Usuário pesquisou por: " + nome);
        return produtoLojaRepository.findByNomeContainingIgnoreCase(nome);
    }


    // ==========================================
    // NOVO ENDPOINT: Estatísticas de Preço (GET)
    // Exemplo: /api/produtos/estatisticas?nome=playstation
    // ==========================================
    @GetMapping("/estatisticas")
    public Map<String, Object> obterEstatisticas(@RequestParam String nome) {
        // 1. Busca todos os produtos com aquele nome no banco
        List<ProdutoLoja> produtosEncontrados = produtoLojaRepository.findByNomeContainingIgnoreCase(nome);

        // Se não achar nada, devolve um aviso elegante
        if (produtosEncontrados.isEmpty()) {
            return Map.of("mensagem", "Nenhum produto encontrado com o termo: " + nome);
        }

        BigDecimal menorPreco = null;
        BigDecimal maiorPreco = null;
        ProdutoLoja produtoMaisBarato = null;

        // 2. Passa por cada produto para descobrir quem é o mais barato e o mais caro
        for (ProdutoLoja produto : produtosEncontrados) {
            // Pega o histórico de preços desse produto específico
            List<HistoricoPreco> historico = historicoPrecoRepository.findByProdutoLojaIdOrderByDataCapturaDesc(produto.getId());
            
            if (!historico.isEmpty()) {
                // Como ordenamos do mais novo pro mais velho (Desc), o índice 0 é o preço atual!
                BigDecimal precoAtual = historico.get(0).getValor();

                // Lógica para achar o Menor Preço
                if (menorPreco == null || precoAtual.compareTo(menorPreco) < 0) {
                    menorPreco = precoAtual;
                    produtoMaisBarato = produto; // Guarda o produto inteiro para o usuário ver o link!
                }
                
                // Lógica para achar o Maior Preço
                if (maiorPreco == null || precoAtual.compareTo(maiorPreco) > 0) {
                    maiorPreco = precoAtual;
                }
            }
        }

        // 3. Monta um JSON bonitão com as respostas
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("termoPesquisado", nome);
        resposta.put("totalAnunciosAnalisados", produtosEncontrados.size());
        resposta.put("maiorPrecoEncontrado", maiorPreco);
        resposta.put("menorPrecoEncontrado", menorPreco);
        resposta.put("produtoMaisBarato", produtoMaisBarato);

        return resposta;
    }

    /* Este método precisa estar dentro da classe ProdutoController
    @PostMapping
    public ProdutoLoja cadastrarProduto(@RequestBody ProdutoLoja novoProduto) {
        ProdutoLoja produtoSalvo = produtoLojaRepository.save(novoProduto);

        System.out.println("Novo produto recebido! Buscando preço inicial para: " + produtoSalvo.getUrl());
        
        // Chama o Scraper para pegar o preço na mesma hora
        java.math.BigDecimal precoInicial = scraperService.extrairPreco(produtoSalvo.getUrl(), produtoSalvo.getSeletorCssPreco());

        if (precoInicial != null) {
            HistoricoPreco historico = new HistoricoPreco();
            historico.setProdutoLoja(produtoSalvo);
            historico.setValor(precoInicial);
            historico.setDataCaptura(java.time.LocalDateTime.now());
            historicoPrecoRepository.save(historico);
            System.out.println("Preço inicial salvo com sucesso!");
        }
        
        return produtoSalvo;
    }*/

    @PostMapping("/varredura")
    public Map<String, String> iniciarVarreduraEmMassa(@RequestParam String termo) {
        System.out.println("\n[API] Gatilho acionado! Iniciando busca em massa por: " + termo);
        
        // Dispara o nosso motor de captura!
        scraperService.varrerBuscaMercadoLivre(termo, produtoLojaRepository, historicoPrecoRepository);
        
        // Devolve uma resposta de sucesso para o usuário/admin
        return Map.of("mensagem", "Varredura para o termo '" + termo + "' concluída. Verifique o banco de dados.");
    }
    
}