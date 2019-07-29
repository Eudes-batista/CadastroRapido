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
import modelo.Grupo;
import servico.GrupoService;

public class FrmGrupoController implements Initializable {

    @FXML
    private AnchorPane ancoraPesquisa;

    @FXML
    private Button btIncluir;

    @FXML
    private Button btSair;

    @FXML
    private TextField editPesquisa;

    @FXML
    private TableView<Grupo> tabela;

    @FXML
    private TableColumn<Grupo, String> columnCodigo;

    @FXML
    private TableColumn<Grupo, String> columnNome;

    @FXML
    private TableColumn<Grupo, String> columnExcluir;

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

    private final GrupoService grupoService = new GrupoService();
    private final ObservableList<Grupo> grupos = FXCollections.observableArrayList();
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
                this.selecionarGrupo();
            }
        });
    }

    private void selecionarGrupo() {
        this.ancoraPesquisa.setVisible(false);
        this.ancoraCadastro.setVisible(true);
        Grupo grupo = this.tabela.getSelectionModel().getSelectedItem();
        this.editCodigo.setText(grupo.getCodigo());
        this.editNome.setText(grupo.getNome());
        this.editCodigo.setDisable(true);
        this.editando = true;
    }

    private void inicializar() {
        this.columnCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        this.columnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        this.columnExcluir.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        this.tabela.setItems(this.grupos);
        this.alterarColuna();
    }

    private void alterarColuna() {
        this.columnExcluir.setCellFactory((TableColumn<Grupo, String> param) -> new TableCell<Grupo, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                if (!empty) {
                    Button button = new Button("Apagar");
                    button.getStyleClass().add("bt-apagar");
                    button.setOnAction(evt -> {      
                          Grupo grupo = FrmGrupoController.this.grupos.get(this.getIndex());
                          FrmGrupoController.this.grupoService.excluirMovimento(grupo.getCodigo());
                          FrmGrupoController.this.pesquisar();
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
            this.grupos.clear();
            List<Grupo> listarGrupos = this.grupoService.listarGrupos(this.editPesquisa.getText().trim().toUpperCase());
            this.grupos.addAll(listarGrupos);
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
        Grupo grupo = new Grupo();
        grupo.setCodigo(this.editCodigo.getText());
        grupo.setNome(this.editNome.getText());
        if (this.editando) {
            this.alterar(grupo);
            return;
        }
        if (this.grupoService.salvar(grupo)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cadastro Rapido");
            alert.setContentText("Grupo salvo com sucesso.");
            alert.show();
            this.pesquisar();
            this.limparCampos();
            this.editCodigo.requestFocus();
        }
    }

    private void alterar(Grupo grupo) {
        if (this.grupoService.alterar(grupo)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cadastro Rapido");
            alert.setContentText("Grupo salvo com sucesso.");
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
