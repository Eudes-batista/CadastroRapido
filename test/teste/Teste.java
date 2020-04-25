package teste;

import exception.CorrentistaException;
import java.util.List;
import modelo.Correntista;
import modelo.dto.CorrentistaFiltro;
import servico.CorrentistaService;

public class Teste {

    public static void main(String[] args) {
        
        CorrentistaService correntistaService = new CorrentistaService();
        
        CorrentistaFiltro correntistaFiltro = new CorrentistaFiltro();
        
        correntistaFiltro.setCliente("00043");
        correntistaFiltro.setDataInicial("2020-04-01");
        correntistaFiltro.setDataFinal("2020-04-25");
        
        try {
            List<Correntista> movimento = correntistaService.listarMovimentacaoCorrentista(correntistaFiltro);
            movimento.forEach(System.out::println);
        } catch (CorrentistaException ex) {
            System.out.println("ex = " + ex);
        }
        
    }

}
