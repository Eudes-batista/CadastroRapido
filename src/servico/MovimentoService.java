package servico;

import controle.ConectaBanco;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import modelo.Movimento;
import modelo.TipoMovimento;
import modelo.dto.MovimentoDTO;

public class MovimentoService {

    private final ConectaBanco conecta = new ConectaBanco();

    public boolean salvar(Movimento movimento) {
        if (!conecta.conexao()) {
            return false;
        }
        try {
            String sql = "UPDATE OR INSERT INTO SCEA02 (MCCODEMP,MCTIPMOV,MCNUMERO,MCDATMOV,MCHISTOR,MCIDENTI,MCDATALT)";
            sql += " VALUES('" + movimento.getEmpresa() + "','" + movimento.getTipo() + "','" + movimento.getDocumento()
                    + "','" + new SimpleDateFormat("yyyy-MM-dd").format(movimento.getDataMovimento())
                    + "','" + movimento.getObservacao() + "','" + movimento.getUsuario() + "','" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(movimento.getDataAtualizacao()) + "')";
            Statement pst = conecta.getConnection().createStatement();
            pst.execute(sql);
            conecta.getConnection().commit();
            pst.close();
            this.conecta.desconecta();
            return true;
        } catch (SQLException ex) {
            try {
                conecta.getConnection().rollback();
            } catch (SQLException ex1) {
            }
            conecta.desconecta();
            return false;
        }
    }

    public List<String> listarEmpresas() {
        List<String> empresas = new ArrayList<>();
        if (!conecta.conexao()) {
            return empresas;
        }
        String sql = "SELECT LDCODEMP FROM LAPA13 WHERE LDUSUARI LIKE '%SUPORTE%'";
        if (conecta.executaSQL(sql)) {
            this.conecta.desconecta();
            return empresas;
        }
        try {
            if (!conecta.getResultSet().first()) {
                this.conecta.desconecta();
                return empresas;
            }
            do {
                empresas.add(conecta.getResultSet().getString("LDCODEMP"));
            } while (conecta.getResultSet().next());
        } catch (SQLException ex) {
            empresas.clear();
        }
        this.conecta.desconecta();
        return empresas;
    }

    public List<TipoMovimento> listarTipos() {
        List<TipoMovimento> tipoMovimentos = new ArrayList<>();
        if (!conecta.conexao()) {
            return tipoMovimentos;
        }
        String sql = "select T62TPMOV,T62DESCR from LAPT62 where T62TPMOV in('ENAQ','SNAQ','ENTP','ENDP') order by T62TPMOV";
        if (!conecta.executaSQL(sql)) {
            this.conecta.desconecta();
            return tipoMovimentos;
        }
        try {
            if (!conecta.getResultSet().first()) {
                this.conecta.desconecta();
                return tipoMovimentos;
            }
            do {
                tipoMovimentos.add(new TipoMovimento(conecta.getResultSet().getString("T62TPMOV"), conecta.getResultSet().getString("T62DESCR")));
            } while (conecta.getResultSet().next());
        } catch (SQLException ex) {
            tipoMovimentos.clear();
        }
        this.conecta.desconecta();
        return tipoMovimentos;
    }

    public List<TipoMovimento> listarTodosTipos() {
        List<TipoMovimento> tipoMovimentos = new ArrayList<>();
        if (!conecta.conexao()) {
            return tipoMovimentos;
        }
        String sql = "select T62TPMOV,T62DESCR from LAPT62 order by T62TPMOV";
        if (!conecta.executaSQL(sql)) {
            this.conecta.desconecta();
            return tipoMovimentos;
        }
        try {
            if (!conecta.getResultSet().first()) {
                this.conecta.desconecta();
                return tipoMovimentos;
            }
            do {
                tipoMovimentos.add(new TipoMovimento(conecta.getResultSet().getString("T62TPMOV"), conecta.getResultSet().getString("T62DESCR")));
            } while (conecta.getResultSet().next());
        } catch (SQLException ex) {
            tipoMovimentos.clear();
        }
        this.conecta.desconecta();
        return tipoMovimentos;
    }

    public List<Movimento> listarMovimentos(String data, String documento) {
        Set<MovimentoDTO> movimentos = new HashSet<>();
        if (!conecta.conexao()) {
            return movimentos.stream().collect(Collectors.toList());
        }
        String sql = "select MCCODEMP as empresa,MCTIPMOV as tipo,MCNUMERO as documento,MCDATMOV as data,MCHISTOR as observacao from SCEA02 where MCNUMERO like '%" + documento + "%' and MCDATMOV BETWEEN '" + data + " 00:00:00' and '" + data + " 23:59:59' group by MCCODEMP,MCTIPMOV,MCNUMERO,MCDATMOV,MCHISTOR order by MCNUMERO";
        if (!conecta.executaSQL(sql)) {
            this.conecta.desconecta();
            return movimentos.stream().collect(Collectors.toList());
        }
        try {
            if (!conecta.getResultSet().first()) {
                this.conecta.desconecta();
                return movimentos.stream().collect(Collectors.toList());
            }
            do {
                MovimentoDTO movimentoDTO = new MovimentoDTO();
                movimentoDTO.setEmpresa(conecta.getResultSet().getString("empresa"));
                movimentoDTO.setTipo(conecta.getResultSet().getString("tipo"));
                movimentoDTO.setDocumento(conecta.getResultSet().getString("documento"));
                movimentoDTO.setDataMovimento(conecta.getResultSet().getDate("data"));
                movimentoDTO.setObservacao(conecta.getResultSet().getString("observacao"));
                movimentos.add(movimentoDTO);
            } while (conecta.getResultSet().next());
        } catch (SQLException ex) {
            movimentos.clear();
        }
        this.conecta.desconecta();
        return movimentos.stream().collect(Collectors.toList());
    }

    public void excluirMovimento(Movimento movimento) {
        if (!conecta.conexao()) {
            return;
        }
        try {
            String sql = "delete from SCEA02 where MCCODEMP=? and MCTIPMOV=? and MCNUMERO=?";
            PreparedStatement pst = conecta.getConnection().prepareStatement(sql);
            pst.setString(1, movimento.getEmpresa());
            pst.setString(2, movimento.getTipo());
            pst.setString(3, movimento.getDocumento());
            pst.execute();
            conecta.getConnection().commit();
            pst.close();
            conecta.desconecta();
        } catch (SQLException ex) {
            conecta.desconecta();
        }
    }

    public Movimento verificarMovimento(Movimento movimento) {
        if (!conecta.conexao()) {
            return null;
        }
        String sql = "select MCCODEMP,MCTIPMOV,MCNUMERO,MCDATMOV,MCHISTOR,MCIDENTI,MCDATALT from SCEA02 where MCCODEMP='" + movimento.getEmpresa() + "' and MCTIPMOV='" + movimento.getTipo() + "' and MCNUMERO='" + movimento.getDocumento() + "' ";
        if (!conecta.executaSQL(sql)) {
            this.conecta.desconecta();
            return null;
        }
        try {
            if (!conecta.getResultSet().first()) {
                this.conecta.desconecta();
                return null;
            }
            Movimento movimentoPreenchido = new Movimento(
                    conecta.getResultSet().getString("MCCODEMP"),
                    conecta.getResultSet().getString("MCTIPMOV"),
                    conecta.getResultSet().getString("MCNUMERO"),
                    conecta.getResultSet().getString("MCHISTOR"),
                    conecta.getResultSet().getString("MCIDENTI"),
                    conecta.getResultSet().getDate("MCDATMOV"),
                    conecta.getResultSet().getDate("MCDATALT")
            );
            this.conecta.desconecta();
            return movimentoPreenchido;
        } catch (SQLException ex) {
            this.conecta.desconecta();
            return null;
        }
    }
}
