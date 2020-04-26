package teste;

import modelo.dto.FiltroProduto;
import relatorio.RelatorioProduto;

public class Teste {

    public static void main(String[] args) {
        
        RelatorioProduto relatorioProduto = new RelatorioProduto();
        
        FiltroProduto filtroProduto = new FiltroProduto();
        filtroProduto.setProduto("");
        filtroProduto.setEmpresa("");
        filtroProduto.setDataInicial("2020-01-01");
        filtroProduto.setDataFinal("2020-04-26");
        
        relatorioProduto.imprimirTodosProdutos(filtroProduto);
    }

}
