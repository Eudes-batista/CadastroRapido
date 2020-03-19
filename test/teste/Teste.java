package teste;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Teste {

    public static void main(String[] args) {

        System.out.println("11.593.321/0001-63".length());
        
    }

    private static boolean validarCampoMonetario(String valor) {
        int tamanhoMaximoDoCampo = 12;
        if (valor.trim().length() >= tamanhoMaximoDoCampo) {
            return true;
        }
        Pattern pattern = Pattern.compile("[aA-zZ]");
        Matcher matcher = pattern.matcher(valor.trim());
        return matcher.find();
    }
}
