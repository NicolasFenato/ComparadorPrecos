package fenato.projects.ComparadorPrecos.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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

}