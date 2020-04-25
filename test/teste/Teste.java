package teste;

import modelo.Usuario;
import servico.UsuarioService;
import util.CriptografiaDeSenha;

public class Teste {

    public static void main(String[] args) {
        
        UsuarioService usuarioService = new UsuarioService();
        
        Usuario usuario = usuarioService.buscarUsuario("NENA", CriptografiaDeSenha.gerarSenhaUsuario("4321"));
        
        System.out.println("usuario = " + usuario);
    }

}
