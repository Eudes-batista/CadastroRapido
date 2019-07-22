
package servico;

import controle.ConectaBanco;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import modelo.Produto;


public class PesquisaService {
    
    private final ConectaBanco conectaBanco = new ConectaBanco();
    private boolean chekcCancelados;
    private final ObservableList<Produto> produtos = FXCollections.observableArrayList();
    
    public ObservableList<Produto> listarProdutos(String pesquisa) {        
        if (this.conectaBanco.conexao()) {
            String cancelados = this.chekcCancelados ?  "PRDATCAN IS NOT NULL" : "PRDATCAN is null";
            String sql = "select first 20 PRREFERE,PRCODBAR,PRDESCRI,EEPBRTB1,EET2PVD1,PRQTDATA from scea01 left outer join scea07 on(eerefere=prrefere) "
                    + "where PRDESCRI like '%" + pesquisa.trim().toUpperCase() + "%' AND  "+cancelados
                    + " group by PRREFERE,PRCODBAR,PRDESCRI,EEPBRTB1,EET2PVD1,PRQTDATA";
            if (this.conectaBanco.executaSQL(sql)) {
                try {
                    if (this.conectaBanco.getRs().first()) {
                        this.produtos.clear();
                        do {
                            this.produtos.add(new Produto(this.conectaBanco.getRs().getString("PRREFERE"),
                                    this.conectaBanco.getRs().getString("PRDESCRI"),
                                    this.conectaBanco.getRs().getDouble("EEPBRTB1"),
                                    this.conectaBanco.getRs().getDouble("EET2PVD1"),
                                    this.conectaBanco.getRs().getDouble("PRQTDATA"),
                                    conectaBanco.getRs().getString("PRCODBAR")));
                        } while (this.conectaBanco.getRs().next());
                    }
                } catch (SQLException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERRO");
                    alert.setContentText("Erro ao consultar\n detalhe do erro " + ex.getMessage());
                    alert.show();
                }
            }
            this.conectaBanco.desconecta();
        }
        return this.produtos;
    }

    public void setChekcCancelados(boolean chekcCancelados) {
        this.chekcCancelados = chekcCancelados;
    }
    
}