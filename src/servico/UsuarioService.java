package servico;

import controle.ConectaBanco;
import exception.UsuarioException;
import java.sql.SQLException;
import modelo.Usuario;

public class UsuarioService {

    private final ConectaBanco conectaBanco;

    public UsuarioService() {
        this.conectaBanco = new ConectaBanco();
    }

    public Usuario buscarUsuario(String nomeDeUsuario, String senha) throws UsuarioException {
        if (!this.conectaBanco.conexao()) {
            throw new UsuarioException("Não foi possivel se conectar com o banco de dados");
        }
        String sql = "select L2NOMEUS,L2SENHAU,L2GERENT from lapa02 where L2NOMEUS='" + nomeDeUsuario + "' and L2SENHAU='" + senha + "'";
        if (!this.conectaBanco.executaSQL(sql)) {
            this.conectaBanco.desconecta();
            throw new UsuarioException("Não foi possivel consultar o usuario.");
        }
        try {
            this.conectaBanco.getResultSet().first();
            Usuario usuario = new Usuario();
            usuario.setUsuario(this.conectaBanco.getResultSet().getString("L2NOMEUS"));
            usuario.setSenha(this.conectaBanco.getResultSet().getString("L2SENHAU"));
            usuario.setPermissao(this.conectaBanco.getResultSet().getString("L2GERENT"));
            this.conectaBanco.desconecta();
            return usuario;
        } catch (SQLException ex) {
            this.conectaBanco.desconecta();
            throw new UsuarioException("Não foi possivel consultar o usuario.");
        }
    }

}
