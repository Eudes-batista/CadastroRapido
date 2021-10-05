package controle;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import relatorio.RelatorioClientes;
import servico.FormaPagamentoService;
import util.FXMLUtil;

public class FXMLRelatorioClienteFormaPagamentoController implements Initializable {

    @FXML
    private AnchorPane ancoraPrincipal;

    @FXML
    private Button btImprimir;

    @FXML
    private Button btSair;

    @FXML
    private DatePicker txtDataInicial;

    @FXML
    private DatePicker txtDataFinal;

    @FXML
    private ComboBox<String> comboFormaPagamento;

    private FormaPagamentoService formaPagamentoService;
    private RelatorioClientes relatorioClientes;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.formaPagamentoService = new FormaPagamentoService();
        this.relatorioClientes = new RelatorioClientes();
        this.txtDataInicial.setValue(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()));
        this.txtDataFinal.setValue(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()));
        this.btSair.setOnAction(evt -> this.sair());
        this.ancoraPrincipal.addEventFilter(KeyEvent.KEY_RELEASED, evt -> this.sair());
        this.btImprimir.setOnAction(evt -> this.imprimir());
        this.comboFormaPagamento.setItems(FXCollections.observableArrayList(formaPagamentoService.listarFormasPagamento()));
        this.comboFormaPagamento.getSelectionModel().selectFirst();
    }

    private void imprimir() {
        LocalDate dataInicialSelecionada = this.txtDataInicial.getValue();
        if (dataInicialSelecionada == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cadastro Rapido");
            alert.setContentText("Selecione uma data Inicial");
            alert.show();
            return;
        }
        LocalDate dataFinalSelecionada = this.txtDataFinal.getValue();
        if (dataFinalSelecionada == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cadastro Rapido");
            alert.setContentText("Selecione uma data Final");
            alert.show();
            return;
        }
        String dataInicialFormatada = dataInicialSelecionada.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String dataFinalFormatada = dataFinalSelecionada.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        this.relatorioClientes.setFormaPagamento(this.comboFormaPagamento.getSelectionModel().getSelectedItem());
        this.relatorioClientes.setDataInicial(dataInicialSelecionada.toString());
        this.relatorioClientes.setDataFinal(dataFinalSelecionada.toString());
        this.relatorioClientes.setPerido("Período: " + dataInicialFormatada + " até " + dataFinalFormatada);
        this.relatorioClientes.imprimir();
    }

    private void sair() {
        FXMLUtil.sair(this.ancoraPrincipal);
    }

}
