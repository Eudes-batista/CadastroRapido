package controle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.FXMLUtil;

public class FXMLMenuController implements Initializable {

    @FXML
    private AnchorPane ancoraPrincipal;

    @FXML
    private Button btSair;

    @FXML
    private Button btCadastroProduto;

    @FXML
    private Button btEstoque;

    @FXML
    private Button btCorrentista;

    @FXML
    private Button btRelatorios;

    @FXML
    private Button btVendedores;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.btCadastroProduto.setOnAction(evt -> this.abrirTelaDeCadastro());
        this.btEstoque.setOnAction(evt -> this.abrirTelaDeEstoque());
        this.btCorrentista.setOnAction(evt -> this.abrirTelaDeCorrentista());
        this.btRelatorios.setOnAction(evt -> this.abrirTelaDeRelatorio());
        this.btVendedores.setOnAction(evt -> this.abrirTelaDeRelatorioVendedores());
        this.btSair.setOnAction(evt -> this.sair());
    }

    private void sair() {
        FXMLUtil.sair(this.ancoraPrincipal);
    }

    private void abrirTelaDeCadastro() {
        this.abrirFXML("FrmAlteracaoPreco");
    }

    private void abrirTelaDeEstoque() {
        this.abrirFXML("FrmMovimentoEstoque");
    }

    private void abrirTelaDeCorrentista() {
        this.abrirFXML("FXMLCorrentista");
    }

    private void abrirTelaDeRelatorio() {
        this.abrirFXML("FrmRelatorio");
    }

    private void abrirTelaDeRelatorioVendedores() {
        this.abrirFXML("FXMLRelatorioVendedor");
    }

    private void abrirFXML(String fxml) {
        try {
            Stage stage = FXMLUtil.abrirFXML(fxml);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cadastro Rapido");
            alert.setContentText(ex.getMessage());
            alert.show();
        }
    }
}
