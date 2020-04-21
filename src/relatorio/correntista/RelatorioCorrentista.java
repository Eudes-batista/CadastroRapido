package relatorio.correntista;

import controle.ConectaBanco;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import modelo.dto.CorrentistaFiltro;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

public class RelatorioCorrentista {

    private final ConectaBanco conectaBanco;
    private String cliente;
    private String saldoDisponivel;
    private String saldoDevedor;
    private String limiteEmCredito;

    public RelatorioCorrentista() {
        this.conectaBanco = new ConectaBanco();
    }

    public void imprimirCorrentista(CorrentistaFiltro correntistaFiltro) {
        String sql = "select * from crea15 where CRCLIENT='" + correntistaFiltro.getCliente() + "' and CRLANCAM between '" + correntistaFiltro.getDataInicial() + "' and '" + correntistaFiltro.getDataFinal() + "'";
        if (!this.conectaBanco.conexao()) {
            return;
        }
        if (!this.conectaBanco.executaSQL(sql)) {
            this.conectaBanco.desconecta();
            return;
        }
        try {
            ResultSet resultSet = this.conectaBanco.getResultSet();
            if (!resultSet.first()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("CADASTRO RAPIDO");
                alert.setContentText("Não existe movimentação");
                alert.show();
                return;
            }
            JRResultSetDataSource dataSource = new JRResultSetDataSource(resultSet);
            InputStream inputStream = getClass().getResourceAsStream("/relatorio/correntista.jasper");

            HashMap<String, Object> parametros = new HashMap<>();

            parametros.put("cliente", this.cliente);
            parametros.put("saldoDisponivel", this.saldoDisponivel);
            parametros.put("saldoDevedor", this.saldoDevedor);
            parametros.put("limiteCredito", this.limiteEmCredito);
            parametros.put("dataInicial", correntistaFiltro.getDataInicial());
            parametros.put("dataFinal", correntistaFiltro.getDataFinal());

            JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, parametros, dataSource);
            jasperPrint.setName("Relatorio de Correntista");
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setTitle("Relatorio de Correntista");
            jasperViewer.setDefaultCloseOperation(JasperViewer.DISPOSE_ON_CLOSE);
            jasperViewer.setResizable(false);
            jasperViewer.setVisible(true);
        } catch (JRException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("CADASTRO RAPIDO");
            alert.setContentText("Erro ao consultar correntista");
            alert.show();
        } catch (SQLException ex) {
            Logger.getLogger(RelatorioCorrentista.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public void setSaldoDisponivel(String saldoDisponivel) {
        this.saldoDisponivel = saldoDisponivel;
    }

    public void setSaldoDevedor(String saldoDevedor) {
        this.saldoDevedor = saldoDevedor;
    }

    public void setLimiteEmCredito(String limiteEmCredito) {
        this.limiteEmCredito = limiteEmCredito;
    }
}
