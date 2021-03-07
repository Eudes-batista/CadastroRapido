package servico;

import controle.ConectaBanco;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.scene.control.Alert;

public class ManutencaoNcmService {

    private final ConectaBanco conectaBanco;

    public ManutencaoNcmService() {
        this.conectaBanco = new ConectaBanco();
    }

    public void atualizarNcm(String ncmExpirado, String ncmNovo) throws SQLException {
        if (!this.conectaBanco.conexao()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText("Não foi possivel se conectar com o banco de dados.");
            alert.show();
            return;
        }
        try (PreparedStatement preparedStatement = this.conectaBanco.getConnection().prepareStatement("update scea01 set prclassi=? where set prclassi=?")) {
            preparedStatement.setString(1, ncmNovo);
            preparedStatement.setString(2, ncmExpirado);
            preparedStatement.executeUpdate();
            this.conectaBanco.getConnection().commit();
        }
        this.conectaBanco.desconecta();
    }

}
