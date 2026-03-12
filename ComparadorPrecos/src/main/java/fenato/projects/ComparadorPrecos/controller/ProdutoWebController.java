package fenato.projects.ComparadorPrecos.controller;

import fenato.projects.ComparadorPrecos.repository.ProdutoLojaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// Controller: Interpreta requisições web
@Controller
public class ProdutoWebController {

    @Autowired
    private ProdutoLojaRepository produtoLojaRepository;

    // Requisição para página inicial
    @GetMapping("/")
    public String exibirPaginaInicial(Model model) {
        var listaDeProdutos = produtoLojaRepository.findAll();
        model.addAttribute("produtos", listaDeProdutos);
        return "index"; 
    }
}