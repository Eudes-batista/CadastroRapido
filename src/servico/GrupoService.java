package servico;

import controle.ConectaBanco;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;
import modelo.Grupo;

public class GrupoService {

    private final ConectaBanco conecta = new ConectaBanco();

    public boolean salvar(Grupo grupo) {
        if (this.conecta.conexao()) {
            try {
                String sql = "INSERT INTO LAPT51 (T51CDGRP,T51DSGRP) values(?,?)";
                PreparedStatement pst = this.conecta.getConnection().prepareStatement(sql);
                pst.setString(1, grupo.getCodigo());
                pst.setString(2, grupo.getNome());
                pst.executeUpdate();
                this.conecta.getConnection().commit();
                 pst.close();
                return true;
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                try {
                    this.conecta.getConnection().rollback();
                } catch (SQLException ex1) {
                    alert.setTitle("Erro");
                    alert.setContentText("Erro ao incluir Grupo");
                    alert.show();
                }
                alert.setTitle("Erro");
                alert.setContentText("Erro ao incluir Grupo");
                alert.show();
            }
            this.conecta.desconecta();
        }
        return false;
    }

    public boolean alterar(Grupo grupo) {
        if (this.conecta.conexao()) {
            try {
                String sql = "update LAPT51 set T51DSGRP=? where T51CDGRP=?";
                PreparedStatement pst = this.conecta.getConnection().prepareStatement(sql);
                pst.setString(1, grupo.getNome());
                pst.setString(2, grupo.getCodigo());
                pst.executeUpdate();
                this.conecta.getConnection().commit();
                 pst.close();
                return true;
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                try {
                    this.conecta.getConnection().rollback();
                } catch (SQLException ex1) {
                    alert.setTitle("Erro");
                    alert.setContentText("Erro ao alterar Grupo");
                    alert.show();
                }
                alert.setTitle("Erro");
                alert.setContentText("Erro ao alterar Grupo");
                alert.show();
            }
            this.conecta.desconecta();
        }
        return false;
    }

    public void excluirMovimento(String grupo) {
        if (this.conecta.conexao()) {
            try {
                String sql = "delete from LAPT51 where T51CDGRP=?";
                PreparedStatement pst = this.conecta.getConnection().prepareStatement(sql);
                pst.setString(1, grupo);
                pst.execute();
                this.conecta.getConnection().commit();
                 pst.close();
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setContentText("Erro ao excluir Grupo");
                alert.show();
            }
        }
    }

    public List<Grupo> listarGrupos(String pesquisa) throws SQLException {
        List<Grupo> grupos = new ArrayList<>();
        if (this.conecta.conexao()) {
            String sql = "SELECT T51CDGRP as codigo,T51DSGRP as nome FROM LAPT51 where T51DSGRP like '%"+pesquisa+"%' ORDER BY T51DSGRP";
            if (this.conecta.executaSQL(sql)) {
                if (this.conecta.getResultSet().first()) {
                    do {
                        grupos.add(new Grupo(this.conecta.getResultSet().getString("codigo"), this.conecta.getResultSet().getString("nome")));
                    } while (this.conecta.getResultSet().next());
                    return grupos;
                }
            }
        }
        return grupos;
    }

    public Grupo buscarGrupo(String codigo) {
        String sql;
        if (!this.conecta.conexao()) {
            return null;
        }
        sql = "SELECT T51CDGRP as codigo,T51DSGRP as nome FROM LAPT51 where T51CDGRP='" + codigo + "'";
        if (!this.conecta.executaSQL(sql)) {
            this.conecta.desconecta();
            return null;
        }
        try {
            if (!this.conecta.getResultSet().first()) {
                this.conecta.desconecta();
                return null;
            }
            return new Grupo(this.conecta.getResultSet().getString("codigo"), this.conecta.getResultSet().getString("nome"));
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setContentText("Grupo não existe.");
            alert.show();
        }
        this.conecta.desconecta();
        return null;
    }
}
