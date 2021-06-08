package relatorio;

import controle.ConectaBanco;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import javafx.scene.control.Alert;
import javax.swing.JFrame;
import modelo.dto.FiltroProduto;
import modelo.dto.FiltroProdutoVendido;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

public class RelatorioProduto {

    private final ConectaBanco conectaBanco;

    public RelatorioProduto() {
        this.conectaBanco = new ConectaBanco();
    }

    public void imprimirTodosProdutos(FiltroProduto filtroProduto) {
        String sql = "select \n"
                + " PRREFERE as REFERENCIA\n"
                + ",PRCODBAR as BARRAS\n"
                + ",PRDESCRI as DESCRICAO\n"
                + ",EEPBRTB1 as PRECO\n"
                + ",EET2PVD1 as PRECO_ATACDO\n"
                + ",PRQTDATA as QUANTIDADE_ATACADO\n"
                + ",SUM(EEESTATU) as ESTOQUE\n"
                + "from\n"
                + " scea07 \n"
                + "left outer join\n";
        sql += " scea01 on(prrefere=eerefere and eecodemp like '%" + filtroProduto.getEmpresa() + "%')\n";
        sql += "where \n"
                + "  prrefere like '%" + filtroProduto.getProduto() + "%' and PRDATCAN is null \n";
        if (filtroProduto.getDataInicial() != null) {
            sql += "and PRULTALT between '" + filtroProduto.getDataInicial() + " 00:00:00' and '" + filtroProduto.getDataFinal() + " 23:59:59' \n";
        }
        sql += " AND PRCGRUPO like '%" + filtroProduto.getGrupo() + "%' AND PRSUBGRP like '%" + filtroProduto.getSubGrupo() + "%' ";
        sql += "group by\n"
                + " PRREFERE,PRCODBAR,PRDESCRI,EEPBRTB1,EET2PVD1,PRQTDATA"
                + " order by PRDESCRI";
        if (!this.conectaBanco.conexao()) {
            return;
        }
        if (!this.conectaBanco.executaSQL(sql)) {
            return;
        }
        try {
            ResultSet resultSet = this.conectaBanco.getResultSet();
            JRResultSetDataSource dataSource = new JRResultSetDataSource(resultSet);
            InputStream inputStream = getClass().getResourceAsStream("/relatorio/produtos.jasper");
            JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, new HashMap<>(), dataSource);
            jasperPrint.setName("Relatorio de Produtos");
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setTitle("Relatorio de Produtos");
            jasperViewer.setDefaultCloseOperation(JasperViewer.DISPOSE_ON_CLOSE);
            jasperViewer.setResizable(true);
            jasperViewer.setExtendedState(JFrame.MAXIMIZED_BOTH);
            jasperViewer.setVisible(true);
        } catch (JRException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("CADASTRO RAPIDO");
            alert.setContentText("Erro ao consultar produtos");
            alert.show();
        }
    }

    public void imprimirProdutosVendidos(FiltroProdutoVendido filtroProdutoVendido) {
        String sql = "select \n"
                + "  POREFERE as referencia,\n"
                + "  sum(POQUANTI) as quantidade,\n"
                + "  sum(POVALITE) as total_vendido,\n"
                + "  PODESCRI as descricao,\n"
                + "  POUNIDAD as  unidade,\n"
                + "  MAX(PODTORCA) as data_ultima_venda \n"
                + "from \n"
                + "  sosa06\n"
                + "inner join\n"
                + "  sosa01 on(OSORCAOS=POORCAOS)\n"
                + "where \n"
                + "    substr(OSORCAOS,2,3) like '%" + filtroProdutoVendido.getCaixa() + "%'\n"
                + "and OSVENDED like '%" + filtroProdutoVendido.getVendedor() + "%'\n";
        String referencia = filtroProdutoVendido.getReferencia();
        if (referencia != null && !referencia.isEmpty()) {
            sql += "and POREFERE ='" + referencia + "'\n";
        }
        sql += "and OSLIQUID >= '" + filtroProdutoVendido.getDataInicial() + " 00:00:00' and OSLIQUID <= '" + filtroProdutoVendido.getDataFinal() + " 23:59:59'\n"
                + "group by \n"
                + "  referencia,\n"
                + "  descricao,\n"
                + "  unidade\n"
                + "order by \n"
                + "  total_vendido desc";
        if (!this.conectaBanco.conexao()) {
            return;
        }
        if (!this.conectaBanco.executaSQL(sql)) {
            return;
        }
        try {
            ResultSet resultSet = this.conectaBanco.getResultSet();
            if (!resultSet.first()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("CADASTRO RAPIDO");
                alert.setContentText("Não existe movimentação nesse período.");
                alert.show();
                return;
            }
            resultSet.beforeFirst();
            JRResultSetDataSource dataSource = new JRResultSetDataSource(resultSet);
            InputStream inputStream = getClass().getResourceAsStream("/relatorio/produtos_vendidos.jasper");
            HashMap<String, Object> parametros = new HashMap<>();
            parametros.put("periodo", filtroProdutoVendido.getPeriodo());
            parametros.put("informacaoCaixa", filtroProdutoVendido.getInformacaoCaixa());
            JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, parametros, dataSource);
            jasperPrint.setName("Relatorio de produtos vendidos");
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setTitle("Relatorio de produtos vendidos");
            jasperViewer.setDefaultCloseOperation(JasperViewer.DISPOSE_ON_CLOSE);
            jasperViewer.setResizable(true);
            jasperViewer.setExtendedState(JFrame.MAXIMIZED_BOTH);
            jasperViewer.setVisible(true);
        } catch (JRException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("CADASTRO RAPIDO");
            alert.setContentText("Erro ao consultar produtos");
            alert.show();
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("CADASTRO RAPIDO");
            alert.setContentText(ex.getMessage());
            alert.show();
        }
    }

    public void imprimirEstoque(FiltroProduto filtroProduto) {
        String sql = "SELECT \n"
                + "     MCCODEMP\n"
                + "     MCTIPMOV,\n"
                + "     MCNUMERO,\n"
                + "     MCDATMOV,\n"
                + "	MICODEMP,\n"
                + "	MITIPMOV,\n"
                + "	MINUMERO,\n"
                + "	MINUMITE,\n"
                + "	MIREFERE,\n"
                + "	PRDESCRI,\n"
                + "	MIDATMOV,\n"
                + "	MIQUANTI,\n"
                + "	MIPRUNIT,\n"
                + "	MCDATMOV,\n"
                + "	MCHISTOR\n"
                + "FROM \n"
                + "    SCEA03\n"
                + "LEFT OUTER JOIN\n"
                + "	 SCEA02 \n"
                + "	 ON (\n"
                + "      MICODEMP = MCCODEMP\n"
                + "  AND MINUMERO = MCNUMERO\n"
                + "  AND MITIPMOV   = MCTIPMOV\n"
                + "  AND MIDATMOV = MCDATMOV\n"
                + ")\n"
                + "LEFT OUTER JOIN\n"
                + "SCEA01\n"
                + "  ON(\n"
                + "    PRREFERE=MIREFERE\n"
                + "  )\n"
                + "WHERE\n"
                + "  MCCODEMP like '%" + filtroProduto.getEmpresa() + "%' and MIREFERE like '%" + filtroProduto.getProduto() + "%'"
                + " AND PRCGRUPO like '%" + filtroProduto.getGrupo() + "%' AND PRSUBGRP like '%" + filtroProduto.getSubGrupo() + "%' ";

        if (filtroProduto.getDataInicial() != null) {
            sql += "and MCDATMOV between '" + filtroProduto.getDataInicial() + " 00:00:00' and '" + filtroProduto.getDataFinal() + " 23:59:59'";
        }
        if (!filtroProduto.getTipoDeMovimentacao().isEmpty() && filtroProduto.getMovimentacao().isEmpty()) {
            sql += " AND MITIPMOV like '%" + filtroProduto.getTipoDeMovimentacao() + "%'";
        }
        if (!filtroProduto.getMovimentacao().isEmpty()) {
            sql += " AND MITIPMOV like '%" + filtroProduto.getMovimentacao() + "%'";
        }
        if (!this.conectaBanco.conexao()) {
            return;
        }
        if (!this.conectaBanco.executaSQL(sql)) {
            this.conectaBanco.desconecta();
            return;
        }
        try {
            ResultSet resultSet = this.conectaBanco.getResultSet();
            JRResultSetDataSource dataSource = new JRResultSetDataSource(resultSet);
            InputStream inputStream = getClass().getResourceAsStream("/relatorio/RelatorioEstoque.jasper");
            JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, new HashMap<>(), dataSource);
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setTitle("Relatorio de Estoque");
            jasperViewer.setDefaultCloseOperation(JasperViewer.DISPOSE_ON_CLOSE);
            jasperViewer.setResizable(true);
            jasperViewer.setExtendedState(JFrame.MAXIMIZED_BOTH);
            jasperViewer.setVisible(true);
        } catch (JRException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("CADASTRO RAPIDO");
            alert.setContentText("Erro ao consultar produtos");
            alert.show();
        } finally {
            conectaBanco.desconecta();
        }
    }

}
