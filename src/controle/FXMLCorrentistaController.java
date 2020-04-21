package controle;

import controle.componentes.CorrentistaComponente;
import exception.CorrentistaException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import modelo.Cliente;
import modelo.Correntista;
import modelo.TipoMovimentacao;
import modelo.dto.ClientesCorrentistaDTO;
import servico.ClienteService;
import servico.CorrentistaService;
import util.FormatterUtil;

public class FXMLCorrentistaController extends CorrentistaComponente implements Initializable {

    private ObservableList<Cliente> clientes;
    private ClienteService clienteService;
    private CorrentistaService correntistaService;
    private Cliente cliente;
    private TipoMovimentacao tipoMovimentacao;
    private Correntista correntista;
    private ClientesCorrentistaDTO consultarSaldosCorrentida;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.clientes = FXCollections.observableArrayList();
        this.clienteService = new ClienteService();
        this.correntistaService = new CorrentistaService();
        this.ancoraPrincipal.addEventFilter(KeyEvent.KEY_RELEASED, evt -> {
            if (evt.getCode().equals(KeyCode.ESCAPE)) {
                this.sair();
            }
        });
        this.inicializarColunas();
        this.tabela.setItems(this.clientes);
        this.adiconarEvento();
    }

    public void adiconarEvento() {
        this.textPesquisa.setOnAction(evt -> this.realizarPesquisa());
        this.btSair.setOnMouseClicked(evt -> this.sair());
        this.btSairContaCorrente.setOnMouseClicked(evt -> this.sair());
        this.textDataInicial.valueProperty().addListener((ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) -> {
            this.consultarSaldoCorrentista(newValue, this.textDataFinal.getValue());
        });
        this.textDataFinal.valueProperty().addListener((ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) -> {
            this.consultarSaldoCorrentista(this.textDataInicial.getValue(), newValue);
        });
        this.btCredito.setOnAction(evt -> this.mostrarLancamento(TipoMovimentacao.CREDITO));
        this.btDebito.setOnAction(evt -> this.mostrarLancamento(TipoMovimentacao.DEBITO));
        this.btSalvar.setOnAction(evt -> this.salvar());
        this.btVoltar.setOnAction(evt -> this.sair());
        this.textDescricao.setOnAction(evt -> this.textValor.requestFocus());
        this.textValor.setOnAction(evt -> {
            this.salvar();
            this.ancoraLancamento.setVisible(false);
        });
        this.tabela.setOnMouseClicked(evt -> {
            if (evt.getClickCount() == 2) {
                this.pegarClienteSelecionadoTabela();
                this.selecionarCliente();
            }
        });
        this.tabela.setOnKeyPressed(evt -> {
            if (evt.getCode().equals(KeyCode.ENTER)) {
                this.pegarClienteSelecionadoTabela();
                this.selecionarCliente();
            }
        });
        this.btSelecionarCliente.setOnAction(evt -> {
            this.pegarClienteSelecionadoTabela();
            this.selecionarCliente();
        });
    }

    private void pegarClienteSelecionadoTabela() {
        this.cliente = this.tabela.getSelectionModel().getSelectedItem();
    }

    private void selecionarCliente() {
        if (this.cliente == null) {
            return;
        }
        LocalDate localDateInicial = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate localDateFinal = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        this.ancoraListarClientes.setVisible(false);
        this.habilitarCampos();
        this.consultarSaldoCorrentista(localDateInicial, localDateFinal);
    }
    
    private void consultarSaldoCorrentista(LocalDate localDateInicial, LocalDate localDateFinal) {        
        String dataInicial = localDateInicial.toString();
        String dataFinal = localDateFinal.toString();
        this.consultarSaldosCorrentida = this.clienteService.consultarSaldosCorrentida(this.cliente.getCodigo(), dataInicial, dataFinal);
        this.preencherInformacoes(localDateInicial, localDateFinal, this.consultarSaldosCorrentida);
    }    

    private void preencherInformacoes(LocalDate localDateInicial, LocalDate localDateFinal, ClientesCorrentistaDTO consultarSaldosCorrentida) {
        this.textDataInicial.setValue(localDateInicial);
        this.textDataFinal.setValue(localDateFinal);
        this.labelSaldoDisponivel.setText("R$ " + FormatterUtil.getValorFormatado(consultarSaldosCorrentida.getSaldoDisponivel()));
        this.labelSaldoDevedor.setText("R$ " + FormatterUtil.getValorFormatado(consultarSaldosCorrentida.getSaldoDevedor()));
        this.labelSaldoLimiteEmCredito.setText("R$ " + FormatterUtil.getValorFormatado(consultarSaldosCorrentida.getSaldoCredito()));
        this.labelTotalCredito.setText("R$ " + FormatterUtil.getValorFormatado(consultarSaldosCorrentida.getTotalCredito()));
        this.labelTotalDebito.setText("R$ " + FormatterUtil.getValorFormatado(consultarSaldosCorrentida.getTotalDebito()));
        this.labelContaCorrente.setText("Conta - "+this.cliente.getNome().toUpperCase().trim());
    }

    private void realizarPesquisa() {
        String pesquisa = this.textPesquisa.getText().trim().toUpperCase();
        this.cliente = this.clienteService.buscarCliente(pesquisa);
        if (this.cliente != null) {
            this.habilitarCampos();
            this.selecionarCliente();
            return;
        }
        this.listarClientes(pesquisa);
        this.mostrarPesquisa();
    }

    public void listarClientes(String pesquisa) {
        List<Cliente> listaDeCliente = this.clienteService.listarClientes(pesquisa);
        this.clientes.clear();
        this.clientes.addAll(listaDeCliente);
    }

    public void mostrarPesquisa() {
        this.ancoraListarClientes.setVisible(true);
        this.ancoraLancamento.setVisible(false);
    }

    private void sair() {
        if (this.ancoraListarClientes.isVisible()) {
            this.ancoraListarClientes.setVisible(false);
            return;
        }
        if (this.ancoraLancamento.isVisible()) {
            this.ancoraLancamento.setVisible(false);
            return;
        }
        ((Stage) this.ancoraPrincipal.getScene().getWindow()).close();
    }

    private void inicializarColunas() {
        this.columnCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        this.columnCliente.setCellValueFactory(new PropertyValueFactory<>("nome"));
        this.columnLimiteCredito.setCellValueFactory(new PropertyValueFactory<>("limiteCredito"));
    }

    private void mostrarLancamento(TipoMovimentacao tipoMovimentacao) {
        this.tipoMovimentacao = tipoMovimentacao;
        this.labelTipoLancamento.setText("Tipo de Lancamento: " + tipoMovimentacao.getValor());
        this.ancoraLancamento.setVisible(true);
        this.textDescricao.requestFocus();
        this.textDescricao.setText(this.tipoMovimentacao.getValor());
        this.textDescricao.selectAll();
        this.textValor.setText("");
    }

    private void salvar() {
        this.correntista = null;
        this.correntista = this.preencherCorrentista();
        try {
            this.correntistaService.salvarMovimentacaoCorrentistaRetaguarda(this.correntista);
            this.mostrarMensagem("Salvo com sucesso", Alert.AlertType.INFORMATION);
            this.ancoraLancamento.setVisible(false);
            this.realizarPesquisa();
        } catch (CorrentistaException ex) {
            this.mostrarMensagem(ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void habilitarCampos() {
        this.btCredito.setDisable(false);
        this.btDebito.setDisable(false);
        this.btImprimir.setDisable(false);
        this.textDataInicial.setDisable(false);
        this.textDataFinal.setDisable(false);
    }

    private Correntista preencherCorrentista() {
        Correntista novoCorrentista = new Correntista();
        novoCorrentista.setCodigoCliente(this.cliente.getCodigo());
        novoCorrentista.setDescricao(this.textDescricao.getText());
        novoCorrentista.setUsuario("ADM");
        novoCorrentista.setTipoMovimento("M");
        novoCorrentista.setDataLancamento(new Date());
        novoCorrentista.setDataDeProcesso(new Date());
        double valor = FormatterUtil.getValorPago(this.textValor.getText());
        novoCorrentista.setDebito(0.0);
        novoCorrentista.setCredito(valor);
        if (this.tipoMovimentacao.equals(TipoMovimentacao.DEBITO)) {
            novoCorrentista.setCredito(0.0);
            novoCorrentista.setDebito(valor);
        }
        String dataInicial = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).toString();
        String dataFinal = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).toString();
        this.consultarSaldosCorrentida = this.clienteService.consultarSaldosCorrentida(this.cliente.getCodigo(), dataInicial, dataFinal);
        double saldoDisponivel = this.consultarSaldosCorrentida.getSaldoDisponivel() - valor;
        novoCorrentista.setSaldoAnterior(BigDecimal.valueOf(saldoDisponivel).setScale(2, RoundingMode.HALF_UP).doubleValue());
        return novoCorrentista;
    }

    public void mostrarMensagem(String mensagem, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Cadastro Rapido");
        alert.setContentText(mensagem);
        alert.show();
    }

}
