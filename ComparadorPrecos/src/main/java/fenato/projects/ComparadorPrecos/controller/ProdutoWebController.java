package fenato.projects.ComparadorPrecos.controller;

import fenato.projects.ComparadorPrecos.repository.ProdutoLojaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProdutoWebController {

    @Autowired
    private ProdutoLojaRepository produtoLojaRepository;

    // Quando você acessar http://localhost:8080/ no navegador
    @GetMapping("/")
    public String exibirPaginaInicial(Model model) {
        
        // 1. Busca todos os PlayStations (e outros produtos) no banco
        var listaDeProdutos = produtoLojaRepository.findAll();
        
        // 2. Cria uma variável chamada "produtos" e coloca a lista dentro dela
        // O Thymeleaf vai usar esse nome "produtos" lá no HTML
        model.addAttribute("produtos", listaDeProdutos);
        
        // 3. Avisa o Spring para procurar e abrir o arquivo "index.html"
        return "index"; 
    }
}