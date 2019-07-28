package controle;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
        this.btProdutos.setOnAction(evt -> this.imprimirRelatorioProduto());
        this.btMovimentacao.setOnAction(evt -> this.imprimirRelatorioEstoque());
        this.btPesquisar.setOnAction(evt -> this.pesquisarProduto());
        this.btSair.setOnAction(evt -> this.sair());
        this.comboEmpresa.setItems(this.empresas);
    }
    
    private void sair() {
        ((Stage) this.btSair.getScene().getWindow()).close();
    }
    

    private void imprimirRelatorioProduto() {
        if(this.validarCampo()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cadastro Rapido");
            alert.setContentText("Selecione uma empresa.");
            alert.show();
            return;
        }
        this.filtroProduto.setEmpresa(this.comboEmpresa.getSelectionModel().getSelectedItem());
        this.filtroProduto.setProduto(this.editProduto.getText().trim().toUpperCase());
        this.filtroProduto.setDataInicial(this.dataInicial.getValue().toString());
        this.filtroProduto.setDataFinal(this.dataFinal.getValue().toString());
        this.relatorioProduto.imprimirTodosProdutos(this.filtroProduto);
    }
   
    private void imprimirRelatorioEstoque() {
         if(this.validarCampo()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cadastro Rapido");
            alert.setContentText("Selecione uma empresa.");
            alert.show();
            return;
        }
        this.filtroProduto.setEmpresa(this.comboEmpresa.getSelectionModel().getSelectedItem());
        this.filtroProduto.setProduto(this.editProduto.getText().trim().toUpperCase());
        this.filtroProduto.setDataInicial(this.dataInicial.getValue().toString());
        this.filtroProduto.setDataFinal(this.dataFinal.getValue().toString());
        this.relatorioProduto.imprimirTodosProdutos(this.filtroProduto);
    }
    
    private void pesquisarProduto() {
        try {
            FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("/visao/FrmPesquisa.fxml"));
            Parent parent = fXMLLoader.load();
            FrmPesquisaController controller = fXMLLoader.getController();
            controller.editPesquisa.setText(this.editProduto.getText().trim().toUpperCase());
            Scene scene = new Scene(parent);
            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            if (controller.tabela.getSelectionModel().getSelectedItem() != null) {
                if (controller.isSelecionouRegistro()) {
                    this.editProduto.setText(controller.tabela.getSelectionModel().getSelectedItem().getReferencia());
                }
            }
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cadastro Rapido");
            alert.setContentText("Erro ao carregar FrmPesquisa.");
            alert.show();
        }
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
    
    private boolean validarCampo() {
        return this.comboEmpresa.getSelectionModel().getSelectedItem() == null;
    }
    

}
