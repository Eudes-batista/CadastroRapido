package util;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FXMLUtil {

      public static Stage abrirFXML(String fxml) throws IOException {
        FXMLLoader fXMLLoader = new FXMLLoader(FXMLUtil.class.getResource("/visao/" + fxml + ".fxml"));
        Parent root = fXMLLoader.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        return stage;
    }
    
    public static void sair(Parent root) {
        ((Stage) root.getScene().getWindow()).close();
    }
      
    
}
