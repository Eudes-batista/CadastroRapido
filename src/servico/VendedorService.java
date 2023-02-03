package servico;

import controle.ConectaBanco;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Vendedor;

public class VendedorService {
    
    private final ConectaBanco conectaBanco;

    public VendedorService() {
        this.conectaBanco = new ConectaBanco();
    }
    
    public List<Vendedor> listarVendedores() {
        List<Vendedor> vendedores = new ArrayList<>();
        if(!this.conectaBanco.conectar()){
            return vendedores;
        }
        if(!this.conectaBanco.executaSQL("select RVIDEVEN,RVNOMEVE from SFTA01")){
            this.conectaBanco.desconecta();
            return vendedores;
        }
        try {
            if(!this.conectaBanco.getResultSet().first()){
                this.conectaBanco.desconecta();
                return vendedores;
            }
            do{
                Vendedor vendedor = new Vendedor();
                vendedor.setCodigo(this.conectaBanco.getResultSet().getString("RVIDEVEN"));
                vendedor.setNome(this.conectaBanco.getResultSet().getString("RVNOMEVE"));
                vendedores.add(vendedor);
            }while(this.conectaBanco.getResultSet().next());
        } catch (SQLException ex) {
            vendedores.clear();
        }
        this.conectaBanco.desconecta();
        return vendedores;
    }
    
    
    
}
