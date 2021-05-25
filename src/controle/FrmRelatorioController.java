package controle;

import componentesjavafx.TextFieldCustom;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import modelo.Grupo;
import modelo.SubGrupo;
import modelo.TipoMovimento;
import modelo.Vendedor;
import modelo.dto.FiltroProduto;
import modelo.dto.FiltroProdutoVendido;
import relatorio.RelatorioProduto;
import servico.GrupoService;
import servico.MovimentoService;
import servico.SubGrupoService;
import servico.VendedorService;

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
    private ComboBox<String> comboTipoMovimentacao;
    
    @FXML
    private ComboBox<TipoMovimento> comboMovimentacao;

    @FXML
    private DatePicker dataInicial;
    @FXML
    private DatePicker dataFinal;

    @FXML
    private CheckBox checkTodos;

    @FXML
    private CheckBox checkSemTipoMovimentacao;

    @FXML
    private CheckBox checkProdutosVendidos;

    @FXML
    private ComboBox<Vendedor> comboVendedores;

    @FXML
    private TextFieldCustom editNumeroCaixa;

    @FXML
    private ComboBox<Grupo> comboGrupos;

    @FXML
    private ComboBox<SubGrupo> comboSubGrupo;

    private RelatorioProduto relatorioProduto;
    private FiltroProduto filtroProduto;
    private MovimentoService movimentoService;
    VendedorService vendedorService;
    private GrupoService grupoService;
    private SubGrupoService subGrupoService;

    private final ObservableList<String> empresas = FXCollections.observableArrayList();
    private ObservableList<TipoMovimento> movimentacoes;
    private ObservableList<String> tipoMovimentacoes = FXCollections.observableArrayList("Entradas","Saidas");
    private ObservableList<Vendedor> vendedores;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.relatorioProduto = new RelatorioProduto();
        this.filtroProduto = new FiltroProduto();
        this.movimentoService = new MovimentoService();
        this.vendedorService = new VendedorService();
        this.grupoService = new GrupoService();
        this.subGrupoService = new SubGrupoService();
        this.listarEmpresa();
        this.btProdutos.setOnAction(evt -> this.imprimirRelatorioProduto());
        this.btMovimentacao.setOnAction(evt -> this.imprimirRelatorioEstoque());
        this.btPesquisar.setOnAction(evt -> this.pesquisarProduto());
        this.btSair.setOnAction(evt -> this.sair());
        this.checkTodos.setOnAction(evt -> this.checkTodos());
        this.checkProdutosVendidos.setOnAction(evt -> this.listarVendedores());
        this.comboEmpresa.setItems(this.empresas);
        LocalDate data = LocalDate.now();
        this.dataInicial.setValue(data);
        this.dataFinal.setValue(data);
        this.vendedores = FXCollections.observableArrayList();
        this.listarTipoMovimentacoes();
        this.comboMovimentacao.setItems(this.movimentacoes);
        this.comboMovimentacao.disableProperty().bind(this.checkSemTipoMovimentacao.selectedProperty());
        this.comboVendedores.disableProperty().bind(this.checkProdutosVendidos.selectedProperty().not());
        this.editNumeroCaixa.disableProperty().bind(this.checkProdutosVendidos.selectedProperty().not());
        this.comboVendedores.setItems(this.vendedores);
        this.comboTipoMovimentacao.setItems(this.tipoMovimentacoes);
        this.listarGrupos();
        this.listarSubGrupos();
        this.comboGrupos.getSelectionModel().selectFirst();
        this.comboSubGrupo.getSelectionModel().selectFirst();
    }

    private void listarTipoMovimentacoes() {
        this.movimentacoes = FXCollections.observableArrayList();
        List<TipoMovimento> listaDeTipoMovimentacoes = this.movimentoService.listarTodosTipos();
        listaDeTipoMovimentacoes.add(0, new TipoMovimento("", "Selecione Tipo de movimentação"));
        this.movimentacoes.addAll(listaDeTipoMovimentacoes);
    }

    private void sair() {
        ((Stage) this.btSair.getScene().getWindow()).close();
    }

    private void checkTodos() {
        if (this.checkTodos.isSelected()) {
            this.dataInicial.setDisable(true);
            this.dataFinal.setDisable(true);
            this.editProduto.setDisable(true);
            this.btPesquisar.setDisable(true);
            this.dataInicial.setValue(null);
            this.dataFinal.setValue(null);
            return;
        }
        this.dataInicial.setDisable(false);
        this.dataFinal.setDisable(false);
        this.editProduto.setDisable(false);
        this.btPesquisar.setDisable(false);
        LocalDate data = LocalDate.now();
        this.dataInicial.setValue(data);
        this.dataFinal.setValue(data);
    }

    private void imprimirRelatorioProduto() {
        if (this.checkProdutosVendidos.isSelected()) {
            this.impirmirProdutosVendidos();
            return;
        }
        String empresa = this.comboEmpresa.getSelectionModel().getSelectedItem();
        this.filtroProduto.setEmpresa(empresa == null ? "" : empresa);
        this.filtroProduto.setProduto(this.editProduto.getText().trim().toUpperCase());
        this.filtroProduto.setDataInicial(this.dataInicial.getValue() == null ? null : this.dataInicial.getValue().toString());
        this.filtroProduto.setDataFinal(this.dataFinal.getValue() == null ? null : this.dataFinal.getValue().toString());
        this.relatorioProduto.imprimirTodosProdutos(this.filtroProduto);
    }

    private void imprimirRelatorioEstoque() {
        String empresa = this.comboEmpresa.getSelectionModel().getSelectedItem();
        this.filtroProduto.setEmpresa(empresa == null ? "" : empresa);
        this.filtroProduto.setProduto(this.editProduto.getText().trim().toUpperCase());
        this.filtroProduto.setDataInicial(this.dataInicial.getValue() == null ? null : this.dataInicial.getValue().toString());
        this.filtroProduto.setDataFinal(this.dataFinal.getValue() == null ? null : this.dataFinal.getValue().toString());
        this.filtroProduto.setGrupo(this.comboGrupos.getSelectionModel().getSelectedItem().getCodigo());
        this.filtroProduto.setSubGrupo(this.comboSubGrupo.getSelectionModel().getSelectedItem().getCodigo());
        TipoMovimento tipoMovimento = this.comboMovimentacao.getSelectionModel().getSelectedItem();
        if (tipoMovimento != null) {
            this.filtroProduto.setTipoDeMovimentacao(tipoMovimento.getCodigo());
        }
        if (this.checkSemTipoMovimentacao.isSelected()) {
            this.filtroProduto.setTipoDeMovimentacao("");
        }
        this.relatorioProduto.imprimirEstoque(this.filtroProduto);
    }

    private void impirmirProdutosVendidos() {
        String numeroDoCaixa = this.editNumeroCaixa.getText().trim();
        FiltroProdutoVendido filtroProdutoVendido = new FiltroProdutoVendido();
        filtroProdutoVendido.setCaixa(numeroDoCaixa);
        filtroProdutoVendido.setReferencia(this.editProduto.getText());
        Vendedor vendedor = this.comboVendedores.getSelectionModel().getSelectedItem();
        filtroProdutoVendido.setVendedor("");
        String vendedorInformado = "nenhum vendedor informado";
        if (vendedor != null) {
            filtroProdutoVendido.setVendedor(vendedor.getCodigo());
            vendedorInformado = vendedor.getNome();
        }
        LocalDate localDateDataInicial = this.dataInicial.getValue() == null ? LocalDate.now() : this.dataInicial.getValue();
        LocalDate localDateDataFinal = this.dataFinal.getValue() == null ? LocalDate.now() : this.dataFinal.getValue();
        String dataInicial = localDateDataInicial.toString();
        String dataFinal = localDateDataFinal.toString();
        filtroProdutoVendido.setDataInicial(dataInicial);
        filtroProdutoVendido.setDataFinal(dataFinal);
        filtroProdutoVendido.setPeriodo("Período: " + localDateDataInicial.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " - " + localDateDataFinal.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        filtroProdutoVendido.setInformacaoCaixa("Caixa: " + String.format("%02d", numeroDoCaixa.isEmpty() ? 0 : Integer.parseInt(numeroDoCaixa)) + " Vendedor: " + vendedorInformado);
        this.relatorioProduto.imprimirProdutosVendidos(filtroProdutoVendido);
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

    private void listarVendedores() {
        this.vendedores.clear();
        List<Vendedor> listaVendedores = this.vendedorService.listarVendedores();
        listaVendedores.add(new Vendedor("", "Selecione um Vendedor"));
        this.vendedores.addAll(listaVendedores);
    }

    private void listarGrupos() {
        try {
            List<Grupo> listaGrupos = this.grupoService.listarGrupos("");
            listaGrupos.add(0, new Grupo("", "Selecione um SubGrupo"));
            this.comboGrupos.setItems(FXCollections.observableArrayList(listaGrupos));
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cadastro Rapido");
            alert.setContentText("Erro ao listar grupos.");
            alert.show();
        }
    }

    private void listarSubGrupos() {
        try {
            List<SubGrupo> listaSubGrupos = this.subGrupoService.listarSubGrupos("");
            listaSubGrupos.add(0, new SubGrupo("", "Selecione um SubGrupo"));
            this.comboSubGrupo.setItems(FXCollections.observableArrayList(listaSubGrupos));
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cadastro Rapido");
            alert.setContentText("Erro ao listar subgrupos.");
            alert.show();
        }
    }

}
