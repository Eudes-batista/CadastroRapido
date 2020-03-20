package controle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FrmBancoController implements Initializable {

    @FXML
    private Label labelHost;
    @FXML
    private Label labelCaminho;
    @FXML
    private TextField editHost;
    @FXML
    private TextField editCaminho;
    @FXML
    private Button btIniciar;
    @FXML
    private Button btSair;
    @FXML
    private Button btPesquisar;

    public void Sair() {
        Stage stage = (Stage) btSair.getScene().getWindow();
        stage.close();
    }

    public void Host() {
        String hot = editHost.getText();
        if (hot.isEmpty()) {
            editCaminho.requestFocus();
        } else {
            btIniciar.requestFocus();
        }
    }

    public void Pesquisa() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Buscar Banco do Omega");
        ExtensionFilter extensionFilter = new ExtensionFilter("*.GDB", "GDB");
        fileChooser.setSelectedExtensionFilter(extensionFilter);
        File retorno = fileChooser.showOpenDialog(null);

        if (retorno != null) {
            File file = retorno;
            String caminho = file.getAbsolutePath();
            editCaminho.setText(caminho);
        }
    }
    static Stage stageFrmAlteracao;

    @FXML
    public void Iniciar() {

        ConectaBanco conecta = new ConectaBanco();
        try {
            String host = editHost.getText(), caminho = editCaminho.getText();
            ArquivoConfiguracao.GerarArquivo(host, caminho);
            conecta.setHost(host).setCaminho(caminho);
            if (conecta.conexao()) {
                Pane root = FXMLLoader.load(getClass().getResource("/visao/FrmAlteracaoPreco.fxml"));
                Scene scene = new Scene(root);
                Stage preco = new Stage();
                preco.setScene(scene);
                preco.toFront();
                preco.initStyle(StageStyle.UNDECORATED);
                stageFrmAlteracao = preco;
                preco.show();
                Stage stage = (Stage) btIniciar.getScene().getWindow();
                stage.close();
            }
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERRO");
            alert.setHeaderText("Erro Encontra o caminho do arquivo FrmAlteracaoPreco.fxml");
            alert.setContentText(ex.getMessage());
            alert.show();
        } finally {
            conecta.desconecta();
        }
    }

    @FXML
    private void KeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Iniciar();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Path path = Paths.get("Preco.txt");
        if (Files.exists(path)) {
            try {
                List<String> lista = Files.readAllLines(path);
                if (lista != null && !lista.isEmpty() && lista.size() > 1) {
                    editHost.setText(lista.get(0));
                    editCaminho.setText(lista.get(1));
                }
            } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setContentText("Erro ao ler e escrever no arquivo \n detalhe do erro: " + ex.getMessage());
                alert.show();
            }
        }

        if (!editHost.getText().isEmpty() && !editCaminho.getText().isEmpty()) {
            editHost.setEditable(false);
            editHost.setFocusTraversable(false);
            editCaminho.setEditable(false);
            editCaminho.setFocusTraversable(false);
            btIniciar.requestFocus();
        }
        editHost.setOnMouseClicked((e) -> editHost.setEditable(e.getClickCount() == 2));
        editCaminho.setOnMouseClicked((e) -> editCaminho.setEditable(e.getClickCount() == 2));
    }
}
