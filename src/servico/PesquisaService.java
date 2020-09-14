package servico;

import controle.ConectaBanco;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import modelo.Produto;

public class PesquisaService {

    private final ConectaBanco conectaBanco = new ConectaBanco();
    private boolean chekcCancelados;
    private final ObservableList<Produto> produtos = FXCollections.observableArrayList();

    public ObservableList<Produto> listarProdutos(String pesquisa) {
        if (!this.conectaBanco.conexao()) {
            return this.produtos;
        }
        if (!this.conectaBanco.executaSQL(this.montarSql(this.chekcCancelados, pesquisa))) {
            return this.produtos;
        }
        try {
            if (!this.conectaBanco.getResultSet().first()) {
                return this.produtos;
            }
            this.produtos.clear();
            do {
                Produto produto = new Produto(
                        this.conectaBanco.getResultSet().getString("referencia"),
                        this.conectaBanco.getResultSet().getString("descricao"),
                        this.conectaBanco.getResultSet().getDouble("preco"),
                        this.conectaBanco.getResultSet().getDouble("preco_atacado"),
                        this.conectaBanco.getResultSet().getDouble("quantidade_atacado"),
                        conectaBanco.getResultSet().getString("codigo_barra"));
                produto.setPrecoEspecial(this.conectaBanco.getResultSet().getDouble("preco_especial"));
                this.produtos.add(produto);
            } while (this.conectaBanco.getResultSet().next());
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERRO");
            alert.setContentText("Erro ao consultar\n detalhe do erro " + ex.getMessage());
            alert.show();
        }
        this.conectaBanco.desconecta();
        return this.produtos;
    }

    public void setChekcCancelados(boolean chekcCancelados) {
        this.chekcCancelados = chekcCancelados;
    }

    private String montarSql(boolean produtoCancelado, String pesquisa) {
        String sql = "select first 50 \n"
                + "   PRREFERE as referencia,\n"
                + "   MAX(PRCODBAR) as codigo_barra,\n"
                + "   MAX(PRDESCRI) as descricao,\n"
                + "   MAX(EEPLQTB1) as preco,\n"
                + "   MAX(EET2PVD1) as preco_atacado,\n"
                + "   MAX(PRQTDATA) as quantidade_atacado,\n"
                + "   MAX(EET3PVD1) as preco_especial \n"
                + "from \n"
                + "   scea01 \n"
                + "left outer join \n"
                + "   scea07 on(eerefere=prrefere) \n"
                + "left outer join \n"
                + "   scea09 on(rarefere=prrefere) \n"
                + "where \n";
        String cancelados = produtoCancelado ? "PRDATCAN IS NOT NULL \n" : "PRDATCAN is null \n";
        sql += " "+cancelados;
        sql += this.adicionarCondicao(pesquisa);
        sql += "group by \n"
                + "  referencia";
        return sql;
    }

    private String adicionarCondicao(String pesquisa) {
        String[] palavras = pesquisa.split(" ");
        String sqlCondicoes = "";
        for (String palavra : palavras) {
            sqlCondicoes += "AND (\n"
                    + "   PRDESCRI like '%" + palavra + "%'\n"
                    + "OR PRAPLICA like '%" + palavra + "%'\n"
                    + "OR PRCODBAR = '" + palavra + "'\n"
                    + "OR PRREFERE = '" + palavra + "'\n"
                    + "OR RAREFAUX = '" + palavra + "'\n"
                    + ") \n";
        }
        return sqlCondicoes;
    }

}
