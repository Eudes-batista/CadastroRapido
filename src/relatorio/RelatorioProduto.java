package relatorio;

import controle.ConectaBanco;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.HashMap;
import javafx.scene.control.Alert;
import modelo.dto.FiltroEstoque;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

public class RelatorioProduto {

    private ConectaBanco conectaBanco;

    public RelatorioProduto() {
        this.conectaBanco = new ConectaBanco();
    }

    public void imprimirTodosProdutos() {
        String sql = "select \n"
                + " PRREFERE as REFERENCIA\n"
                + ",PRCODBAR as BARRAS\n"
                + ",PRDESCRI as DESCRICAO\n"
                + ",EEPBRTB1 as PRECO\n"
                + ",EET2PVD1 as PRECO_ATACDO\n"
                + ",PRQTDATA as QUANTIDADE_ATACADO\n"
                + ",MAX(EEESTATU) as ESTOQUE\n"
                + "from\n"
                + " scea07 \n"
                + "left outer join\n"
                + " scea01 on(prrefere=eerefere)\n"
                + "group by\n"
                + " PRREFERE,PRCODBAR,PRDESCRI,EEPBRTB1,EET2PVD1,PRQTDATA"
                + " order by PRDESCRI";
        if (!this.conectaBanco.conexao()) {
            return;
        }
        if (!this.conectaBanco.executaSQL(sql)) {
            return;
        }
        try {
            ResultSet resultSet = this.conectaBanco.getRs();
            JRResultSetDataSource dataSource = new JRResultSetDataSource(resultSet);
            InputStream inputStream = getClass().getResourceAsStream("/relatorio/produtos.jasper");
            JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, new HashMap(), dataSource);
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setTitle("Relatorio de Produtos");
            jasperViewer.setDefaultCloseOperation(JasperViewer.DISPOSE_ON_CLOSE);
            jasperViewer.setResizable(false);
            jasperViewer.setVisible(true);
        } catch (JRException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("CADASTRO RAPIDO");
            alert.setContentText("Erro ao consultar produtos");
            alert.show();
        }
    }

    public void imprimirEstoque(FiltroEstoque filtroEstoque) {
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
                + "  MCCODEMP ='"+filtroEstoque.getEmpresa()+"' and MCDATMOV between '"+filtroEstoque.getDataInicial()+" 00:00:00' and '"+filtroEstoque.getDataFinal()+" 23:59:59' and MIREFERE like '%"+filtroEstoque.getProduto()+"%'";
        if (!this.conectaBanco.conexao()) {
            return;
        }
        if (!this.conectaBanco.executaSQL(sql)) {
            return;
        }
        try {
            ResultSet resultSet = this.conectaBanco.getRs();
            JRResultSetDataSource dataSource = new JRResultSetDataSource(resultSet);
            InputStream inputStream = getClass().getResourceAsStream("/relatorio/RelatorioEstoque.jasper");
            JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, new HashMap(), dataSource);
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setTitle("Relatorio de Estoque");
            jasperViewer.setDefaultCloseOperation(JasperViewer.DISPOSE_ON_CLOSE);
            jasperViewer.setResizable(false);
            jasperViewer.setVisible(true);
        } catch (JRException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("CADASTRO RAPIDO");
            alert.setContentText("Erro ao consultar produtos");
            alert.show();
        }
    }

}