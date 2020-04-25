package servico;

import exception.CorrentistaException;
import controle.ConectaBanco;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import modelo.Correntista;
import modelo.dto.CorrentistaFiltro;

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
            this.conectaBanco.desconecta();
        } catch (SQLException ex) {
            this.conectaBanco.desconecta();
            throw new CorrentistaException("Não foi possivel salvar lancamento correntista");
        }
    }

    public void excluirMovimentacaoCorrentista(String dataInicial, String dataFinal, String cliente) throws CorrentistaException {
        if (!this.conectaBanco.conexao()) {
            throw new CorrentistaException("Não foi possivel se conectar com o servidor");
        }
        String sql = "delete from crea15 where CRCLIENT='" + cliente + "'";
        if (!dataInicial.isEmpty()) {
            sql += " and CRLANCAM between '" + dataInicial + "' and '" + dataFinal + "'";
        }
        try {
            try (PreparedStatement preparedStatement = this.conectaBanco.getConnection().prepareStatement(sql)) {
                preparedStatement.execute();
                this.conectaBanco.getConnection().commit();
            }
            this.conectaBanco.desconecta();
        } catch (SQLException ex) {
            this.conectaBanco.desconecta();
            throw new CorrentistaException("Não foi possivel salvar lancamento correntista");
        }
    }

    public void excluirMovimentacao(String dataLancamento, String dataProcesso, String cliente) throws CorrentistaException {
        if (!this.conectaBanco.conexao()) {
            throw new CorrentistaException("Não foi possivel se conectar com o servidor");
        }
        String sql = "delete from crea15 where CRCLIENT='" + cliente + "'";
        sql += " and CRLANCAM ='" + dataLancamento + "' and CRPROCES='" + dataProcesso + "'";
        try {
            try (PreparedStatement preparedStatement = this.conectaBanco.getConnection().prepareStatement(sql)) {
                preparedStatement.execute();
                this.conectaBanco.getConnection().commit();
            }
            this.conectaBanco.desconecta();
        } catch (SQLException ex) {
            this.conectaBanco.desconecta();
            throw new CorrentistaException("Não foi possivel salvar lancamento correntista");
        }
    }

    public List<Correntista> listarMovimentacaoCorrentista(CorrentistaFiltro correntistaFiltro) throws CorrentistaException {
        if (!this.conectaBanco.conexao()) {
            throw new CorrentistaException("Não foi possivel se conectar com o servidor");
        }
        String sql = "select CRCLIENT,CRLANCAM,CRPROCES,CRUSUARI,CRTIPMOV,CRHISTOR,COALESCE(CRDEBITO,0) as CRDEBITO,COALESCE(CRCREDIT,0) as CRCREDIT from crea15 where CRCLIENT='" + correntistaFiltro.getCliente() + "' ";
        if (!correntistaFiltro.getDataInicial().isEmpty()) {
            sql += "and CRLANCAM between '" + correntistaFiltro.getDataInicial() + "' and '" + correntistaFiltro.getDataFinal() + "'";
        }
        if (!this.conectaBanco.executaSQL(sql)) {
            this.conectaBanco.desconecta();
            throw new CorrentistaException("Não foi possivel consultar movimentação");
        }
        List<Correntista> correntistas = new ArrayList<>();
        try {
            this.conectaBanco.getResultSet().first();
            do {
                correntistas.add(this.preencherCorrentista());
            } while (this.conectaBanco.getResultSet().next());
        } catch (SQLException ex) {
            correntistas.clear();
        }
        this.conectaBanco.desconecta();
        return correntistas;
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
                + correntista.getUsuario() + "','" + correntista.getTipoMovimento() + "','" + correntista.getDescricao() + "',"
                + correntista.getDebito() + "," + correntista.getCredito() + ","
                + correntista.getSaldoAnterior() + ")";
    }

    private Correntista preencherCorrentista() throws SQLException {
        Correntista correntista = new Correntista();
        correntista.setCodigoCliente(this.conectaBanco.getResultSet().getString("CRCLIENT"));
        correntista.setDataDeProcesso(this.conectaBanco.getResultSet().getTimestamp("CRPROCES"));
        correntista.setDataLancamento(this.conectaBanco.getResultSet().getDate("CRLANCAM"));
        correntista.setUsuario(this.conectaBanco.getResultSet().getString("CRUSUARI"));
        correntista.setDescricao(this.conectaBanco.getResultSet().getString("CRHISTOR"));
        correntista.setTipoMovimento(this.conectaBanco.getResultSet().getString("CRTIPMOV"));
        correntista.setCredito(this.conectaBanco.getResultSet().getDouble("CRCREDIT"));
        correntista.setDebito(this.conectaBanco.getResultSet().getDouble("CRDEBITO"));
        return correntista;
    }

}
