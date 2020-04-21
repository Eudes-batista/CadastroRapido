package servico;

import exception.CorrentistaException;
import controle.ConectaBanco;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import modelo.Correntista;

public class CorrentistaService {

    private final SimpleDateFormat dateTimeFormat;
    private final SimpleDateFormat dateFormat;
    private final ConectaBanco conectaBanco;

    public CorrentistaService() {
        this.conectaBanco = new ConectaBanco();
        this.dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    public void salvarMovimentacaoCorrentistaRetaguarda(Correntista correntista) throws CorrentistaException {
        if (!this.conectaBanco.conexao()) {
            throw new CorrentistaException("Não foi possivel se conectar com o servidor");
        }
        String sql = this.criarSqlInsercao(correntista);
        try {
            try (PreparedStatement preparedStatement = this.conectaBanco.getConnection().prepareStatement(sql)) {
                preparedStatement.executeUpdate();
                this.conectaBanco.getConnection().commit();
            }
        } catch (SQLException ex) {
            throw new CorrentistaException("Não foi possivel salvar lancamento correntista");
        }
        this.conectaBanco.desconecta();
    }

    private String criarSqlInsercao(Correntista correntista) {
        return "insert into CREA15 \n"
                + "(\n"
                + "  CRCLIENT,\n"
                + "  CRLANCAM,\n"
                + "  CRPROCES,\n"
                + "  CRUSUARI,\n"
                + "  CRTIPMOV,\n"
                + "  CRHISTOR,\n"
                + "  CRDEBITO,\n"
                + "  CRCREDIT,\n"
                + "  CRSLDPAR\n"
                + ")\n"
                + "values('" + correntista.getCodigoCliente() + "','"
                + this.dateFormat.format(correntista.getDataLancamento()) + "','"
                + this.dateTimeFormat.format(correntista.getDataDeProcesso()) + "','"
                + correntista.getUsuario() + "','"+correntista.getTipoMovimento()+"','" + correntista.getDescricao() + "',"
                + correntista.getDebito() + "," + correntista.getCredito() + ","
                + correntista.getSaldoAnterior() + ")";
    }

}
