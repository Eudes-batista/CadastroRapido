package servico;

import controle.ConectaBanco;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FormaPagamentoService {

    private final ConectaBanco conectaBanco;

    public FormaPagamentoService() {
        this.conectaBanco = new ConectaBanco();
    }

    public List<String> listarFormasPagamento() {
        List<String> formasPagamento = new ArrayList<>();
        if (!this.conectaBanco.conexao()) {
            return formasPagamento;
        }
        String sql = "select CDTIPCAR from sosa09 group by CDTIPCAR ";
        if (!this.conectaBanco.executaSQL(sql)) {
            this.conectaBanco.desconecta();
            return formasPagamento;
        }
        ResultSet resultSet = this.conectaBanco.getResultSet();
        try {
            if (!resultSet.first()) {
                this.conectaBanco.desconecta();
            }
            do {
                formasPagamento.add(resultSet.getString("CDTIPCAR"));
            } while (resultSet.next());
        } catch (SQLException ex) {
            formasPagamento.clear();
        }
        this.conectaBanco.desconecta();
        return formasPagamento;
    }

}
