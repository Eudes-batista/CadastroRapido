package controle;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

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
    private ComboBox comboEmpresa;
    
    @FXML
    private DatePicker dataInicial;    
    @FXML
    private DatePicker dataFinal;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
}
