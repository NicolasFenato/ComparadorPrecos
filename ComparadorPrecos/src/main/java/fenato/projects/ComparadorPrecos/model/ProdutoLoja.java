package fenato.projects.ComparadorPrecos.model;

import jakarta.persistence.*;

@Entity
public class ProdutoLoja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String nome;

    @Column(length = 2048)
    private String url;
    
    private String seletorCssPreco; 

    public Long getId(){
        return id;
    }

    public String getNome(){
        return nome;
    }

    public String getUrl(){
        return url;
    }

    public String getSeletorCssPreco(){
        return seletorCssPreco;
    }

    public void setId(Long id){
        this.id = id;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public void setSeletorCssPreco(String seletorCssPreco){
        this.seletorCssPreco = seletorCssPreco;
    }
    
}