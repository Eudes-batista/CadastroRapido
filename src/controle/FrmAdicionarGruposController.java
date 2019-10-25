package controle;

import componentesjavafx.TextFieldCustom;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import modelo.Grupo;
import modelo.Produto;
import servico.ProdutoServico;

public class FrmAdicionarGruposController implements Initializable {

    @FXML
    private AnchorPane ancoraPrincipal;

    @FXML
    private ComboBox<Grupo> comboBoxGrupo;

    @FXML
    private TableView<Produto> tabelaProdutos;

    @FXML
    private TableColumn<Produto, CheckBox> columnCheckBox;

    @FXML
    private CheckBox checkBoxTodos;

    @FXML
    private TableColumn<Produto, String> columnReferencia;

    @FXML
    private TableColumn<Produto, String> columnDescricao;

    @FXML
    private TableColumn<Produto, String> columnCodigoBarra;

    @FXML
    private TextFieldCustom txtPesquisa;

    @FXML
    private Button btSalvar;

    @FXML
    private Button btSair;

    private final ObservableList<Grupo> grupos = FXCollections.observableArrayList();
    private final ObservableList<Produto> produtos = FXCollections.observableArrayList();

    private ProdutoServico produtoServico;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.produtoServico = new ProdutoServico();
        this.btSair.setOnAction(evt -> this.sair());
        this.checkBoxTodos.setOnAction(evt -> this.marcaTodos());
        this.btSalvar.setOnAction(evt -> this.salvar());
        this.listarGrupos();
        this.comboBoxGrupo.setItems(this.grupos);
        this.comboBoxGrupo.setConverter(new StringConverter<Grupo>() {
            @Override
            public String toString(Grupo object) {
                return object.getCodigo() + " - " + object.getNome();
            }

            @Override
            public Grupo fromString(String string) {
                String codigo = string.split("-")[0].trim();
                Grupo grupo = new Grupo(codigo);
                int index = grupos.indexOf(grupo);
                return grupos.get(index);
            }
        });
        this.txtPesquisa.setOnKeyReleased(evt -> {
            if (evt.getCode().equals(KeyCode.ENTER)) {
                this.pesquisarProdutos();
            }
        });
        this.iniciarColunas();
        this.tabelaProdutos.setItems(this.produtos);
    }

    private void iniciarColunas() {
        this.columnReferencia.setCellValueFactory(new PropertyValueFactory<>("referencia"));
        this.columnDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        this.columnCodigoBarra.setCellValueFactory(new PropertyValueFactory<>("codigoBarra"));
        this.columnCodigoBarra.setCellValueFactory(new PropertyValueFactory<>("codigoBarra"));
        this.columnCheckBox.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
    }

    private void pesquisarProdutos() {
        this.produtos.clear();
        List<Produto> listaDeProdutos = this.produtoServico.pesquisarProduto(this.txtPesquisa.getText().toUpperCase());
        this.produtos.addAll(listaDeProdutos);
    }

    private void marcaTodos() {
        if (this.checkBoxTodos.isSelected()) {
            this.produtos.forEach(produto -> produto.getCheckBox().setSelected(true));
            return;
        }
        this.produtos.forEach(produto -> produto.getCheckBox().setSelected(false));
    }

    private void salvar() {
        if (this.comboBoxGrupo.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cadastro Rapido");
            alert.setContentText("Selecione um grupo.");
            alert.show();
            return;
        }
        List<Produto> produtos = this.produtos.stream().filter(produto -> produto.getCheckBox().isSelected()).collect(Collectors.toList());
        try {
            String referencias =  produtos.stream().map(produto -> "'"+produto.getReferencia()+"'").collect(Collectors.joining(","));
            this.produtoServico.atualizarGrupo(referencias, this.comboBoxGrupo.getSelectionModel().getSelectedItem().getCodigo());
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cadastro Rapido");
            alert.setContentText("Produtos Atualizados com sucesso!!");
            alert.show();
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cadastro Rapido");
            alert.setContentText("Erro ao atualizar grupo no produto.");
            alert.show();
        }
    }

    private void listarGrupos() {
        this.grupos.clear();
        try {
            List<Grupo> listaDeGrupos = produtoServico.listarGrupos();
            this.grupos.addAll(listaDeGrupos);
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cadastro Rapido");
            alert.setContentText("Erro ao pesquisar grupos.");
            alert.show();
        }
    }

    private void sair() {
        ((Stage) this.btSair.getScene().getWindow()).close();
    }

}
