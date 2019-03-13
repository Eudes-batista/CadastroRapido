package controle;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import static javafx.scene.input.KeyCode.ESCAPE;
import static javafx.scene.input.KeyCode.F1;
import static javafx.scene.input.KeyCode.F2;
import static javafx.scene.input.KeyCode.F3;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import modelo.ItemMovimento;
import modelo.Movimento;
import modelo.Produto;
import modelo.TipoMovimento;
import servico.ItemMovimentoService;
import servico.MovimentoService;
import servico.ProdutoServico;
import util.TableColumnUtil;

public class FrmMovimentoEstoqueController implements Initializable {

    @FXML
    private AnchorPane ancoraPrincipal;

    @FXML
    private TextField observacao;

    @FXML
    private Label labelDescricao;

    @FXML
    private TextField documento;

    @FXML
    private TextField item;

    @FXML
    private TextField quantidade;

    @FXML
    private TextField precoUnitario;

    @FXML
    private ComboBox<String> empresa;

    @FXML
    private ComboBox<TipoMovimento> tipo;

    @FXML
    private TableView<ItemMovimento> tabelaMovimento;

    @FXML
    private TableColumn<ItemMovimento, CheckBox> columnCheckBox;

    @FXML
    private TableColumn<ItemMovimento, String> columnProduto;

    @FXML
    private TableColumn<ItemMovimento, Double> columnQuantidade;

    @FXML
    private TableColumn<ItemMovimento, Double> columnPreco;

    @FXML
    private TableColumn<ItemMovimento, Double> columnTotal;

    @FXML
    private Button btAdicionar;

    @FXML
    private Button btFinalizar;

    @FXML
    private Button btApagarTodos;

    @FXML
    private Button btSair;

    @FXML
    private Button btCarregar;

    @FXML
    private Text quantidadeItens;

    @FXML
    private CheckBox checkTodos;

    private final ObservableList<String> empresas = FXCollections.observableArrayList();
    private final ObservableList<TipoMovimento> tipoMovimentos = FXCollections.observableArrayList();
    private final ObservableList<ItemMovimento> itemMovimentos = FXCollections.observableArrayList();

    private final MovimentoService movimentoService = new MovimentoService();
    private final ItemMovimentoService itemMovimentoService = new ItemMovimentoService();
    private final ProdutoServico produtoServico = new ProdutoServico();
    private Movimento movimento;
    private DecimalFormat numberFormat;

    private void listarEmpresa() {
        try {
            this.empresas.clear();
            this.empresas.addAll(this.movimentoService.listarEmpresas());
            this.empresa.setItems(empresas);
            this.empresa.getSelectionModel().select(0);
        } catch (SQLException ex) {
        }
    }

    private void listarTipoMovimento() {
        try {
            this.tipoMovimentos.clear();
            this.tipoMovimentos.addAll(this.movimentoService.listarTipos());
            this.tipo.setItems(tipoMovimentos);
            this.tipo.getSelectionModel().select(0);
        } catch (SQLException ex) {
        }
    }

    private void listarItemMovimento() {
        try {
            this.itemMovimentos.clear();
            List<ItemMovimento> itemMovimento = this.itemMovimentoService.listarMovimento(movimento);
            if (itemMovimento != null) {
                this.itemMovimentos.addAll(itemMovimento);
            }
            this.tabelaMovimento.setItems(itemMovimentos);
        } catch (SQLException ex) {
        }
    }

    private void buscarProduto() {
        Produto produto = this.produtoServico.buscarProduto(item.getText());
        if (produto != null) {
            this.item.setText(produto.getReferencia());
            this.labelDescricao.setText(produto.getDescricao());
            this.precoUnitario.setText(String.valueOf(produto.getPreco()));
            this.precoUnitario.setDisable(true);
            return;
        }
        try {
            FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("/visao/FrmPesquisa.fxml"));
            Parent parent = fXMLLoader.load();
            FrmPesquisaController controller = fXMLLoader.getController();
            controller.editPesquisa.setText(item.getText());
            Scene scene = new Scene(parent);
            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            if (controller.tabela.getSelectionModel().getSelectedItem() != null) {
                if (controller.isSelecionouRegistro()) {
                    this.item.setText(controller.tabela.getSelectionModel().getSelectedItem().getReferencia());
                    this.labelDescricao.setText(controller.tabela.getSelectionModel().getSelectedItem().getDescricao());
                    this.precoUnitario.setText(String.valueOf(controller.tabela.getSelectionModel().getSelectedItem().getPreco()));
                    this.precoUnitario.setDisable(true);
                }
            }
        } catch (IOException ex) {
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ancoraPrincipal.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent e) -> {
            switch (e.getCode()) {
                case F1:
                    finalizar();
                    break;
                case F2:
                    carregarMovimento();
                    break;
                case F3:
                    apagarSelecionados();
                    break;
                case ESCAPE:
                    ((Stage) btSair.getScene().getWindow()).close();
                    break;
                default:
                    break;
            }
        });
        listarEmpresa();
        listarTipoMovimento();
        inicializarColunas();
        this.numberFormat = new DecimalFormat("###,##0.00");
        this.columnPreco.setCellFactory((TableColumn<ItemMovimento, Double> param) -> new TableCell<ItemMovimento, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                setText("");
                if (!empty) {
                    setText(numberFormat.format(item));
                    setAlignment(Pos.CENTER_RIGHT);
                }
            }
        });
        this.columnTotal.setCellFactory((TableColumn<ItemMovimento, Double> param) -> new TableCell<ItemMovimento, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                setText("");
                if (!empty) {
                    setText(numberFormat.format(item));
                    setAlignment(Pos.CENTER_RIGHT);
                }
            }
        });
        new TableColumnUtil<ItemMovimento>().alinharConteudo(columnQuantidade, Pos.CENTER_RIGHT);
        this.movimento = new Movimento();
        eventos();
    }

    private void carregarMovimento() {
        if (this.documento.isDisabled()) {
            return;
        }
        try {
            movimento.setDocumento(documento.getText());
            FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("/visao/FrmPesquisaMovimentos.fxml"));
            Parent load = fXMLLoader.load();
            FrmPesquisaMovimentosController pesquisaMovimentosController = fXMLLoader.getController();
            Scene scene = new Scene(load);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.showAndWait();
            if (pesquisaMovimentosController.getTabela().getSelectionModel().getSelectedItem() != null) {
                if (pesquisaMovimentosController.isSelecionouRegistro()) {
                    Movimento movimentoSelecionado = pesquisaMovimentosController.getTabela().getSelectionModel().getSelectedItem();
                    if (movimento.getDocumento().isEmpty()) {
                        empresa.getSelectionModel().select(movimentoSelecionado.getEmpresa());
                        tipo.getSelectionModel().select(new TipoMovimento(movimentoSelecionado.getTipo()));
                        documento.setText(movimentoSelecionado.getDocumento());
                        buscarMovimento();
                        return;
                    }
                    movimento = movimentoSelecionado;
                    listarItemMovimento();
                    this.salvarItemNoMovimentoAtual();
                }
            }
        } catch (IOException ex) {
        }
    }

    private void salvarItemNoMovimentoAtual() {
        movimento.setEmpresa(empresa.getSelectionModel().getSelectedItem());
        movimento.setTipo(tipo.getSelectionModel().getSelectedItem().getCodigo());
        movimento.setDocumento(documento.getText());
        movimento.setObservacao(observacao.getText());
        movimento.setDataMovimento(new Date());
        movimento.setDataAtualizacao(new Date());
        movimento.setUsuario("EDS");
        if (movimentoService.salvar(movimento)) {
            ObservableList<ItemMovimento> itens = FXCollections.observableArrayList(this.itemMovimentos);
            this.itemMovimentos.forEach(item -> {
                item.setMovimento(this.movimento);
                item.setCheckBox(new CheckBox());
                if (this.itemMovimentoService.salvar(item)) {
                    itens.remove(item);
                    itens.add(item);
                }
            });
            this.itemMovimentos.clear();
            this.itemMovimentos.addAll(itens);
            this.tabelaMovimento.setItems(itemMovimentos);
            desabilitarCampos(true);
            tabelaMovimento.scrollTo(this.itemMovimentos.size());
            limparCamposItem();
            quantidadeItens.setText(String.valueOf(itemMovimentos.size()));
        }
    }

    private void eventos() {
        this.observacao.setOnAction(e -> item.requestFocus());
        this.btAdicionar.setOnAction(e -> salvar());
        this.btFinalizar.setOnAction(e -> finalizar());
        this.btApagarTodos.setOnAction(e -> this.apagarSelecionados());
        this.checkTodos.setOnAction(e -> marcaTodos());
        this.documento.setOnAction(e -> buscarMovimento());
        this.btCarregar.setOnAction(e -> carregarMovimento());
        this.item.setOnAction(e -> {
            this.buscarProduto();
            quantidade.setText("1");
            quantidade.requestFocus();
            quantidade.selectAll();
        });
        this.quantidade.setOnAction(e -> salvar());
        this.btSair.setOnAction(e -> ((Stage) btSair.getScene().getWindow()).close());
        this.quantidade.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                quantidade.setText(newValue.replaceAll("^[aA-zZ]", ""));
            }
        });
        this.item.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.DOWN)) {
                if (!itemMovimentos.isEmpty()) {
                    tabelaMovimento.requestFocus();
                }
            }
        });
    }

    private void inicializarColunas() {
        this.columnCheckBox.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
        this.columnProduto.setCellValueFactory(new PropertyValueFactory<>("produto"));
        this.columnQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        this.columnPreco.setCellValueFactory(new PropertyValueFactory<>("precoUnitario"));
        this.columnTotal.setCellValueFactory(new PropertyValueFactory<>("precoTotal"));
        this.tipo.setConverter(new StringConverter<TipoMovimento>() {
            @Override
            public String toString(TipoMovimento object) {
                return object.getCodigo() + " - " + object.getDescricao();
            }

            @Override
            public TipoMovimento fromString(String string) {
                return new TipoMovimento(string.split("-")[0].trim());
            }
        });
    }

    private boolean salvou = false;

    private void salvar() {
        if (!salvou) {
            movimento.setEmpresa(empresa.getSelectionModel().getSelectedItem());
            movimento.setTipo(tipo.getSelectionModel().getSelectedItem().getCodigo());
            movimento.setDocumento(documento.getText());
            movimento.setObservacao(observacao.getText());
            movimento.setDataMovimento(new Date());
            movimento.setDataAtualizacao(new Date());
            movimento.setUsuario("EDS");
            salvou = movimentoService.salvar(movimento);
        }
        if (validacaoCampos()) {
            return;
        }
        ItemMovimento itemMovimento = new ItemMovimento(movimento);
        itemMovimento.setProduto(item.getText() + " - " + labelDescricao.getText());
        itemMovimento.setQuantidade(Double.parseDouble(quantidade.getText().replace(".", "").replaceAll(",", ".")));
        String preco = precoUnitario.getText().isEmpty() ? "0" : precoUnitario.getText().replace(".", "").replaceAll(",", ".");
        itemMovimento.setPrecoUnitario(Double.parseDouble(preco));
        if (this.itemMovimentoService.salvar(itemMovimento)) {
            itemMovimento.setCheckBox(new CheckBox());
            this.itemMovimentos.add(itemMovimento);
            this.tabelaMovimento.setItems(itemMovimentos);
        }
        desabilitarCampos(true);
        tabelaMovimento.scrollTo(this.itemMovimentos.size());
        limparCamposItem();
        quantidadeItens.setText(String.valueOf(itemMovimentos.size()));
    }

    private void limparCamposItem() {
        this.item.setText("");
        this.quantidade.setText("");
        this.precoUnitario.setText("");
        this.labelDescricao.setText("Item");
        this.item.requestFocus();
    }

    private void finalizar() {
        limparCamposItem();
        desabilitarCampos(false);
        this.documento.setText("");
        this.observacao.setText("");
        this.checkTodos.setSelected(false);
        this.itemMovimentos.clear();
        this.tabelaMovimento.setItems(itemMovimentos);
        this.documento.requestFocus();
        this.movimento = null;
        this.movimento = new Movimento();
        this.salvou = false;
        this.quantidadeItens.setText("0000");
    }

    private void desabilitarCampos(boolean desabilitar) {
        empresa.setDisable(desabilitar);
        tipo.setDisable(desabilitar);
        documento.setDisable(desabilitar);
        observacao.setDisable(desabilitar);
    }

    private void apagarSelecionados() {
        List<ItemMovimento> itensSelecionados = this.itemMovimentos.stream().filter(item -> item.getCheckBox().isSelected()).collect(Collectors.toList());
        if (itensSelecionados.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("AVISO");
            alert.setContentText("Nenhum item selecionado.");
            alert.showAndWait();
            return;
        }
        if (this.itemMovimentos.size() == itensSelecionados.size()) {
            if (itemMovimentoService.excluirTodosItens(itensSelecionados)) {
                this.itemMovimentos.clear();
            }
            this.movimentoService.excluirMovimento(itensSelecionados.get(0).getMovimento());
            finalizar();
            quantidadeItens.setText(String.valueOf(itemMovimentos.size()));
            return;
        }
        itensSelecionados.forEach(item -> {
            if (itemMovimentoService.excluirItem(item)) {
                this.itemMovimentos.remove(item);
            }
        });
        item.requestFocus();
        quantidadeItens.setText(String.valueOf(itemMovimentos.size()));
    }

    private void marcaTodos() {
        if (checkTodos.isSelected()) {
            this.itemMovimentos.forEach(item -> item.getCheckBox().setSelected(true));
            return;
        }
        this.itemMovimentos.forEach(item -> item.getCheckBox().setSelected(false));
    }

    private void buscarMovimento() {
        Movimento m = new Movimento(empresa.getSelectionModel().getSelectedItem(),
                tipo.getSelectionModel().getSelectedItem().getCodigo(),
                documento.getText(),
                observacao.getText(),
                "EDS", new Date(), new Date());
        try {
            movimento = this.movimentoService.verificarMovimento(m);
            if (movimento != null) {
                listarItemMovimento();
                desabilitarCampos(true);
                item.requestFocus();
                quantidadeItens.setText(String.valueOf(itemMovimentos.size()));
                return;
            }
            movimento = new Movimento();
            this.observacao.requestFocus();
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("AVISO");
            alert.setContentText("Erro ao pesquisar movimento");
            alert.showAndWait();
        }
    }

    private boolean validacaoCampos() {
        if (movimento == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("AVISO");
            alert.setContentText("Movimento não foi enserido, verifique as informções no cabeçalho.");
            alert.showAndWait();
            documento.requestFocus();
            return true;
        }
        if (documento.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("AVISO");
            alert.setContentText("Documento é Obrigatoria!");
            alert.showAndWait();
            documento.requestFocus();
            return true;
        }
        if (item.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("AVISO");
            alert.setContentText("Você não pesquisou nenhum produto.");
            alert.showAndWait();
            item.requestFocus();
            return true;
        }
        if (quantidade.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("AVISO");
            alert.setContentText("Qauntidade é Obrigatoria!");
            alert.showAndWait();
            quantidade.requestFocus();
            return true;
        }

        return false;
    }

}
