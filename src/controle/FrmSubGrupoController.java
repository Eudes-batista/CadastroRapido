package controle;

import componentesjavafx.TextFieldCustom;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import modelo.SubGrupo;
import servico.SubGrupoService;

public class FrmSubGrupoController implements Initializable {

    @FXML
    private AnchorPane ancoraPesquisa;

    @FXML
    private Button btIncluir;

    @FXML
    private Button btSair;

    @FXML
    private TextField editPesquisa;

    @FXML
    private TableView<SubGrupo> tabela;

    @FXML
    private TableColumn<SubGrupo, String> columnCodigo;

    @FXML
    private TableColumn<SubGrupo, String> columnNome;

    @FXML
    private TableColumn<SubGrupo, String> columnExcluir;

    @FXML
    private AnchorPane ancoraCadastro;

    @FXML
    private TextFieldCustom editCodigo;

    @FXML
    private TextFieldCustom editNome;

    @FXML
    private Button btSalvar;

    @FXML
    private Button btVoltar;

    private final SubGrupoService subGrupoService = new SubGrupoService();
    private final ObservableList<SubGrupo> subGrupos = FXCollections.observableArrayList();
    private boolean editando;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.inicializar();
        this.pesquisar();
        this.btSalvar.setOnAction(evt -> salvar());
        this.btSair.setOnAction(evt -> sair());
        this.editCodigo.setOnAction(evt -> this.editNome.requestFocus());
        this.editNome.setOnAction(evt -> this.salvar());
        this.btIncluir.setOnAction(evt -> {
            this.ancoraPesquisa.setVisible(false);
            this.ancoraCadastro.setVisible(true);
        });
        this.btVoltar.setOnAction(evt -> {
            this.ancoraCadastro.setVisible(false);
            this.ancoraPesquisa.setVisible(true);
            this.limparCampos();
        });
        this.editPesquisa.setOnKeyReleased(evt -> {
            if (evt.getCode().equals(KeyCode.ENTER)) {
                this.pesquisar();
            }
        });
        this.tabela.setOnMouseClicked(evt -> {
            if (evt.getClickCount() == 2) {
                this.selecionarSubGrupo();
            }
        });
    }

    private void selecionarSubGrupo() {
        this.ancoraPesquisa.setVisible(false);
        this.ancoraCadastro.setVisible(true);
        SubGrupo grupo = this.tabela.getSelectionModel().getSelectedItem();
        this.editCodigo.setText(grupo.getCodigo());
        this.editNome.setText(grupo.getNome());
        this.editCodigo.setDisable(true);
        this.editando = true;
    }

    private void inicializar() {
        this.columnCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        this.columnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        this.columnExcluir.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        this.tabela.setItems(this.subGrupos);
        this.alterarColuna();
    }

    private void alterarColuna() {
        this.columnExcluir.setCellFactory((TableColumn<SubGrupo, String> param) -> new TableCell<SubGrupo, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                if (!empty) {
                    Button button = new Button("Apagar");
                    button.getStyleClass().add("btpadrao");
                    button.setStyle("-fx-font-size:11pt; -fx-text-fill: #fff;");
                    button.setOnAction(evt -> {
                        SubGrupo grupo = FrmSubGrupoController.this.subGrupos.get(this.getIndex());
                        FrmSubGrupoController.this.subGrupoService.excluirMovimento(grupo.getCodigo());
                        FrmSubGrupoController.this.pesquisar();
                    });
                    setGraphic(button);
                } else {
                    setGraphic(null);
                }
            }
        });
    }

    private void pesquisar() {
        try {
            this.subGrupos.clear();
            List<SubGrupo> listarSubGrupos = this.subGrupoService.listarSubGrupos(this.editPesquisa.getText().trim().toUpperCase());
            this.subGrupos.addAll(listarSubGrupos);
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cadastro Rapido");
            alert.setContentText("Erro ao pesquisar.");
            alert.show();
        }
    }

    private void salvar() {
        if (validarCampos()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cadastro Rapido");
            alert.setContentText("Preencha os campos corretamente.");
            alert.show();
            return;
        }
        SubGrupo grupo = new SubGrupo();
        grupo.setCodigo(this.editCodigo.getText());
        grupo.setNome(this.editNome.getText());
        if (this.editando) {
            this.alterar(grupo);
            return;
        }
        if (this.subGrupoService.salvar(grupo)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cadastro Rapido");
            alert.setContentText("SubGrupo salvo com sucesso.");
            alert.show();
            this.pesquisar();
            this.limparCampos();
            this.editCodigo.requestFocus();
        }
    }

    private void alterar(SubGrupo grupo) {
        if (this.subGrupoService.alterar(grupo)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cadastro Rapido");
            alert.setContentText("SubGrupo salvo com sucesso.");
            alert.show();
            this.pesquisar();
            this.limparCampos();
            this.editCodigo.requestFocus();
        }
    }

    private void limparCampos() {
        this.editCodigo.setText("");
        this.editNome.setText("");
        this.editCodigo.setDisable(false);
        this.editando = false;
    }

    private boolean validarCampos() {
        return this.editCodigo.getText().trim().isEmpty() || this.editNome.getText().trim().isEmpty();
    }

    private void sair() {
        ((Stage) this.btSair.getScene().getWindow()).close();
    }

}
