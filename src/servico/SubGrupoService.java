package servico;

import controle.ConectaBanco;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;
import modelo.SubGrupo;

public class SubGrupoService {

    private final ConectaBanco conecta = new ConectaBanco();

    public boolean salvar(SubGrupo subGrupo) {
        if (!this.conecta.conexao()) {
            return false;
        }
        try {
            String sql = "INSERT INTO LAPT52 (T52CDSGR,T52DSSGR) values(?,?)";
            PreparedStatement pst = this.conecta.getConnection().prepareStatement(sql);
            pst.setString(1, subGrupo.getCodigo());
            pst.setString(2, subGrupo.getNome());
            pst.executeUpdate();
            this.conecta.getConnection().commit();
            pst.close();
            this.conecta.desconecta();
            return true;
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            try {
                this.conecta.getConnection().rollback();
            } catch (SQLException ex1) {
                alert.setTitle("Erro");
                alert.setContentText("Erro ao incluir SubGrupo");
                alert.show();
            }
            alert.setTitle("Erro");
            alert.setContentText("Erro ao incluir SubGrupo");
            alert.show();
        }
        this.conecta.desconecta();
        return false;
    }

    public boolean alterar(SubGrupo subGrupo) {
        if (!this.conecta.conexao()) {
            return false;
        }
        try {
            String sql = "update LAPT52 set T52DSSGR=? where T52CDSGR=?";
            PreparedStatement pst = this.conecta.getConnection().prepareStatement(sql);
            pst.setString(1, subGrupo.getNome());
            pst.setString(2, subGrupo.getCodigo());
            pst.executeUpdate();
            this.conecta.getConnection().commit();
            pst.close();
            this.conecta.desconecta();
            return true;
        } catch (SQLException ex) {
            try {
                this.conecta.getConnection().rollback();
            } catch (SQLException ex1) {
            }
            this.conecta.desconecta();
            return false;
        }
    }

    public boolean excluirMovimento(String grupo) {
        if (!this.conecta.conexao()) {
            return false;
        }
        try {
            String sql = "delete from LAPT52 where T52CDSGR=?";
            PreparedStatement pst = this.conecta.getConnection().prepareStatement(sql);
            pst.setString(1, grupo);
            pst.execute();
            this.conecta.getConnection().commit();
            pst.close();
            this.conecta.desconecta();
            return true;
        } catch (SQLException ex) {
            this.conecta.desconecta();
            return false;
        }
    }

    public List<SubGrupo> listarSubGrupos(String pesquisa) throws SQLException {
        List<SubGrupo> grupos = new ArrayList<>();
        if (!this.conecta.conexao()) {
            return grupos;
        }
        String sql = "SELECT T52CDSGR as codigo,T52DSSGR as nome FROM LAPT52 where T52DSSGR like '%" + pesquisa + "%' ORDER BY T52DSSGR";
        if (!this.conecta.executaSQL(sql)) {
            this.conecta.desconecta();
            return grupos;
        }
        if (!this.conecta.getResultSet().first()) {
            this.conecta.desconecta();
            return grupos;
        }
        do {
            grupos.add(new SubGrupo(this.conecta.getResultSet().getString("codigo"), this.conecta.getResultSet().getString("nome")));
        } while (this.conecta.getResultSet().next());
        this.conecta.desconecta();
        return grupos;
    }

    public SubGrupo buscarSubGrupo(String codigo) {
        String sql;
        if (!this.conecta.conexao()) {
            return null;
        }
        sql = "SELECT T52CDSGR as codigo,T52DSSGR as nome FROM LAPT52 where T52CDSGR='" + codigo + "'";
        if (!this.conecta.executaSQL(sql)) {
            this.conecta.desconecta();
            return null;
        }
        try {
            if (!this.conecta.getResultSet().first()) {
                this.conecta.desconecta();
                return null;
            }
            return new SubGrupo(this.conecta.getResultSet().getString("codigo"), this.conecta.getResultSet().getString("nome"));
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setContentText("SubGrupo não existe.");
            alert.show();
        }
        this.conecta.desconecta();
        return null;
    }
}
