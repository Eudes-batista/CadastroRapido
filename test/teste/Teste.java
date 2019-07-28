package teste;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Teste extends Application{

    public static void main(String[] args) throws IOException {
        launch(args);
        
    }
    public void abrir(Stage stage) throws IOException {
        Parent root =FXMLLoader.load(getClass().getResource("/visao/FrmRelatorio.fxml"));
        Scene scene = new Scene(root);        
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        abrir(primaryStage);
    }
    
    
}
