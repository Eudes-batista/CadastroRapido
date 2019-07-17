package controle;

import componentesjavafx.TextFieldCustom;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import modelo.Grupo;

public class FrmGrupoController implements Initializable {

    @FXML
    private AnchorPane ancoraPesquisa;

    @FXML
    private Button btIncluir;

    @FXML
    private Button btSair;

    @FXML
    private TextField editPesquisa;

    @FXML
    private TableView<Grupo> tabela;

    @FXML
    private TableColumn<Grupo,String> columnCodigo;

    @FXML
    private TableColumn<Grupo,String> columnNome;

    @FXML
    private AnchorPane ancoraCadastro;

    @FXML
    private TextFieldCustom editCodigo;

    @FXML
    private TextFieldCustom editNome;

    @FXML
    private Button btSalvar;

    @FXML
    private Button btVoltar;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
}
