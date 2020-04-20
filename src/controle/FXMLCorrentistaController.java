/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controle;

import com.jfoenix.controls.JFXDatePicker;
import componentesjavafx.TextFieldCustom;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author ZOOM
 */
public class FXMLCorrentistaController implements Initializable {

    
       @FXML
    private AnchorPane ancoraPrincipal;

    @FXML
    private AnchorPane ancoraSaldo;

    @FXML
    private Label labelSaldoDisponivel;

    @FXML
    private Label labelSaldoDevedor;

    @FXML
    private Label labelSaldoLimiteEmCredito;

    @FXML
    private Label labelTotalCredito;

    @FXML
    private Label labelTotalDebito;

    @FXML
    private JFXDatePicker textDataInicial;

    @FXML
    private JFXDatePicker textDataFinal;

    @FXML
    private Button btCredito;

    @FXML
    private Button btDebito;

    @FXML
    private TextField textPesquisa;

    @FXML
    private Button btDebito1;

    @FXML
    private AnchorPane ancoraListarClientes;

    @FXML
    private TableView<?> tabela;

    @FXML
    private TableColumn<?, ?> columnCodigo;

    @FXML
    private TableColumn<?, ?> columnCliente;

    @FXML
    private TableColumn<?, ?> columnLimiteCredito;

    @FXML
    private Button btSair;

    @FXML
    private Button btCredito1;

    @FXML
    private AnchorPane ancoraLancamento;

    @FXML
    private Label labelTipoLancamento;

    @FXML
    private TextFieldCustom textDescricao;

    @FXML
    private TextFieldCustom textValor;

    @FXML
    private Button btSalvar;

    @FXML
    private Button btVoltar;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
