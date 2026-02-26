package fenato.projects.ComparadorPrecos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class HistoricoPreco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal valor; 
    
    private LocalDateTime dataCaptura;

    @ManyToOne
    @JoinColumn(name = "produto_loja_id")
    private ProdutoLoja produtoLoja;

    public Long getId(){
        return id;
    }

    public BigDecimal getValor(){
        return valor;
    }

    public LocalDateTime getDataCaptura(){
        return dataCaptura;
    }

    public ProdutoLoja getProdutoLoja(){
        return produtoLoja;
    }

    public void setId(Long id){
        this.id = id;
    }

    public void setValor(BigDecimal valor){
        this.valor = valor;
    }

    public void setDataCaptura(LocalDateTime dataCaptura){
        this.dataCaptura = dataCaptura;
    }

    public void setProdutoLoja(ProdutoLoja produtoLoja){
        this.produtoLoja = produtoLoja;
    }

}