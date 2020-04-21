package teste;

import modelo.dto.CorrentistaFiltro;
import relatorio.correntista.RelatorioCorrentista;

public class Teste {

    public static void main(String[] args) {

        RelatorioCorrentista relatorioCorrentista = new RelatorioCorrentista();
        
        CorrentistaFiltro correntistaFiltro = new CorrentistaFiltro();
        correntistaFiltro.setCliente("1199");
        correntistaFiltro.setDataInicial("2020-04-01");
        correntistaFiltro.setDataFinal("2020-04-30");
        
        relatorioCorrentista.setCliente("EUDES BATISTA DOS SANTOS");
        relatorioCorrentista.setLimiteEmCredito("999.999,00");
        relatorioCorrentista.setSaldoDevedor("999.999,00");
        relatorioCorrentista.setSaldoDisponivel("999.999,00");
        
        relatorioCorrentista.imprimirCorrentista(correntistaFiltro);
        
    }
}
