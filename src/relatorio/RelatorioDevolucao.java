package relatorio;

import controle.ConectaBanco;
import java.io.InputStream;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import javafx.scene.control.Alert;
import javax.swing.JFrame;
import modelo.dto.FiltroDevolucao;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

public class RelatorioDevolucao {

    private final ConectaBanco conectaBanco;

    public RelatorioDevolucao() {
        this.conectaBanco = new ConectaBanco();
    }

    public void imprimir(FiltroDevolucao filtroDevolucao) {
        String sql = "select \n"
                + "    dvdevolu as codigo,\n"
                + "    MAX(dvvended) as vendedor,\n"
                + "    MAX(dvdatdev) as data_devolucao,\n"
                + "    MAX(dvdatven) as data_venda,\n"
                + "    MAX(dvusuari) as usuario,\n"
                + "    SUM(DIQUANTI*DIPRUNIT) as total \n"
                + "from \n"
                + "    sosa20\n"
                + "inner join \n"
                + "    sosa21 on(didevolu=dvdevolu)\n"
                + "where\n";
        if (!filtroDevolucao.getDataInicial().isEmpty() && !filtroDevolucao.getDataFinal().isEmpty()) {
            sql += "  dvdatdev between '" + filtroDevolucao.getDataInicial() + " 00:00:00' and '" + filtroDevolucao.getDataFinal() + " 23:59:59' and\n";
        }
        sql += "  dvvended like '%" + filtroDevolucao.getVendedora() + "%' \n"
                + "group by\n"
                + "  dvdevolu";
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
            InputStream inputStream = getClass().getResourceAsStream("/relatorio/relatorioDevolucao.jasper");
            HashMap<String, Object> hashMap = new HashMap<>();
            String dataInicial, dataFinal;
            if (filtroDevolucao.getDataInicial().isEmpty() && filtroDevolucao.getDataFinal().isEmpty()) {
                String dataAtual = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                dataInicial = dataAtual;
                dataFinal = dataAtual;
            } else {
                dataInicial = LocalDate.parse(filtroDevolucao.getDataInicial()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                dataFinal = LocalDate.parse(filtroDevolucao.getDataFinal()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }
            hashMap.put("periodo", "Período: " + dataInicial + " até " + dataFinal);
            JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, hashMap, dataSource);
            jasperPrint.setName("Relatório de Devolução");
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setTitle("Relatório de Devolução");
            jasperViewer.setDefaultCloseOperation(JasperViewer.DISPOSE_ON_CLOSE);
            jasperViewer.setResizable(true);
            jasperViewer.setExtendedState(JFrame.MAXIMIZED_BOTH);
            jasperViewer.setVisible(true);
        } catch (JRException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("CADASTRO RAPIDO");
            alert.setContentText("Erro ao consultar devolução");
            alert.show();
        }
    }

}
