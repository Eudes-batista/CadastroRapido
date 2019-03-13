package controle;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import servico.ProdutoServico;

public class FrmPesquisaController implements Initializable {

    private final ConectaBanco conectaBanco = new ConectaBanco();

    private String host, caminho;

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
    private Button button;

    @FXML
    private ImageView carregando;

    private final DecimalFormat df = new DecimalFormat("#,###0.00");

    public final ObservableList<Produto> produtos = FXCollections.observableArrayList();
    private boolean selecionouRegistro = false;

    @FXML
    private void pesquisar(KeyEvent event) {
        carregando.setVisible(true);
        if (editPesquisa.getText().isEmpty()) {
            carregando.setVisible(false);
        }
        if (event.getCode() == KeyCode.ENTER) {
            Platform.runLater(() -> {
                pesquisarProduto();
                carregando.setVisible(false);
            });
        }
    }

    public void pesquisarProduto() {
        produtos.clear();
        if (conectaBanco.conexao(getHost(), getCaminho())) {
            String sql = "select first 30 PRREFERE,PRCODBAR,PRDESCRI,EEPBRTB1,EET2PVD1,PRQTDATA from scea01 left outer join scea07 on(eerefere=prrefere) "
                    + "where PRDESCRI like '" + editPesquisa.getText().trim().toUpperCase() + "%' AND PRDATCAN is null "
                    + "group by PRREFERE,PRCODBAR,PRDESCRI,EEPBRTB1,EET2PVD1,PRQTDATA";
            if (conectaBanco.executaSQL(sql)) {
                try {
                    if (conectaBanco.getRs().first()) {
                        do {
                            produtos.add(new Produto(conectaBanco.getRs().getString("PRREFERE"),
                                    conectaBanco.getRs().getString("PRDESCRI"),
                                    conectaBanco.getRs().getDouble("EEPBRTB1"),
                                    conectaBanco.getRs().getDouble("EET2PVD1"),
                                    conectaBanco.getRs().getDouble("PRQTDATA"),
                                    conectaBanco.getRs().getString("PRCODBAR")));
                        } while (conectaBanco.getRs().next());
                        tabela.setItems(produtos);
                    }
                } catch (SQLException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERRO");
                    alert.setContentText("Erro ao consultar\n detalhe do erro " + ex.getMessage());
                    alert.show();
                }
            }
            this.conectaBanco.desconecta();
        }
    }

    private void processoConexao() {
        try {
            Path path = Paths.get("Preco.txt");
            if (Files.exists(path)) {
                List<String> dados = Files.lines(path).collect(Collectors.toList());
                setHost(dados.get(0));
                setCaminho(dados.get(1));
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("AVISO");
                alert.setContentText("Arquivo Preço.txt não existe");
                alert.show();
            }
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setContentText("Erro ao ler arquivo Preco.txt \n detalhe do erro: " + ex.getMessage());
            alert.show();
        }
    }

    @FXML
    private void selecionarRegistro(MouseEvent event) {
        if (event.getClickCount() == 2) {
            selecionouRegistro = true;
            ((Stage) tabela.getScene().getWindow()).close();
        }
    }

    @FXML
    private void selecionarComOEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            selecionouRegistro = true;
            ((Stage) tabela.getScene().getWindow()).close();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        processoConexao();
        inicializarColunas();
        carregando.setVisible(false);
        editPesquisa.setOnKeyPressed((e) -> {
            pesquisar(e);
            if (e.getCode() == KeyCode.DOWN) {
                tabela.requestFocus();
                carregando.setVisible(false);
            }
        });
        button.setOnAction((e) -> {
            selecionouRegistro = false;
            ((Stage) button.getScene().getWindow()).close();
        });
        this.editPesquisa.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if(!editPesquisa.getText().isEmpty())
                pesquisarProduto();
        });
        anchorPane.addEventHandler(KeyEvent.KEY_RELEASED, evt -> {
            if (evt.getCode() == KeyCode.ESCAPE) {
                selecionouRegistro = false;
                ((Stage) anchorPane.getScene().getWindow()).close();
            }
        });
    }

    private void inicializarColunas() {
        columnReferencia.setCellValueFactory(new PropertyValueFactory<>("referencia"));
        columnDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        columnCodigoBarra.setCellValueFactory(new PropertyValueFactory<>("codigoBarra"));
        columnPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
        columnPrecoAtacado.setCellValueFactory(new PropertyValueFactory<>("precoAtacado"));
        columnQtdAtacado.setCellValueFactory(new PropertyValueFactory<>("qtdAtacado"));
        columnApagar.setCellValueFactory(new PropertyValueFactory<>("button"));
        columnCheckBox.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
        tabela.setItems(produtos);
        columnPreco.setCellFactory((TableColumn<Produto, Double> param) -> {
            return new TableCell() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(null);
                    if (item != null) {
                        setText(df.format(item));
                        setAlignment(Pos.CENTER_RIGHT);
                    }
                }
            };
        });

        columnPrecoAtacado.setCellFactory((TableColumn<Produto, Double> param) -> {
            return new TableCell() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(null);
                    if (item != null) {
                        setText(df.format(item));
                        setAlignment(Pos.CENTER_RIGHT);
                    }
                }
            };
        });

        columnQtdAtacado.setCellFactory((TableColumn<Produto, Double> param) -> {
            return new TableCell() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(null);
                    if (item != null) {
                        setText(df.format(item));
                        setAlignment(Pos.CENTER_RIGHT);
                    }
                }
            };
        });

        columnApagar.setCellFactory((TableColumn<Produto, Button> param) -> {
            return new TableCell<Produto, Button>() {
                @Override
                protected void updateItem(Button item, boolean empty) {
                    setText(null);
                    setGraphic(null);
                    if (item != null) {
                        Button button = new Button("Apagar");
                        button.getStyleClass().add("bt-apagar");
                        button.setOnAction(e -> {
                            excluirProduto(produtos.get(getIndex()), false);
                        });
                        setAlignment(Pos.CENTER_RIGHT);
                        setGraphic(button);
                    }
                }
            };
        });
        columnCodigoBarra.setCellFactory((TableColumn<Produto, String> param) -> {
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
            count = 0;
            List<Produto> produtosSelecionados = this.produtos.stream().filter(p -> p.getCheckBox().isSelected()).collect(Collectors.toList());
            produtosSelecionados.forEach(p -> {
                if (this.excluirProduto(p, true)) {
                    count++;
                }
            });
            if (produtosSelecionados.size() == count) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setContentText("excluido todos com sucesso!!");
                alert.show();
            }
        }
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isSelecionouRegistro() {
        return selecionouRegistro;
    }

}
