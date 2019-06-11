package servico;

import controle.ConectaBanco;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.scene.control.Alert;
import modelo.Movimento;
import modelo.TipoMovimento;
import modelo.dto.MovimentoDTO;

public class MovimentoService {

    private final ConectaBanco conecta = new ConectaBanco();
    private String host, caminho;

    public MovimentoService() {
        this.BuscarCaminho();
    }

    public boolean salvar(Movimento movimento) {
        if (conecta.conexao(host, caminho)) {
            try {
                String sql = "INSERT INTO SCEA02 (MCCODEMP,MCTIPMOV,MCNUMERO,MCDATMOV,MCHISTOR,MCIDENTI,MCDATALT)";
                sql += " VALUES('"+movimento.getEmpresa()+"','"+movimento.getTipo()+"','"+movimento.getDocumento()+
                        "','" + new SimpleDateFormat("yyyy-MM-dd").format(movimento.getDataMovimento()) +
                        "','"+movimento.getObservacao()+"','"+movimento.getUsuario()+"','"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(movimento.getDataAtualizacao())+"')";
                Statement pst = conecta.getConn().createStatement();
                pst.execute(sql);
                conecta.getConn().commit();
                return true;
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                try {
                    conecta.getConn().rollback();
                } catch (SQLException ex1) {
                    alert.setTitle("Erro");
                    alert.setContentText("Erro ao incluir Movimento. \n" + ex1.getMessage());
                    alert.show();
                }
                alert.setTitle("Erro");
                alert.setContentText("Erro ao incluir Movimento. \n " + ex.getMessage());
                alert.show();
            }
            conecta.desconecta();
        }
        return false;
    }

    public List<String> listarEmpresas() throws SQLException {
        if (conecta.conexao(host, caminho)) {
            String sql = "SELECT LDCODEMP FROM LAPA13 WHERE LDUSUARI LIKE '%SUPORTE%'";
            if (conecta.executaSQL(sql)) {
                List<String> empresas = new ArrayList<>();
                if (conecta.getRs().first()) {
                    do {
                        empresas.add(conecta.getRs().getString("LDCODEMP"));
                    } while (conecta.getRs().next());
                    return empresas;
                }
            }
        }
        return null;
    }

    public List<TipoMovimento> listarTipos() throws SQLException {
        if (conecta.conexao(host, caminho)) {
            String sql = "select T62TPMOV,T62DESCR from LAPT62 order by T62TPMOV";
            if (conecta.executaSQL(sql)) {
                List<TipoMovimento> tipoMovimentos = new ArrayList<>();
                if (conecta.getRs().first()) {
                    do {
                        tipoMovimentos.add(new TipoMovimento(conecta.getRs().getString("T62TPMOV"), conecta.getRs().getString("T62DESCR")));
                    } while (conecta.getRs().next());
                    return tipoMovimentos;
                }
            }
        }
        return null;
    }

    public List<Movimento> listarMovimentos(String data, String documento) throws SQLException {
        if (conecta.conexao(host, caminho)) {
            String sql = "select MCCODEMP as empresa,MCTIPMOV as tipo,MCNUMERO as documento,MCDATMOV as data,MCHISTOR as observacao from SCEA02 where MCNUMERO like '%" + documento + "%' and MCDATMOV BETWEEN '" + data + " 00:00:00' and '" + data + " 23:59:59' group by MCCODEMP,MCTIPMOV,MCNUMERO,MCDATMOV,MCHISTOR order by MCNUMERO";
            if (conecta.executaSQL(sql)) {
                Set<MovimentoDTO> movimentos = new HashSet<>();
                if (conecta.getRs().first()) {
                    do {
                        movimentos.add(new MovimentoDTO(conecta.getRs().getString("empresa"),
                                conecta.getRs().getString("tipo"),
                                conecta.getRs().getString("documento"),
                                conecta.getRs().getDate("data"),
                                conecta.getRs().getString("observacao")
                        ));
                    } while (conecta.getRs().next());
                    return movimentos.stream().collect(Collectors.toList());
                }
            }
        }
        return null;
    }

    public void excluirMovimento(Movimento movimento) {
        if (conecta.conexao(host, caminho)) {
            try {
                String sql = "delete from SCEA02 where MCCODEMP=? and MCTIPMOV=? and MCNUMERO=?";
                PreparedStatement pst = conecta.getConn().prepareStatement(sql);
                pst.setString(1, movimento.getEmpresa());
                pst.setString(2, movimento.getTipo());
                pst.setString(3, movimento.getDocumento());
                pst.execute();
                conecta.getConn().commit();
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setContentText("Erro ao excluir Movimento. \n" + ex.getMessage());
                alert.show();
            }
        }
    }

    public Movimento verificarMovimento(Movimento movimento) throws SQLException {
        if (conecta.conexao(host, caminho)) {
            String sql = "select MCCODEMP,MCTIPMOV,MCNUMERO,MCDATMOV,MCHISTOR,MCIDENTI,MCDATALT from SCEA02 where MCCODEMP='" + movimento.getEmpresa() + "' and MCTIPMOV='" + movimento.getTipo() + "' and MCNUMERO='" + movimento.getDocumento() + "' ";
            if (conecta.executaSQL(sql)) {
                if (conecta.getRs().first()) {
                    return new Movimento(conecta.getRs().getString("MCCODEMP"),
                            conecta.getRs().getString("MCTIPMOV"),
                            conecta.getRs().getString("MCNUMERO"),
                            conecta.getRs().getString("MCHISTOR"),
                            conecta.getRs().getString("MCIDENTI"),
                            conecta.getRs().getDate("MCDATMOV"),
                            conecta.getRs().getDate("MCDATALT"));
                }
            }
        }
        return null;
    }

    private void BuscarCaminho() {
        Path path = Paths.get("Preco.txt");
        if (Files.exists(path)) {
            try {
                List<String> lista = Files.lines(path).collect(Collectors.toList());
                host = lista.get(0);
                caminho = lista.get(1);
            } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setContentText("Erro arquivo não encontrado" + ex.getMessage());
                alert.show();
            }
        }
    }
}
