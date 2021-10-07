/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 *
 * @author eudes
 */
public class RelatorioClientes {

    private String perido;
    private String dataInicial;
    private String dataFinal;
    private String formaPagamento;
    private final ConectaBanco conectaBanco;

    public RelatorioClientes() {
        this.conectaBanco = new ConectaBanco();
    }

    public void setPerido(String perido) {
        this.perido = perido;
    }

    public void setDataInicial(String dataInicial) {
        this.dataInicial = dataInicial;
    }

    public void setDataFinal(String dataFinal) {
        this.dataFinal = dataFinal;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public void imprimir() {
        String sql = "select \n"
                + "   iif(OSCLIENT is null or OSCLIENT = '','00000',OSCLIENT) as codigo_cliente,\n"
                + "   OSORCAOS NUMERO_VENDA,\n"
                + "   iif(OSNOMCLI is null or OSCLIENT = '' , 'Não identificado',OSNOMCLI) as cliente, \n"
                + "   osvlrorc as total,\n"
                + "   OSLIQUID as data_ultima_venda,\n"
                + "   iif(CLTELEFO is null,'',CLTELEFO) as CLTELEFO \n"
                + "from \n"
                + "  sosa01 \n"
                + "inner join \n"
                + "  sosa09 on(CDORCAOS=OSORCAOS)\n"
                + "left outer join\n"
                + "  crea01 on(CLCODIGO=OSCLIENT)\n"
                + "where\n"
                + "  CDTIPCAR='" + this.formaPagamento + "' "
                + "AND  OSLIQUID >= '" + this.dataInicial + " 00:00:00' "
                + "AND  OSLIQUID <= '" + this.dataFinal + " 23:59:59' ";
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
            InputStream inputStream = getClass().getResourceAsStream("/relatorio/relatorio_clientes.jasper");
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("periodo", this.perido);
            parametros.put("formaPagamento", "Forma de Pagamento: "+this.formaPagamento.toUpperCase());
            JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, parametros, dataSource);
            jasperPrint.setName("Relatorio de clientes");
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setTitle("Relatorio de clientes");
            jasperViewer.setDefaultCloseOperation(JasperViewer.DISPOSE_ON_CLOSE);
            jasperViewer.setResizable(true);
            jasperViewer.setExtendedState(JFrame.MAXIMIZED_BOTH);
            jasperViewer.setVisible(true);;
        } catch (JRException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("CADASTRO RAPIDO");
            alert.setContentText("Erro ao consultar clientes");
            alert.show();
        }
        this.conectaBanco.desconecta();
    }

}
