package controle;

import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import modelo.Movimento;
import servico.MovimentoService;
import util.TableColumnUtil;

public class FrmPesquisaMovimentosController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TableView<Movimento> tabela;

    @FXML
    private TableColumn<Movimento, String> columnEmpresa;

    @FXML
    private TableColumn<Movimento, String> columnTipo;

    @FXML
    private TableColumn<Movimento, String> columnDocumento;

    @FXML
    private TableColumn<Movimento, Date> columnDataMovimentacao;

    @FXML
    private TableColumn<Movimento, String> columnObservacao;

    @FXML
    private DatePicker dataMovimentacao;

    @FXML
    private Button btSair;

    @FXML
    private Button btPesquisar;

    @FXML
    private TextField documento;

    private NumberFormat numberFormat;
    public final ObservableList<Movimento> movimentos = FXCollections.observableArrayList();
    private MovimentoService movimentoService;
    private boolean selecionouRegistro = false;

    public void pesquisarMovimentacao() {
        movimentos.clear();
        try {
            List<Movimento> movimentosPesquisados = this.movimentoService.listarMovimentos(dataMovimentacao.getValue().toString(), documento.getText());
            if (movimentosPesquisados != null) {
                this.movimentos.addAll(movimentosPesquisados);
            }
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("AVISO");
            alert.setContentText("Movimento não encontrado.");
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
        inicializarServicos();
        pesquisarMovimentacao();
        inicializarColunas();
        eventos();
        anchorPane.addEventHandler(KeyEvent.KEY_RELEASED, evt -> {
            if (evt.getCode() == KeyCode.ESCAPE) {
                selecionouRegistro = false;
                ((Stage) anchorPane.getScene().getWindow()).close();
            }
        });
    }

    private void eventos() {
        btPesquisar.setOnAction(e -> {
            pesquisarMovimentacao();
        });
        btSair.setOnAction(e -> {
            ((Stage) btSair.getScene().getWindow()).close();
        });
    }

    private void inicializarColunas() {
        this.columnEmpresa.setCellValueFactory(new PropertyValueFactory<>("empresa"));
        this.columnTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        this.columnDocumento.setCellValueFactory(new PropertyValueFactory<>("documento"));
        this.columnDataMovimentacao.setCellValueFactory(new PropertyValueFactory<>("dataMovimento"));
        this.columnObservacao.setCellValueFactory(new PropertyValueFactory<>("observacao"));
        this.tabela.setItems(movimentos);
        organizarConteudoColunas();
    }

    private void organizarConteudoColunas() {
        TableColumnUtil<Movimento> tableColumnUtil = new TableColumnUtil<>();
        tableColumnUtil.alinharConteudo(columnEmpresa, Pos.CENTER);
        tableColumnUtil.alinharConteudo(columnTipo, Pos.CENTER);
        tableColumnUtil.alinharConteudo(columnDocumento, Pos.CENTER_RIGHT);
        tableColumnUtil.alinharConteudo(columnDataMovimentacao, Pos.CENTER);
        tableColumnUtil.alinharConteudo(columnObservacao, Pos.CENTER);
    }

    private void inicializarServicos() {
        this.movimentoService = new MovimentoService();
        this.numberFormat = NumberFormat.getInstance();
        this.dataMovimentacao.setValue(LocalDate.now());
    }

    public boolean isSelecionouRegistro() {
        return selecionouRegistro;
    }

    public TableView<Movimento> getTabela() {
        return tabela;
    }

}
