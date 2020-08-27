package alteracaoprecofx;

import controle.ConectaBanco;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AlteracaoPrecoFX extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Platform.setImplicitExit(true);
        if (this.conectar()) {
            this.abrirTelaCadastro();
            return;
        }
        this.abrirConexaoComBanco();
    }

    private void abrirConexaoComBanco() throws IOException {
        this.abrirFXML("FrmBanco",false);
    }

    private void abrirTelaCadastro() throws IOException {
        this.abrirFXML("FrmAlteracaoPreco",true);
    }

    private void abrirFXML(String fxml,boolean telaCheia) throws IOException {
        FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("/visao/" + fxml + ".fxml"));
        Parent root = fXMLLoader.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setMaximized(telaCheia);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    private boolean conectar() {
        Path path = Paths.get("Preco.txt");
        boolean conectou = false;
        if (Files.notExists(path)) {
            return conectou;
        }
        try {
            List<String> conteudos = Files.readAllLines(path);
            if (conteudos == null || conteudos.isEmpty() || conteudos.size() < 2) {
                return conectou;
            }
            String host = conteudos.get(0);
            String caminho = conteudos.get(1);
            ConectaBanco conectaBanco = new ConectaBanco();
            conectaBanco.setHost(host).setCaminho(caminho);
            conectou = conectaBanco.conexao();
            conectaBanco.desconecta();
        } catch (IOException ex) {
            conectou = false;
        }
        return conectou;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
