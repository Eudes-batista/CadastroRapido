package teste;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class Teste {

    public static void main(String[] args) {

        String dataInicial = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).toString();
        System.out.println("dataInicial = " + dataInicial);
        String dataFinal = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).toString();
        System.out.println("dataFinal = " + dataFinal);
    }
}
