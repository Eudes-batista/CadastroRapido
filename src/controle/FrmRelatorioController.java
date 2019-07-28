package controle;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import modelo.dto.FiltroProduto;
import relatorio.RelatorioProduto;
import servico.MovimentoService;

public class FrmRelatorioController implements Initializable {

    @FXML
    private AnchorPane ancoraPrincipal;

    @FXML
    private Button btPesquisar;
    @FXML
    private Button btProdutos;
    @FXML
    private Button btMovimentacao;
    @FXML
    private Button btSair;

    @FXML
    private TextField editProduto;

    @FXML
    private ComboBox<String> comboEmpresa;

    @FXML
    private DatePicker dataInicial;
    @FXML
    private DatePicker dataFinal;

    private RelatorioProduto relatorioProduto;
    private FiltroProduto filtroProduto;
    private MovimentoService movimentoService;

    private final ObservableList<String> empresas = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.relatorioProduto = new RelatorioProduto();
        this.filtroProduto = new FiltroProduto();
        this.movimentoService = new MovimentoService();
        this.listarEmpresa();
    }

    private void imprimirRelatorioProduto() {
        this.filtroProduto.setEmpresa(this.comboEmpresa.getSelectionModel().getSelectedItem());
        this.filtroProduto.setProduto(this.editProduto.getText().trim().toUpperCase());
        this.filtroProduto.setDataInicial(this.dataInicial.getValue().toString());
        this.filtroProduto.setDataFinal(this.dataFinal.getValue().toString());
        this.relatorioProduto.imprimirTodosProdutos(filtroProduto);
    }

    private void listarEmpresa() {
        try {
            this.empresas.clear();
            List<String> listaEmpresa = this.movimentoService.listarEmpresas();
            this.empresas.addAll(listaEmpresa);
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cadastro Rapido");
            alert.setContentText("Erro ao listar empresa.");
            alert.show();
        }
    }

}
