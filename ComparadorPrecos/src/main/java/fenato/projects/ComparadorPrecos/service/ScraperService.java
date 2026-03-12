package fenato.projects.ComparadorPrecos.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.math.BigDecimal;

// Serviço: Processa informações e age de acordo com a demanda
// Serviço de Scraping 
@Service
public class ScraperService {

    // Extrator de preço específico - por link URL
    public BigDecimal extrairPreco(String url, String seletorCss) {
        try {
            // Finge ser um navegador real para evitar bloqueios simples
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
                    .header("Accept-Language", "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7")
                    .header("Connection", "keep-alive")
                    .header("Upgrade-Insecure-Requests", "1")
                    .header("Sec-Fetch-Dest", "document")
                    .header("Sec-Fetch-Mode", "navigate")
                    .header("Sec-Fetch-Site", "none")
                    .header("Sec-Fetch-User", "?1")
                    .header("Cache-Control", "max-age=0")
                    .timeout(15000)
                    .get();
            // Busca o elemento no HTML usando o seletor CSS
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

    // Método usado para converter preço para BigDecimal ( R$1900,50 -> 1900.50)
    private BigDecimal limparEConverterPreco(String precoTexto) {
        
        String precoLimpo = precoTexto.replaceAll("[^0-9,]", "").replace(",", ".");
        
        try {
            return new BigDecimal(precoLimpo);
        } catch (NumberFormatException e) {
            System.out.println("Erro ao converter o texto para número: " + precoTexto);
            return null;
        }
    }

    // Metodo de scraping de uma página de busca do Mercado Livre
    public void varrerBuscaMercadoLivre(String termoBusca, 
        fenato.projects.ComparadorPrecos.repository.ProdutoLojaRepository produtoRepo, 
        fenato.projects.ComparadorPrecos.repository.HistoricoPrecoRepository historicoRepo) {
        
        // Preparando URL no modelo "https://lista.mercadolivre.com.br/teclado-mecanico"
        String buscaFormatada = termoBusca.replace(" ", "-").toLowerCase();
        String urlBusca = "https://lista.mercadolivre.com.br/" + buscaFormatada;

        System.out.println("\n[SCRAPER] Navegando para: " + urlBusca);

        try {
            // Finge ser um navegador real para evitar bloqueios simples
            Document doc = Jsoup.connect(urlBusca)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
                    .header("Accept-Language", "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7")
                    .header("Connection", "keep-alive")
                    .header("Upgrade-Insecure-Requests", "1")
                    .header("Sec-Fetch-Dest", "document")
                    .header("Sec-Fetch-Mode", "navigate")
                    .header("Sec-Fetch-Site", "none")
                    .header("Sec-Fetch-User", "?1")
                    .header("Cache-Control", "max-age=0")
                    .timeout(15000)
                    .get();

            // Mensagens de DEBUG no terminal ( ver se a classe do seletor de cards de produto existe )
            System.out.println("[DEBUG] Título da página: " + doc.title());
            System.out.println("[DEBUG] O HTML baixado pelo robô tem a classe? " + doc.html().contains("ui-search-layout__item"));

            // Lista os produtos encontrados nos cards pela classe de seleção
            Elements cardsDeProduto = doc.select(".ui-search-layout__item");
            System.out.println("[SCRAPER] Encontramos " + cardsDeProduto.size() + " produtos! Salvando no Neon DB...");

            // Para cada card extraído da página...
            for (Element card : cardsDeProduto) {
                //... pega o nome, o link e o preço do produto
                String nome = card.select("h2, h3, .ui-search-item__title, .poly-component__title").text();
                Element tagLink = card.select("a").first();
                String link = (tagLink != null) ? tagLink.attr("href") : "";
                Element tagPreco = card.select(".andes-money-amount__fraction").first();
                String precoTexto = (tagPreco != null) ? tagPreco.text() : "0";

                // Se o nome não é vazio e o preço não é nulo ...
                if (!nome.isEmpty() && !precoTexto.equals("0")) {
                    // ... limpa o preço e pega a imagem do produto
                    String precoLimpo = precoTexto.replace(".", "").replace(",", ".");
                    java.math.BigDecimal valorConvertido = new java.math.BigDecimal(precoLimpo);
                    org.jsoup.nodes.Element imgElement = card.selectFirst("img");
                    String imgUrl = "";
                
                    if (imgElement != null) {
                        imgUrl = imgElement.hasAttr("data-src") ? imgElement.attr("data-src") : imgElement.attr("src");
                    }

                    // Salva produto no banco utilizando a entidade ProdutoLoja
                    fenato.projects.ComparadorPrecos.model.ProdutoLoja produto = new fenato.projects.ComparadorPrecos.model.ProdutoLoja();
                    produto.setNome(nome);
                    produto.setUrl(link);
                    produto.setSeletorCssPreco(".andes-money-amount__fraction");
                    produto.setUrlImagem(imgUrl);
                    produtoRepo.save(produto);

                    // Salva historico de preco do produto no banco utilizando a entidade HistoricoPreco
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