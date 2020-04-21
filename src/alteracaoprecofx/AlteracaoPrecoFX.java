package alteracaoprecofx;

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
        FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("/visao/FrmBanco.fxml"));
        Parent root = fXMLLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
