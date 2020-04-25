/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controle.componentes;

import com.jfoenix.controls.JFXDatePicker;
import componentesjavafx.TextFieldCustom;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import modelo.Cliente;
import modelo.Correntista;

/**
 *
 * @author ZOOM
 */
public abstract class CorrentistaComponente {

    @FXML
    protected AnchorPane ancoraPrincipal;

    @FXML
    protected AnchorPane ancoraSaldo;

    @FXML
    protected Label labelSaldoDisponivel;

    @FXML
    protected Label labelSaldoDevedor;

    @FXML
    protected Label labelSaldoLimiteEmCredito;

    @FXML
    protected Label labelTotalCredito;

    @FXML
    protected Label labelTotalDebito;

    @FXML
    protected JFXDatePicker textDataInicial;

    @FXML
    protected JFXDatePicker textDataFinal;

    @FXML
    protected Button btCredito;

    @FXML
    protected Button btDebito;

    @FXML
    protected TextField textPesquisa;

    @FXML
    protected Button btImprimir;

    @FXML
    protected AnchorPane ancoraListarClientes;

    @FXML
    protected TableView<Cliente> tabela;

    @FXML
    protected TableColumn<Cliente, String> columnCodigo;

    @FXML
    protected TableColumn<Cliente, String> columnCliente;

    @FXML
    protected TableColumn<Cliente, String> columnLimiteCredito;

    @FXML
    protected Button btSair;

    @FXML
    protected Button btSelecionarCliente;

    @FXML
    protected AnchorPane ancoraLancamento;

    @FXML
    protected Label labelTipoLancamento;

    @FXML
    protected TextFieldCustom textDescricao;

    @FXML
    protected TextFieldCustom textValor;

    @FXML
    protected Button btSalvar;

    @FXML
    protected Button btVoltar;

    @FXML
    protected Button btSairContaCorrente;

    @FXML
    protected Button btExcluirMovimentacoes;

    @FXML
    protected Button btMovimentacoes;

    @FXML
    protected Label labelContaCorrente;

    @FXML
    protected Label labelValorAReceber;

    @FXML
    protected AnchorPane ancoraMovimentacao;

    @FXML
    protected Button btVoltarMovimentacoes;

    @FXML
    protected TableView<Correntista> tabelaMovimentacoes;

    @FXML
    protected TableColumn<Correntista, String> columnDataLancamento;

    @FXML
    protected TableColumn<Correntista, String> columnDataDescricao;

    @FXML
    protected TableColumn<Correntista, String> columnCredito;

    @FXML
    protected TableColumn<Correntista, String> columnDebito;

    @FXML
    protected TableColumn<Correntista, String> columnUsuario;

    @FXML
    protected TableColumn<Correntista, String> columnExcluir;

    @FXML
    protected JFXDatePicker textDataInicialMovimentacao;

    @FXML
    protected JFXDatePicker textDataFinalMovimentacao;

    @FXML
    protected Button btPesquisa;

    @FXML
    protected Label labelMovimentacaoSaldoDisponivel;

    @FXML
    protected Label labelMovimentacaoSaldoDevedor;

    @FXML
    protected AnchorPane ancoraPermissao;

    @FXML
    protected TextField textUsuario;

    @FXML
    protected PasswordField textSenha;

    @FXML
    protected Button btConfirmarUsuario;

    @FXML
    protected Button btVoltarPermissao;

}
