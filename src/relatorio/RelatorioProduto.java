package relatorio;

import controle.ConectaBanco;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.HashMap;
import javafx.scene.control.Alert;
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

    public void imprimir() {
        String sql = "select \n"
                + " PRREFERE as REFERENCIA\n"
                + ",PRCODBAR as BARRAS\n"
                + ",PRDESCRI as DESCRICAO\n"
                + ",EEPBRTB1 as PRECO\n"
                + ",EET2PVD1 as PRECO_ATACDO\n"
                + ",PRQTDATA as QUANTIDADE_ATACADO\n"
                + ",EEESTATU as ESTOQUE\n"
                + "from\n"
                + " scea07 \n"
                + "left outer join\n"
                + " scea01 on(prrefere=eerefere)\n"
                + "group by\n"
                + " PRREFERE,PRCODBAR,PRDESCRI,EEPBRTB1,EET2PVD1,PRQTDATA,EEESTATU";
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
            JasperViewer jasperViewer = new JasperViewer(jasperPrint,false);
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

}
