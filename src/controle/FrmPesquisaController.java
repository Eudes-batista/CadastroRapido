package controle;

import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import modelo.Produto;
import servico.PesquisaService;
import servico.ProdutoServico;

public class FrmPesquisaController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    public TableView<Produto> tabela;

    @FXML
    private TableColumn<Produto, String> columnReferencia;
    @FXML
    private TableColumn<Produto, String> columnDescricao;
    @FXML
    private TableColumn<Produto, Double> columnPreco;
    @FXML
    private TableColumn<Produto, Double> columnPrecoAtacado;
    @FXML
    private TableColumn<Produto, Double> columnQtdAtacado;
    @FXML
    private TableColumn<Produto, String> columnCodigoBarra;
    @FXML
    private TableColumn<Produto, Button> columnApagar;
    @FXML
    private TableColumn<Produto, CheckBox> columnCheckBox;

    @FXML
    protected TextField editPesquisa;
    
    @FXML
    private CheckBox chekcCancelados;

    @FXML
    private Button button;

    @FXML
    private ImageView carregando;

    private final DecimalFormat df = new DecimalFormat("#,###0.00");
    private boolean selecionouRegistro = false;
    private ObservableList<Produto> produtos = FXCollections.observableArrayList();
    private PesquisaService pesquisaService;

    @FXML
    private void pesquisar(KeyEvent event) {
        this.carregando.setVisible(true);
        if (this.editPesquisa.getText().isEmpty()) {
            this.carregando.setVisible(false);
        }
        if (event.getCode() == KeyCode.ENTER) {
            Platform.runLater(() -> {
                pesquisarProduto();
                this.carregando.setVisible(false);
            });
        }
    }

    public void pesquisarProduto() {
       this.pesquisaService.setChekcCancelados(this.chekcCancelados.isSelected());
       this.produtos  = this.pesquisaService.listarProdutos(this.editPesquisa.getText());
       this.tabela.setItems(this.produtos);
    }

    @FXML
    private void selecionarRegistro(MouseEvent event) {
        if (event.getClickCount() == 2) {
            this.selecionouRegistro = true;
            ((Stage) this.tabela.getScene().getWindow()).close();
        }
    }

    @FXML
    private void selecionarComOEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            this.selecionouRegistro = true;
            ((Stage) this.tabela.getScene().getWindow()).close();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.pesquisaService=new PesquisaService();
        inicializarColunas();
        this.carregando.setVisible(false);
        this.editPesquisa.setOnKeyPressed((e) -> {
            pesquisar(e);
            if (e.getCode() == KeyCode.DOWN) {
                this.tabela.requestFocus();
                this.carregando.setVisible(false);
            }
        });
        this.button.setOnAction((e) -> {
            this.selecionouRegistro = false;
            ((Stage) this.button.getScene().getWindow()).close();
        });
        this.editPesquisa.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if(!this.editPesquisa.getText().isEmpty())
                pesquisarProduto();
        });
        this.anchorPane.addEventHandler(KeyEvent.KEY_RELEASED, evt -> {
            if (evt.getCode() == KeyCode.ESCAPE) {
                this.selecionouRegistro = false;
                ((Stage) this.anchorPane.getScene().getWindow()).close();
            }
        });
        this.tabela.setItems(this.produtos);
    }

    private void inicializarColunas() {
        this.columnReferencia.setCellValueFactory(new PropertyValueFactory<>("referencia"));
        this.columnDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        this.columnCodigoBarra.setCellValueFactory(new PropertyValueFactory<>("codigoBarra"));
        this.columnPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
        this.columnPrecoAtacado.setCellValueFactory(new PropertyValueFactory<>("precoAtacado"));
        this.columnQtdAtacado.setCellValueFactory(new PropertyValueFactory<>("qtdAtacado"));
        this.columnApagar.setCellValueFactory(new PropertyValueFactory<>("button"));
        this.columnCheckBox.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
        this.columnPreco.setCellFactory((TableColumn<Produto, Double> param) -> {
            return new TableCell() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(null);
                    if (item != null) {
                        setText(FrmPesquisaController.this.df.format(item));
                        setAlignment(Pos.CENTER_RIGHT);
                    }
                }
            };
        });

        this.columnPrecoAtacado.setCellFactory((TableColumn<Produto, Double> param) -> {
            return new TableCell() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(null);
                    if (item != null) {
                        setText(FrmPesquisaController.this.df.format(item));
                        setAlignment(Pos.CENTER_RIGHT);
                    }
                }
            };
        });

        this.columnQtdAtacado.setCellFactory((TableColumn<Produto, Double> param) -> {
            return new TableCell() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(null);
                    if (item != null) {
                        setText(FrmPesquisaController.this.df.format(item));
                        setAlignment(Pos.CENTER_RIGHT);
                    }
                }
            };
        });

        this.columnApagar.setCellFactory((TableColumn<Produto, Button> param) -> {
            return new TableCell<Produto, Button>() {
                @Override
                protected void updateItem(Button item, boolean empty) {
                    setText(null);
                    setGraphic(null);
                    if (item != null) {
                        Button button = new Button("Apagar");
                        button.getStyleClass().add("bt-apagar");
                        button.setOnAction(e -> {
                            excluirProduto(FrmPesquisaController.this.produtos.get(getIndex()), false);
                        });
                        setAlignment(Pos.CENTER_RIGHT);
                        setGraphic(button);
                    }
                }
            };
        });
        this.columnCodigoBarra.setCellFactory((TableColumn<Produto, String> param) -> {
            return new TableCell() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(null);
                    if (item != null) {
                        setText(item.toString());
                        setAlignment(Pos.CENTER);
                    }
                }
            };
        });
    }

    private boolean excluirProduto(Produto produto, boolean todos) {
        if (todos) {
            if (excluirProdutoUnico(produto)) {
                this.produtos.remove(produto);
                return true;
            }
            return false;
        }
        if (opcoes()) {
            if (excluirProdutoUnico(produto)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setContentText("excluido com sucesso!!");
                alert.show();
                pesquisarProduto();
                return true;
            }
        }
        return false;
    }

    private boolean excluirProdutoUnico(Produto produto) {
        ProdutoServico produtoServico = new ProdutoServico();
        try {
            produtoServico.excluirProduto(produto.getReferencia());
            return true;
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            try {
                produtoServico.conecta.getConn().rollback();
            } catch (SQLException ex1) {
                alert.setTitle("Erro");
                alert.setContentText("Erro ao Excluir produto! já existe movimentação");
                alert.show();
            }
            alert.setTitle("Erro");
            alert.setContentText("Erro ao Excluir produto! já existe movimentação");
            alert.show();
        }
        return false;
    }

    private boolean opcoes() {
        Alert alert = new Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setContentText("Deseja memso excluir o produto?");
        Optional optional = alert.showAndWait();
        return ButtonType.OK == optional.get();
    }

    
    private int count = 0;
    @FXML
    private void apagarProdutosSelecionados() {
        if (opcoes()) {
            this.count = 0;
            List<Produto> produtosSelecionados = this.produtos.stream().filter(p -> p.getCheckBox().isSelected()).collect(Collectors.toList());
            produtosSelecionados.forEach(p -> {
                if (this.excluirProduto(p, true)) {
                    this.count++;
                }
            });
            if (produtosSelecionados.size() == this.count) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setContentText("excluido todos com sucesso!!");
                alert.show();
            }
        }
    }
    public boolean isSelecionouRegistro() {
        return this.selecionouRegistro;
    }

}
