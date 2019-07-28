package teste;

import modelo.dto.FiltroEstoque;
import relatorio.RelatorioProduto;

public class Teste {

    public static void main(String[] args) {
        
        RelatorioProduto relatorioProduto = new RelatorioProduto();
        FiltroEstoque filtroEstoque = new FiltroEstoque("SIG", "2019-07-26", "2019-07-28","");
        relatorioProduto.imprimirEstoque(filtroEstoque);
        
    }
    
}
