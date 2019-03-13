package controle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javafx.scene.control.Alert;
import javax.swing.JOptionPane;

public class ConectaBanco {

    private final String driver = "org.firebirdsql.jdbc.FBDriver";
    private Statement stm;
    private ResultSet rs;
    private Connection conn;

    public boolean conexao(String host, String banco) {

        String caminho = "jdbc:firebirdsql://" + host + ":3050/" + banco;

        try {
            Class.forName(driver);
            Properties props = new Properties();
            props.put("user", "SYSDBA");
            props.put("password", "masterkey");
            props.put("charset", "UTF8");
            props.put("lc_ctype", "ISO8859_1");
            conn = DriverManager.getConnection(caminho,props);
            conn.setAutoCommit(false);
            return true;
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            return false;
        }
    }

    public void desconecta() {

        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    public boolean executaSQL(String sql) {
        try {

            if (conn != null) {
                stm = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                rs = stm.executeQuery(sql);
                return true;
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("AVISO");
                alert.setContentText("a conexão não foi realizada com o banco de dados");
                alert.show();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }

        return false;
    }

    public ResultSet getRs() {
        return rs;
    }

    public void setRs(ResultSet rs) {
        this.rs = rs;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

}
