package controle;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import modelo.Vendedor;
import relatorio.RelatorioVendedor;
import servico.VendedorService;

public class FXMLRelatorioVendedorController implements Initializable {

    @FXML
    private AnchorPane ancoraPrincipal;

    @FXML
    private DatePicker txtDataInicial;

    @FXML
    private ComboBox<Vendedor> comboVendedores;

    @FXML
    private DatePicker txtDataFinal;

    @FXML
    private Button btTotalVendas;

    @FXML
    private Button btSair;

    @FXML
    private CheckBox checkTodosVendedores;

    private RelatorioVendedor relatorioVendedor;

    private ObservableList<Vendedor> vendedores;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.relatorioVendedor = new RelatorioVendedor();
        this.btSair.setOnAction(evt -> this.sair());
        this.vendedores = FXCollections.observableArrayList();
        this.comboVendedores.setItems(this.vendedores);
        this.listarVendedores();
        this.comboVendedores.disableProperty().bind(this.checkTodosVendedores.selectedProperty());
        this.btTotalVendas.setOnAction(evt -> this.realizarImpressao());
        this.txtDataInicial.setValue(LocalDate.now().withDayOfMonth(1));
        this.txtDataFinal.setValue(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()));
        this.comboVendedores.getSelectionModel().selectFirst();
    }

    private void listarVendedores() {
        VendedorService vendedorService = new VendedorService();
        List<Vendedor> listaVendedores = vendedorService.listarVendedores();
        this.vendedores.addAll(listaVendedores);
    }

    private void realizarImpressao() {
        if (this.txtDataInicial.getValue() == null || this.txtDataFinal.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("CADASTRO RAPIDO");
            alert.setContentText("Selecione um período.");
            alert.show();
            return;
        }
        String vendedor = this.comboVendedores.getSelectionModel().getSelectedItem().getCodigo();
        if (this.checkTodosVendedores.isSelected()) {
            vendedor = "";
        }
        relatorioVendedor.setVendedor(vendedor);
        relatorioVendedor.setDataInicial(this.txtDataInicial.getValue().toString());
        relatorioVendedor.setDataFinal(this.txtDataFinal.getValue().toString());
        String dataIniciaFormatada = this.txtDataInicial.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String dataFinalFormatada = this.txtDataFinal.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String periodo = "Período: " + dataIniciaFormatada + " - " + dataFinalFormatada;
        relatorioVendedor.setPerido(periodo);
        relatorioVendedor.imprimir();
    }

    private void sair() {
        ((Stage) this.ancoraPrincipal.getScene().getWindow()).close();
    }

}
