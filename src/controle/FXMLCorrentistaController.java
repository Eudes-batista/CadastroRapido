package controle;

import controle.componentes.CorrentistaComponente;
import exception.CorrentistaException;
import exception.UsuarioException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import modelo.Cliente;
import modelo.Correntista;
import modelo.TipoMovimentacao;
import modelo.Usuario;
import modelo.dto.ClientesCorrentistaDTO;
import modelo.dto.CorrentistaFiltro;
import relatorio.correntista.RelatorioCorrentista;
import servico.ClienteService;
import servico.CorrentistaService;
import servico.UsuarioService;
import util.FormatterUtil;

public class FXMLCorrentistaController extends CorrentistaComponente implements Initializable {

    private ObservableList<Cliente> clientes;
    private ClienteService clienteService;
    private CorrentistaService correntistaService;
    private Cliente cliente;
    private TipoMovimentacao tipoMovimentacao;
    private Correntista correntista;
    private ClientesCorrentistaDTO consultarSaldosCorrentida;
    private RelatorioCorrentista relatorioCorrentista;
    private CorrentistaFiltro correntistaFiltro;
    private boolean pesquisaDoCliente;
    private SimpleDateFormat dataFormat;
    private ObservableList<Correntista> correntistas;
    private UsuarioService usuarioService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.clientes = FXCollections.observableArrayList();
        this.correntistas = FXCollections.observableArrayList();
        this.dataFormat = new SimpleDateFormat("dd/MM/yyyy");
        this.relatorioCorrentista = new RelatorioCorrentista();
        this.correntistaFiltro = new CorrentistaFiltro();
        this.clienteService = new ClienteService();
        this.correntistaService = new CorrentistaService();
        this.usuarioService = new UsuarioService();
        this.inicializarColunas();
        this.adicionarTeclasDeAtalhos();
        this.adiconarEvento();
        this.adicionarEventoInputs();
        this.adicionarEventoTabela();
        this.tabela.setItems(this.clientes);
        this.tabelaMovimentacoes.setItems(this.correntistas);
    }

    private void adicionarTeclasDeAtalhos() {
        this.ancoraPrincipal.addEventFilter(KeyEvent.KEY_RELEASED, evt -> {
            if (evt.getCode().equals(KeyCode.ESCAPE)) {
                this.sair();
            }
        });
    }

    private void adiconarEvento() {
        this.btSair.setOnMouseClicked(evt -> this.sair());
        this.btSairContaCorrente.setOnMouseClicked(evt -> this.sair());
        this.btImprimir.setOnMouseClicked(evt -> this.imprimirRelatorio());
        this.btExcluirMovimentacoes.setOnAction(evt -> this.mostrarPermissaoDeUsuario());
        this.btConfirmarUsuario.setOnAction(evt -> this.validarUsuario());
        this.btCredito.setOnAction(evt -> this.mostrarLancamento(TipoMovimentacao.CREDITO));
        this.btDebito.setOnAction(evt -> this.mostrarLancamento(TipoMovimentacao.DEBITO));
        this.btSalvar.setOnAction(evt -> this.salvar());
        this.btVoltar.setOnAction(evt -> this.sair());
        this.btMovimentacoes.setOnAction(evt -> this.mostrarMovimentacoes());
        this.btVoltarMovimentacoes.setOnAction(evt -> this.ancoraMovimentacao.setVisible(false));
        this.btSelecionarCliente.setOnAction(evt -> {
            this.pegarClienteSelecionadoTabela();
            this.selecionarCliente();
        });
        this.btPesquisa.setOnAction(evt -> {
            try {
                this.listarMovimentacoes();
                this.mostrarSaldoDaMovimentacao();
            } catch (CorrentistaException ex) {
                this.mostrarMensagem(ex.getMessage(), Alert.AlertType.ERROR);
            }
        });
        this.btVoltarPermissao.setOnAction(evt -> this.sair());
    }

    private void adicionarEventoTabela() {
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
    }

    private void adicionarEventoInputs() {
        this.textDescricao.setOnAction(evt -> this.textValor.requestFocus());
        this.textPesquisa.setOnAction(evt -> {
            this.pesquisaDoCliente = true;
            this.realizarPesquisa();
        });
        this.textDataInicial.valueProperty().addListener((ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) -> {
            if (this.textDataFinal.getValue() != null) {
                this.consultarSaldoCorrentista(newValue, this.textDataFinal.getValue());
            }
        });
        this.textDataFinal.valueProperty().addListener((ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) -> {
            if (this.textDataInicial.getValue() != null) {
                this.consultarSaldoCorrentista(this.textDataInicial.getValue(), newValue);
            }
        });
        this.textValor.setOnAction(evt -> {
            this.salvar();
            this.ancoraLancamento.setVisible(false);
        });
        this.textUsuario.setOnAction(evt -> this.textSenha.requestFocus());
        this.textSenha.setOnAction(evt -> this.validarUsuario());
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
        if (this.pesquisaDoCliente) {
            this.consultarSaldosCorrentida = this.clienteService.consultarSaldosCorrentida(this.cliente.getCodigo(), "", "");
            this.pesquisaDoCliente = false;
        } else {
            this.consultarSaldosCorrentida = this.clienteService.consultarSaldosCorrentida(this.cliente.getCodigo(), dataInicial, dataFinal);
        }
        this.preencherInformacoes(this.consultarSaldosCorrentida);
    }

    private void preencherInformacoes(ClientesCorrentistaDTO consultarSaldosCorrentida) {
        this.labelSaldoDisponivel.setText("R$ " + FormatterUtil.getValorFormatado(consultarSaldosCorrentida.getSaldoDisponivel()));
        this.labelSaldoDevedor.setText("R$ " + FormatterUtil.getValorFormatado(consultarSaldosCorrentida.getSaldoDevedor()));
        this.labelSaldoLimiteEmCredito.setText("R$ " + FormatterUtil.getValorFormatado(consultarSaldosCorrentida.getSaldoCredito()));
        this.labelTotalCredito.setText("R$ " + FormatterUtil.getValorFormatado(consultarSaldosCorrentida.getTotalCredito()));
        this.labelTotalDebito.setText("R$ " + FormatterUtil.getValorFormatado(consultarSaldosCorrentida.getTotalDebito()));
        this.labelValorAReceber.setText("R$ " + FormatterUtil.getValorFormatado(consultarSaldosCorrentida.getValorReceber()));
        this.labelContaCorrente.setText("Conta - " + this.cliente.getNome().toUpperCase().trim());
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
        if (this.ancoraMovimentacao.isVisible()) {
            this.ancoraMovimentacao.setVisible(false);
            return;
        }
        if (this.ancoraPermissao.isVisible()) {
            this.ancoraPermissao.setVisible(false);
            return;
        }
        ((Stage) this.ancoraPrincipal.getScene().getWindow()).close();
    }

    private void inicializarColunas() {
        this.columnCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        this.columnCliente.setCellValueFactory(new PropertyValueFactory<>("nome"));
        this.columnLimiteCredito.setCellValueFactory(new PropertyValueFactory<>("limiteCredito"));
        this.columnDataLancamento.setCellValueFactory(cell -> new SimpleStringProperty(this.dataFormat.format(cell.getValue().getDataLancamento())));
        this.columnDataDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        this.columnCredito.setCellValueFactory(cell -> new SimpleStringProperty(FormatterUtil.getValorFormatado(cell.getValue().getCredito())));
        this.columnDebito.setCellValueFactory(cell -> new SimpleStringProperty(FormatterUtil.getValorFormatado(cell.getValue().getDebito())));
        this.columnUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        this.columnExcluir.setCellValueFactory(new PropertyValueFactory<>("codigoCliente"));

        this.columnExcluir.setCellFactory((TableColumn<Correntista, String> param) -> new TableCell<Correntista, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                if (!empty) {
                    Hyperlink hyperlink = new Hyperlink("Excluir");
                    hyperlink.setTextFill(Color.BLACK);
                    hyperlink.setOnAction(evt -> {
                        Correntista correntista = correntistas.get(this.getIndex());
                        removerCorrentista(correntista);
                    });
                    setGraphic(hyperlink);
                    setAlignment(Pos.CENTER);
                } else {
                    setGraphic(null);
                }
            }
        });
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
        if (!this.validarCamposAntesDeSalvar()) {
            return;
        }
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
        this.btMovimentacoes.setDisable(false);
        this.btExcluirMovimentacoes.setDisable(false);
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
        String dataInicial = this.textDataInicial.getValue().toString();
        String dataFinal = this.textDataFinal.getValue().toString();
        this.consultarSaldosCorrentida = this.clienteService.consultarSaldosCorrentida(this.cliente.getCodigo(), dataInicial, dataFinal);
        double saldoDisponivelNoMomento = this.consultarSaldosCorrentida.getSaldoCredito() - this.consultarSaldosCorrentida.getTotalDebito();
        double saldoDisponivel = saldoDisponivelNoMomento - valor;
        novoCorrentista.setSaldoAnterior(BigDecimal.valueOf(saldoDisponivel).setScale(2, RoundingMode.HALF_UP).doubleValue());
        return novoCorrentista;
    }

    private void mostrarMensagem(String mensagem, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Cadastro Rapido");
        alert.setContentText(mensagem);
        alert.show();
    }

    private boolean validarCamposAntesDeSalvar() {
        if (this.cliente == null) {
            this.mostrarMensagem("Selecione algum cliente", Alert.AlertType.WARNING);
            return false;
        }
        if (this.textDescricao.getText().trim().isEmpty()) {
            this.mostrarMensagem("Preencher a descrição", Alert.AlertType.WARNING);
            this.textDescricao.requestFocus();
            this.textDescricao.setText(this.tipoMovimentacao.getValor());
            this.textDescricao.selectAll();
            return false;
        }
        if (this.textValor.getText().trim().isEmpty()) {
            this.textValor.requestFocus();
            return false;
        }
        return true;
    }

    private void imprimirRelatorio() {
        this.correntistaFiltro.setCliente(this.cliente.getCodigo());
        if (this.textDataInicial.getValue() == null || this.textDataFinal.getValue() == null) {
            this.correntistaFiltro.setDataInicial("");
            this.correntistaFiltro.setDataFinal("");
        } else {
            this.correntistaFiltro.setDataInicial(this.textDataInicial.getValue().toString());
            this.correntistaFiltro.setDataFinal(this.textDataFinal.getValue().toString());
        }
        this.relatorioCorrentista.setCliente(this.cliente.getNome());
        this.relatorioCorrentista.setLimiteEmCredito(this.labelSaldoLimiteEmCredito.getText());
        this.relatorioCorrentista.setSaldoDevedor(this.labelValorAReceber.getText());
        this.relatorioCorrentista.setSaldoDisponivel(this.labelSaldoDisponivel.getText());

        this.relatorioCorrentista.imprimirCorrentista(this.correntistaFiltro);
    }

    private void excluirMovimentacao() {
        try {
            if (this.textDataInicial.getValue() == null || this.textDataFinal.getValue() == null) {
                this.correntistaService.excluirMovimentacaoCorrentista("", "", cliente.getCodigo());
            } else {
                this.correntistaService.excluirMovimentacaoCorrentista(this.textDataInicial.getValue().toString(), this.textDataFinal.getValue().toString(), cliente.getCodigo());
            }
            this.mostrarMensagem("Excluido com sucesso!!", Alert.AlertType.INFORMATION);
            this.realizarPesquisa();
        } catch (CorrentistaException ex) {
            this.mostrarMensagem(ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarMovimentacoes() {
        if (this.cliente == null) {
            this.mostrarMensagem("Selecione um cliente", Alert.AlertType.WARNING);
            return;
        }
        try {
            this.listarMovimentacoes();
            this.ancoraMovimentacao.setVisible(true);
            this.preencherInformaLabelsMovimentacao(this.labelSaldoDisponivel.getText(), this.labelValorAReceber.getText());
        } catch (CorrentistaException ex) {
            this.mostrarMensagem(ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void listarMovimentacoes() throws CorrentistaException {
        this.correntistaFiltro = null;
        this.correntistaFiltro = new CorrentistaFiltro();
        this.correntistaFiltro.setCliente(this.cliente.getCodigo());
        if (this.textDataInicialMovimentacao.getValue() == null || this.textDataFinalMovimentacao.getValue() == null) {
            this.correntistaFiltro.setDataInicial("");
            this.correntistaFiltro.setDataFinal("");
        } else {
            this.correntistaFiltro.setDataInicial(this.textDataInicialMovimentacao.getValue().toString());
            this.correntistaFiltro.setDataFinal(this.textDataFinalMovimentacao.getValue().toString());
        }
        List<Correntista> movimentacaoCorrentista = this.correntistaService.listarMovimentacaoCorrentista(this.correntistaFiltro);
        this.correntistas.clear();
        this.correntistas.addAll(movimentacaoCorrentista);
    }

    private void removerCorrentista(Correntista correntista) {
        Date dataProcesso = correntista.getDataDeProcesso();
        Date dataLancamento = correntista.getDataLancamento();
        String dataDeLancamento = new Date(dataLancamento.getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString();
        String dataDeProcesso = new Date(dataProcesso.getTime()).toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try {
            if (correntista.getTipoMovimento().equals("A")) {
                this.mostrarMensagem("Movimento não pode ser apagado\n foi gerado através do frente de loja.", Alert.AlertType.WARNING);
                return;
            }
            this.correntistaService.excluirMovimentacao(dataDeLancamento, dataDeProcesso, cliente.getCodigo());
            this.correntistas.remove(correntista);
        } catch (CorrentistaException ex) {
            this.mostrarMensagem("Erro ao excluir movimento", Alert.AlertType.ERROR);
        }
    }

    private void mostrarSaldoDaMovimentacao() {
        double limiteDeCredito = this.cliente.getLimiteCredito();
        double totalDeCredito = this.correntistas.stream().mapToDouble(Correntista::getCredito).sum();
        double totalDeDebito = this.correntistas.stream().mapToDouble(Correntista::getDebito).sum();
        double saldoDisponivel = (limiteDeCredito + totalDeCredito) - totalDeDebito;
        double saldoDevedor = totalDeCredito - totalDeDebito;
        String tituloSaldoDisponivel = saldoDisponivel < 0 ? "0,00" : FormatterUtil.getValorFormatado(saldoDisponivel);
        String tituloSaldoDevedor = saldoDevedor > 0 ? "0,00" : FormatterUtil.getValorFormatado(saldoDevedor);
        this.preencherInformaLabelsMovimentacao(tituloSaldoDisponivel, tituloSaldoDevedor);
    }

    private void preencherInformaLabelsMovimentacao(String saldoDisponivel, String saldoDevedor) {
        this.labelMovimentacaoSaldoDisponivel.setText("Saldo Disponivel: R$ " + saldoDisponivel);
        this.labelMovimentacaoSaldoDevedor.setText("Saldo Devedor: R$ " + saldoDevedor);
    }

    private void mostrarPermissaoDeUsuario() {
        this.ancoraPermissao.setVisible(true);
        this.textUsuario.requestFocus();
    }

    private void validarUsuario() {
        try {
            if (this.textUsuario.getText().isEmpty()) {
                this.mostrarMensagem("Informe o nome do usuario.", Alert.AlertType.WARNING);
                this.textUsuario.requestFocus();
                return;
            }
            if (this.textSenha.getText().isEmpty()) {
                this.mostrarMensagem("Informe a senha do usuario.", Alert.AlertType.WARNING);
                this.textSenha.requestFocus();
                return;
            }
            Usuario usuario = this.usuarioService.buscarUsuario(this.textUsuario.getText(), this.textSenha.getText());
            if (!usuario.isGerente()) {
                this.mostrarMensagem("Usuario não tem permissão", Alert.AlertType.WARNING);
                return;
            }
            this.ancoraPermissao.setVisible(false);
            this.excluirMovimentacao();
        } catch (UsuarioException ex) {
            this.mostrarMensagem(ex.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
