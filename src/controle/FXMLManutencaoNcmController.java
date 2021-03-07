package controle;

import componentesjavafx.TextFieldCustom;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import modelo.Produto;
import servico.ManutencaoNcmService;
import servico.PesquisaService;

public class FXMLManutencaoNcmController implements Initializable {

    @FXML
    private Button btAtualizar;

    @FXML
    private Button btSair;

    @FXML
    private TableView<Produto> tableview;

    @FXML
    private TableColumn<Produto, String> columnReferencia;

    @FXML
    private TableColumn<Produto, String> columnCodigoBarra;

    @FXML
    private TableColumn<Produto, String> columnDescricao;

    @FXML
    private TableColumn<Produto, String> columnNcm;

    @FXML
    private TextFieldCustom editNcmExpirado;

    @FXML
    private TextFieldCustom editNcmNovo;

    @FXML
    private TextFieldCustom editPesquisaProduto;

    @FXML
    private Button btPesquisar;

    private ManutencaoNcmService manutencaoNcmService;
    private ObservableList<Produto> produtos = FXCollections.observableArrayList();
    private PesquisaService pesquisaService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.pesquisaService = new PesquisaService();
        this.manutencaoNcmService = new ManutencaoNcmService();
        this.inicializarColunas();
        this.editNcmExpirado.setOnAction(evt -> this.editNcmNovo.requestFocus());
        this.editNcmNovo.setOnAction(evt -> this.atualizarNcm());
        this.btAtualizar.setOnAction(evt -> this.atualizarNcm());
        this.btSair.setOnAction(evt -> this.sair());        
        this.btPesquisar.setOnAction(evt -> this.pesquisarProduto());
        this.editPesquisaProduto.setOnKeyPressed(evt -> {
            if (evt.getCode().equals(KeyCode.ENTER)) {
                this.pesquisarProduto();
            }
        });
    }

    private void inicializarColunas() {
        this.columnReferencia.setCellValueFactory(new PropertyValueFactory<>("referencia"));
        this.columnDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        this.columnCodigoBarra.setCellValueFactory(new PropertyValueFactory<>("codigoBarra"));
        this.columnNcm.setCellValueFactory(new PropertyValueFactory<>("ncm"));
    }

    private void atualizarNcm() {
        if (this.editNcmExpirado.getText().trim().isEmpty()) {
            this.mostrarMensagem(Alert.AlertType.WARNING, "Informe o ncm expirado.");
            return;
        }
        if (this.editNcmNovo.getText().trim().isEmpty()) {
            this.mostrarMensagem(Alert.AlertType.WARNING, "Informe o novo ncm.");
            return;
        }
        try {
            this.manutencaoNcmService.atualizarNcm(this.editNcmExpirado.getText(), this.editNcmNovo.getText());
            this.mostrarMensagem(Alert.AlertType.INFORMATION, "Ncm atualizado com sucesso!!");
            this.pesquisarProduto();
        } catch (SQLException ex) {
            this.mostrarMensagem(Alert.AlertType.ERROR, "Não foi possivel atualizar o ncm.");
        }
    }

    public void pesquisarProduto() {
        this.pesquisaService.setChekcCancelados(false);
        this.produtos.clear();
        this.produtos = this.pesquisaService.listarProdutos(this.editPesquisaProduto.getText().trim().toUpperCase());
        this.tableview.setItems(this.produtos);
    }

    private void mostrarMensagem(Alert.AlertType alertType, String mensagem) {
        Alert alert = new Alert(alertType);
        alert.setTitle("CADASTRO RAPIDO");
        alert.setContentText(mensagem);
        alert.show();
    }

    private void sair() {
        ((Stage) this.btSair.getScene().getWindow()).close();
    }

}
