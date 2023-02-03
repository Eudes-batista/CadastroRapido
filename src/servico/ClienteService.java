package servico;

import controle.ConectaBanco;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Cliente;
import modelo.dto.ClientesCorrentistaDTO;

public class ClienteService {

    private final ConectaBanco conectaBanco;

    public ClienteService() {
        this.conectaBanco = new ConectaBanco();
    }

    public List<Cliente> listarClientes(String pesquisa) {
        List<Cliente> clientes = new ArrayList<>();
        if (!this.conectaBanco.conectar()) {
            return clientes;
        }
        String sql = "select \n"
                + " CLCODIGO\n"
                + ",CLRAZSOC\n"
                + ",CLNOMFAN\n"
                + ",CLCGCCPF\n"
                + ",CLLIMCRE \n"
                + "from \n"
                + " crea01 \n"
                + "where \n"
                + "    CLCODIGO = '" + pesquisa + "' \n"
                + "or  CLRAZSOC like '%" + pesquisa + "%' \n"
                + "or  CLNOMFAN like '%" + pesquisa + "%' \n"
                + "or  CLCGCCPF = '" + pesquisa + "'\n";
        if (!this.conectaBanco.executaSQL(sql)) {
            this.conectaBanco.desconecta();
            return clientes;
        }
        try {
            this.conectaBanco.getResultSet().first();
            do {
                Cliente cliente = new Cliente();
                cliente.setCodigo(this.conectaBanco.getResultSet().getString("CLCODIGO"));
                cliente.setNome(this.conectaBanco.getResultSet().getString("CLRAZSOC"));
                cliente.setLimiteCredito(this.conectaBanco.getResultSet().getDouble("CLLIMCRE"));
                clientes.add(cliente);
            } while (this.conectaBanco.getResultSet().next());
        } catch (SQLException ex) {
            clientes.clear();
        }
        this.conectaBanco.desconecta();
        return clientes;
    }

    public Cliente buscarCliente(String pesquisa) {
        Cliente cliente = null;
        if (!this.conectaBanco.conectar()) {
            return cliente;
        }
        String sql = "select \n"
                + " CLCODIGO\n"
                + ",CLRAZSOC\n"
                + ",CLNOMFAN\n"
                + ",CLCGCCPF\n"
                + ",CLLIMCRE \n"
                + "from \n"
                + " crea01 \n"
                + "where \n"
                + "    CLCODIGO = '" + pesquisa + "' \n"
                + "or  CLCGCCPF = '" + pesquisa + "'\n";
        if (!this.conectaBanco.executaSQL(sql)) {
            this.conectaBanco.desconecta();
            return cliente;
        }
        try {
            this.conectaBanco.getResultSet().first();
            cliente = new Cliente();
            cliente.setCodigo(this.conectaBanco.getResultSet().getString("CLCODIGO"));
            cliente.setNome(this.conectaBanco.getResultSet().getString("CLRAZSOC"));
            cliente.setLimiteCredito(this.conectaBanco.getResultSet().getDouble("CLLIMCRE"));
        } catch (SQLException ex) {
            cliente = null;
        }
        this.conectaBanco.desconecta();
        return cliente;
    }

    public ClientesCorrentistaDTO consultarSaldosCorrentida(String codigoCliente, String dataInicial, String dataFinal) {
        ClientesCorrentistaDTO clientesCorrentistaDTO = new ClientesCorrentistaDTO();
        clientesCorrentistaDTO.setSaldoDevedor(0.0);
        clientesCorrentistaDTO.setSaldoDisponivel(0.0);
        clientesCorrentistaDTO.setSaldoCredito(0.0);
        clientesCorrentistaDTO.setTotalCredito(0.0);
        clientesCorrentistaDTO.setTotalDebito(0.0);
        clientesCorrentistaDTO.setValorReceber(0.0);
        if (!this.conectaBanco.conectar()) {
            return clientesCorrentistaDTO;
        }
        String sql = "select \n"
                + " (coalesce(sum(crcredit),0)+ coalesce(max(CLLIMCRE),0)) - coalesce(sum(crdebito),0) as saldo_disponivel\n"
                + " ,coalesce(sum(crcredit),0) as credito\n"
                + " ,coalesce(sum(crdebito),0) as debito\n"
                + " ,coalesce(max(CLLIMCRE),0) as limite_credito\n"
                + "from \n"
                + "  crea01 \n"
                + "left outer join \n"
                + "  crea15 on(CRCLIENT=CLCODIGO)\n"
                + "where \n"
                + "  clcodigo='" + codigoCliente + "' \n";
                if(!dataInicial.isEmpty()){
                    sql += " and CRLANCAM between '" + dataInicial + "' and '" + dataFinal + "' \n";
                }
                sql += "group by CLCODIGO";
        if (!this.conectaBanco.executaSQL(sql)) {
            this.conectaBanco.desconecta();
            return clientesCorrentistaDTO;
        }
        try {
            this.conectaBanco.getResultSet().next();
            double limiteDeCredito = this.conectaBanco.getResultSet().getDouble("limite_credito");
            double credito = this.conectaBanco.getResultSet().getDouble("credito");
            double limiteDeCreditoMasCredito = credito + limiteDeCredito;
            double debito = this.conectaBanco.getResultSet().getDouble("debito");
            double saldoDevedor = (limiteDeCreditoMasCredito - debito) > 0 ? 0 : (limiteDeCreditoMasCredito - debito);
            double saldoDisponivel = this.conectaBanco.getResultSet().getDouble("saldo_disponivel");
            clientesCorrentistaDTO.setSaldoDisponivel(saldoDisponivel < 0 ? 0 : saldoDisponivel);
            clientesCorrentistaDTO.setSaldoDevedor(saldoDevedor);
            clientesCorrentistaDTO.setSaldoCredito(limiteDeCredito);
            clientesCorrentistaDTO.setTotalCredito(credito);
            clientesCorrentistaDTO.setTotalDebito(debito);
            double valorAReceber = (credito - debito) > 0 ? 0 : (credito - debito);
            clientesCorrentistaDTO.setValorReceber(valorAReceber);
        } catch (SQLException ex) {
        }
        this.conectaBanco.desconecta();
        return clientesCorrentistaDTO;
    }

}
