package teste;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Teste {

    public static void main(String[] args) {

        boolean invalid = validarCampoMonetario("789680670006a");
        System.out.println("invalid = " + invalid);
        
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
