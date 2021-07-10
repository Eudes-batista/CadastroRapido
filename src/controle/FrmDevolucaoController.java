package controle;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import modelo.Vendedor;
import modelo.dto.FiltroDevolucao;
import relatorio.RelatorioDevolucao;
import servico.VendedorService;
import util.FXMLUtil;

public class FrmDevolucaoController implements Initializable {

    @FXML
    private AnchorPane ancoraPrincipal;

    @FXML
    private ComboBox<Vendedor> comboVendedor;

    @FXML
    private Button btImprimir;

    @FXML
    private Button btSair;

    @FXML
    private DatePicker dataInicial;

    @FXML
    private DatePicker dataFinal;

    private VendedorService vendedorService;

    private ObservableList<Vendedor> vendedores;
    private FiltroDevolucao filtroDevolucao;
    private RelatorioDevolucao relatorioDevolucao;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.vendedorService = new VendedorService();
        this.filtroDevolucao = new FiltroDevolucao();
        this.relatorioDevolucao = new RelatorioDevolucao();
        this.listarVendedores();
        LocalDate dataAtual = LocalDate.now();
        this.dataInicial.setValue(dataAtual);
        this.dataFinal.setValue(dataAtual);
        this.comboVendedor.getSelectionModel().selectFirst();
        this.btSair.setOnAction(evt -> this.sair());
        this.ancoraPrincipal.addEventFilter(KeyEvent.KEY_PRESSED, evt -> {
            if (evt.getCode().equals(KeyCode.ESCAPE)) {
                this.sair();
            }
        });
        this.btImprimir.setOnAction(evt -> this.imprimir());
        this.dataInicial.setOnAction(evt -> this.dataFinal.setValue(this.dataInicial.getValue()));
    }

    private void imprimir() {
        String dataInicialSelecionada = this.dataInicial.getValue() != null ? this.dataInicial.getValue().toString() : "";
        String dataFinalSelecionada = this.dataFinal.getValue() != null ? this.dataFinal.getValue().toString() : "";
        this.filtroDevolucao.setDataInicial(dataInicialSelecionada);
        this.filtroDevolucao.setDataFinal(dataFinalSelecionada);
        this.filtroDevolucao.setVendedora(this.comboVendedor.getSelectionModel().getSelectedItem().getCodigo());
        relatorioDevolucao.imprimir(filtroDevolucao);
    }

    private void sair() {
        FXMLUtil.sair(this.ancoraPrincipal);
    }

    private void listarVendedores() {
        this.vendedores = FXCollections.observableArrayList(this.vendedorService.listarVendedores());
        this.vendedores.add(0, new Vendedor("", "Selecione um vendedor"));
        this.comboVendedor.setItems(this.vendedores);
    }

}
