package util;

public class CriptografiaDeSenha {

    public static String gerarSenhaUsuario(String senha) {
        StringBuilder sb = new StringBuilder();
        String senhaCript = "";
        for (int i = 1; i < 300; i++) {
            char[] toChars = Character.toChars(i);
            char ascii = toChars[0];
            sb.append(ascii);
        }
        sb.append(" ");
        for (int i = 0; i < senha.length(); i++) {
            char ascii = senha.charAt(i);
            int encontrouNaBase = sb.indexOf(String.valueOf(ascii));
            int somandoIndiceEncontradoMasOnze = encontrouNaBase + (i + 12);
            char encontrouNaTabelaAscii = (char) somandoIndiceEncontradoMasOnze;
            senhaCript += encontrouNaTabelaAscii;
        }
        return senhaCript;
    }
}
