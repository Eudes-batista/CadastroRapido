package relatorio;

import controle.ConectaBanco;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.control.Alert;
import javax.swing.JFrame;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

public class RelatorioVendedor {

    private String perido;
    private String dataInicial;
    private String dataFinal;
    private String vendedor;
    private final ConectaBanco conectaBanco;

    public RelatorioVendedor() {
        this.conectaBanco = new ConectaBanco();
    }

    public String getPerido() {
        return perido;
    }

    public void setPerido(String perido) {
        this.perido = perido;
    }

    public String getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(String dataInicial) {
        this.dataInicial = dataInicial;
    }

    public String getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(String dataFinal) {
        this.dataFinal = dataFinal;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public void imprimir() {

        String sql = "select \n"
                + "  coalesce(sum(OSVLRORC),0) as total_vendas_brutas\n"
                + ", (select \n"
                + "  coalesce(sum(OSVLRORC) - sum(DIQUANTI*DIPRUNIT),0)\n"
                + "from \n"
                + "  sosa20 \n"
                + "inner join \n"
                + "  sosa21 on(DIDEVOLU=DVDEVOLU)\n"
                + "where\n"
                + "    DVDATVEN >= '" + this.dataInicial + " 00:00:00' \n"
                + "and DVDATVEN <= '" + this.dataFinal + " 23:59:59'\n"
                + "and DVVENDED = OSVENDED) as total_vendas_liquidas\n"
                + ", (select \n"
                + "   coalesce(sum(DIQUANTI*DIPRUNIT),0)\n"
                + "from \n"
                + "  sosa20 \n"
                + "inner join \n"
                + "  sosa21 on(DIDEVOLU=DVDEVOLU)\n"
                + "where\n"
                + "    DVDATVEN >= '" + this.dataInicial + " 00:00:00' \n"
                + "and DVDATVEN <= '" + this.dataFinal + " 23:59:59'\n"
                + "and DVVENDED = OSVENDED) as total_devolucoes,\n"
                + "OSVENDED as vendedores\n"
                + "from \n"
                + "  sosa01 \n"
                + "where \n"
                + "  OSLIQUID >= '" + this.dataInicial + " 00:00:00' and OSLIQUID <= '" + this.dataFinal + " 23:59:59' and OSVENDED like '%" + this.vendedor + "%'\n"
                + "group by OSVENDED";
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
            InputStream inputStream = getClass().getResourceAsStream("/relatorio/relatorioTotalVendedores.jasper");
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("periodoSelecionado", this.perido);
            JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, parametros, dataSource);
            jasperPrint.setName("Relatorio de Vendedores");
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setTitle("Relatorio de Vendedores");
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
        this.conectaBanco.desconecta();
    }

}
