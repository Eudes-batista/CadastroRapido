package servico;

import controle.ConectaBanco;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import modelo.ItemMovimento;
import modelo.Movimento;

public class ItemMovimentoService {

    private final ConectaBanco conecta = new ConectaBanco();

    public boolean salvar(ItemMovimento itemMovimento) {
        if (conecta.conexao()) {
            try {
                itemMovimento.setSeguenciaItem(buscarSequenciaItem(itemMovimento.getMovimento()));
                String sql = "INSERT INTO SCEA03 (MICODEMP,MITIPMOV,MINUMERO,MINUMITE,MIREFERE,MIDATMOV,MIQUANTI,MIPRUNIT)";
                sql += " VALUES('" + itemMovimento.getMovimento().getEmpresa()
                        + "','" + itemMovimento.getMovimento().getTipo()
                        + "','" + itemMovimento.getMovimento().getDocumento()
                        + "','" + itemMovimento.getSeguenciaItem()
                        + "','" + itemMovimento.getProduto().split("-")[0].trim()
                        + "','" + new SimpleDateFormat("yyyy-MM-dd").format(itemMovimento.getMovimento().getDataMovimento())
                        + "'," + itemMovimento.getQuantidade()
                        + "," + itemMovimento.getPrecoUnitario() + ")";
                Statement pst = conecta.getConnection().createStatement();
                pst.execute(sql);
                conecta.getConnection().commit();
                pst.close();
                movimentarEstoqueAtual(itemMovimento);
                return true;
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                try {
                    conecta.getConnection().rollback();
                } catch (SQLException ex1) {
                    alert.setTitle("Erro");
                    alert.setContentText("Erro ao incluir Movimento. \n" + ex1.getMessage());
                    alert.show();
                }
                alert.setTitle("Erro");
                alert.setContentText("Erro ao incluir Movimento. \n " + ex.getMessage());
                alert.show();
            }
            conecta.desconecta();
        }
        return false;
    }

    private void movimentarEstoqueAtual(ItemMovimento itemMovimento) throws SQLException {
        String produto;
        String produtos[] = itemMovimento.getProduto().split(",");
        for (String p : produtos) {
            produto = p.split("-")[0].trim();
            String sql = "select (sum( iif( substr(MITIPMOV,1,1) = 'E' , MIQUANTI ,0 ) ) - sum(iif(substr(MITIPMOV,1,1)='S',MIQUANTI,0))) as estoque from scea03 where MIREFERE in('" + produto + "') and MICODEMP='" + itemMovimento.getMovimento().getEmpresa() + "'";
            if (!conecta.executaSQL(sql)) {
                break;
            }
            if (!conecta.getResultSet().first()) {
                break;
            }
            double estoque = conecta.getResultSet().getDouble("estoque");
            sql = "update scea07 set EEESTATU=? where EEREFERE in('" + produto + "') and EECODEMP='" + itemMovimento.getMovimento().getEmpresa() + "'";
            PreparedStatement preparedStatement = conecta.getConnection().prepareStatement(sql);
            preparedStatement.setDouble(1, estoque);
            preparedStatement.execute();
            conecta.getConnection().commit();
            preparedStatement.close();
            this.atualizarInformacaoProduto(produto);
        }
    }

    public boolean excluirItem(ItemMovimento itemMovimento) {
        if (conecta.conexao()) {
            try {
                String sql = "delete from SCEA03 where MINUMITE=? and MICODEMP=? and MITIPMOV=? and MINUMERO=?";
                PreparedStatement pst = conecta.getConnection().prepareStatement(sql);
                pst.setString(1, itemMovimento.getSeguenciaItem());
                pst.setString(2, itemMovimento.getMovimento().getEmpresa());
                pst.setString(3, itemMovimento.getMovimento().getTipo());
                pst.setString(4, itemMovimento.getMovimento().getDocumento());
                pst.execute();
                conecta.getConnection().commit();
                pst.close();
                movimentarEstoqueAtual(itemMovimento);
                return true;
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setContentText("Erro ao excluir Item Movimento. \n" + ex.getMessage());
                alert.show();
            }
        }
        return false;
    }

    public boolean excluirTodosItens(List<ItemMovimento> itemMovimentos) {
        if (conecta.conexao()) {
            try {
                String sql = "delete from SCEA03 where MICODEMP=? and MITIPMOV=? and MINUMERO=?";
                PreparedStatement pst = conecta.getConnection().prepareStatement(sql);
                pst.setString(1, itemMovimentos.get(0).getMovimento().getEmpresa());
                pst.setString(2, itemMovimentos.get(0).getMovimento().getTipo());
                pst.setString(3, itemMovimentos.get(0).getMovimento().getDocumento());
                pst.execute();
                conecta.getConnection().commit();
                pst.close();
                ItemMovimento itemMovimento = itemMovimentos.get(0);
                itemMovimento.setProduto(itemMovimentos.stream().map(ItemMovimento::getProduto).collect(Collectors.joining(",")));
                movimentarEstoqueAtual(itemMovimento);
                return true;
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setContentText("Erro ao excluir Item Movimento. \n" + ex.getMessage());
                alert.show();
            }
        }
        return false;
    }

    public List<ItemMovimento> listarMovimento(Movimento movimento) throws SQLException {
        if (conecta.conexao()) {
            String sql = "select \n"
                    + "   MIREFERE as REFERENCIA\n"
                    + "  ,PRDESCRI as DESCRICAO\n"
                    + "  ,MIQUANTI as QUANTIDADE\n"
                    + "  ,EEPLQTB1 as PRECO\n"
                    + "  ,(MIQUANTI*EEPLQTB1) as TOTAL\n"
                    + "  ,MINUMITE as ITEM\n"
                    + "from \n"
                    + "scea03 \n"
                    + "  inner join \n"
                    + "scea07 on(EEREFERE=MIREFERE) \n"
                    + "  inner join \n"
                    + "SCEA01 on(PRREFERE=EEREFERE) \n"
                    + "where\n"
                    + "    MICODEMP='" + movimento.getEmpresa() + "'\n"
                    + "AND MITIPMOV='" + movimento.getTipo() + "'  \n"
                    + "AND MINUMERO='" + movimento.getDocumento() + "'\n"
                    + "group by\n"
                    + "   MIREFERE,PRDESCRI,MIQUANTI,EEPLQTB1,MINUMITE";
            if (conecta.executaSQL(sql)) {
                List<ItemMovimento> itemMovimentos = new ArrayList<>();
                if (conecta.getResultSet().first()) {
                    do {
                        String refrencia = conecta.getResultSet().getString("REFERENCIA"), descricao = conecta.getResultSet().getString("DESCRICAO"), produto;
                        produto = refrencia + " - " + descricao;
                        ItemMovimento itemMovimento = new ItemMovimento();
                        itemMovimento.setCheckBox(new CheckBox());
                        itemMovimento.setMovimento(movimento);
                        itemMovimento.setSeguenciaItem(conecta.getResultSet().getString("ITEM"));
                        itemMovimento.setProduto(produto);
                        itemMovimento.setQuantidade(conecta.getResultSet().getDouble("QUANTIDADE"));
                        itemMovimento.setPrecoUnitario(conecta.getResultSet().getDouble("PRECO"));
                        itemMovimento.setPrecoTotal(conecta.getResultSet().getDouble("TOTAL"));
                        itemMovimentos.add(itemMovimento);
                    } while (conecta.getResultSet().next());
                    return itemMovimentos;
                }
            }
        }
        return null;
    }

    private String buscarSequenciaItem(Movimento movimento) throws SQLException {
        String sql = "select max(cast(MINUMITE as Integer))+1 as qtd from scea03 where MICODEMP ='" + movimento.getEmpresa() + "' and MITIPMOV='" + movimento.getTipo() + "' and MINUMERO='" + movimento.getDocumento() + "';";
        if (conecta.executaSQL(sql)) {
            if (conecta.getResultSet().first()) {
                String item = conecta.getResultSet().getString("qtd") == null ? "1" : conecta.getResultSet().getString("qtd");
                return item;
            }
        }
        return "1";
    }

    private void atualizarInformacaoProduto(String produto) throws SQLException {
        try ( PreparedStatement preparedStatement = conecta.getConnection().prepareStatement("update scea01 set PRULTALT=? where PRREFERE=?")) {
            preparedStatement.setDate(1, new Date(new java.util.Date().getTime()));
            preparedStatement.setString(2, produto);
            preparedStatement.execute();
            conecta.getConnection().commit();
        }
    }

}
