package fenato.projects.ComparadorPrecos.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;

@Service
public class ScraperService {

    public BigDecimal extrairPreco(String url, String seletorCss) {
        try {
            // 1. Finge ser um navegador real para evitar bloqueios simples
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000) // Aguarda até 10 segundos
                    .get();

            // 2. Busca o elemento no HTML usando o seletor CSS
            Element elementoPreco = doc.selectFirst(seletorCss);

            if (elementoPreco != null) {
                String precoTexto = elementoPreco.text();
                return limparEConverterPreco(precoTexto);
            } else {
                System.out.println("Elemento não encontrado na URL: " + url);
                return null;
            }

        } catch (IOException e) {
            System.out.println("Erro ao conectar no site: " + e.getMessage());
            return null;
        }
    }

    // 3. Método auxiliar para transformar "R$ 1.500,99" ou "1.500,99" em um BigDecimal 1500.99
    private BigDecimal limparEConverterPreco(String precoTexto) {
        // Remove "R$", espaços e pontos de milhar. Troca a vírgula decimal por ponto.
        String precoLimpo = precoTexto.replaceAll("[^0-9,]", "").replace(",", ".");
        
        try {
            return new BigDecimal(precoLimpo);
        } catch (NumberFormatException e) {
            System.out.println("Erro ao converter o texto para número: " + precoTexto);
            return null;
        }
    }

    public void varrerBuscaMercadoLivre(String termoBusca, 
                                        fenato.projects.ComparadorPrecos.repository.ProdutoLojaRepository produtoRepo, 
                                        fenato.projects.ComparadorPrecos.repository.HistoricoPrecoRepository historicoRepo) {
        
        String buscaFormatada = termoBusca.replace(" ", "-").toLowerCase();
        String urlBusca = "https://lista.mercadolivre.com.br/" + buscaFormatada;

        System.out.println("\n[SCRAPER] Navegando para: " + urlBusca);

        try {
            Document doc = Jsoup.connect(urlBusca)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                    .header("Accept-Language", "pt-BR,pt;q=0.9")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                    .get();

            Elements cardsDeProduto = doc.select(".ui-search-layout__item, .ui-search-result__wrapper");
            System.out.println("[SCRAPER] Encontramos " + cardsDeProduto.size() + " produtos! Salvando no Neon DB...");

            for (Element card : cardsDeProduto) {
                String nome = card.select("h2, h3, .ui-search-item__title, .poly-component__title").text();
                Element tagLink = card.select("a").first();
                String link = (tagLink != null) ? tagLink.attr("href") : "";
                
                Element tagPreco = card.select(".andes-money-amount__fraction").first();
                String precoTexto = (tagPreco != null) ? tagPreco.text() : "0";

                if (!nome.isEmpty() && !precoTexto.equals("0")) {
                    // 1. Converte o texto "4.850" para um número de verdade: 4850.00
                    String precoLimpo = precoTexto.replace(".", "").replace(",", ".");
                    java.math.BigDecimal valorConvertido = new java.math.BigDecimal(precoLimpo);

                    // 2. Salva o Produto no Banco
                    fenato.projects.ComparadorPrecos.model.ProdutoLoja produto = new fenato.projects.ComparadorPrecos.model.ProdutoLoja();
                    produto.setNome(nome);
                    produto.setUrl(link);
                    produto.setSeletorCssPreco(".andes-money-amount__fraction");
                    produtoRepo.save(produto);

                    // 3. Salva o Histórico de Preço vinculado a este produto
                    fenato.projects.ComparadorPrecos.model.HistoricoPreco historico = new fenato.projects.ComparadorPrecos.model.HistoricoPreco();
                    historico.setProdutoLoja(produto);
                    historico.setValor(valorConvertido);
                    historico.setDataCaptura(java.time.LocalDateTime.now());
                    historicoRepo.save(historico);

                    System.out.println("✔️ Salvo: " + nome.substring(0, Math.min(nome.length(), 40)) + "... | R$ " + valorConvertido);
                }
            }
            System.out.println("\n[SUCESSO] Todos os produtos foram gravados com sucesso!");

        } catch (Exception e) {
            System.out.println("[SCRAPER] Erro ao acessar a página: " + e.getMessage());
        }
    }

}