package teste;

import java.text.NumberFormat;

public class Teste {

    public static void main(String[] args) {
        
        String tipo ="ENAQ";
        
        System.out.println("tipo = " + tipo.startsWith("EN"));
        
        double valor = 2500;
        
        System.out.println("valor = " + NumberFormat.getInstance().format(valor));
        
    }
    
}
