package alteracaoprecofx;

import controle.ConectaBanco;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import util.FXMLUtil;

public class AlteracaoPrecoFX extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Platform.setImplicitExit(true);
        if (this.conectar()) {
            this.abrirTelaMenu();
            return;
        }
        this.abrirConexaoComBanco();
    }

    private void abrirConexaoComBanco() throws IOException {
        Stage stage = FXMLUtil.abrirFXML("FrmBanco");
        stage.setMaximized(false);
        stage.show();
    }

    private void abrirTelaMenu() throws IOException {
        Stage stage = FXMLUtil.abrirFXML("FXMLMenu");
        stage.setMaximized(true);
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
