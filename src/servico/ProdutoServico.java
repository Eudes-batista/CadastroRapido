package servico;

import controle.ConectaBanco;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.control.Alert;
import modelo.Grupo;
import modelo.ItemMovimento;
import modelo.Movimento;
import modelo.Produto;
import modelo.SubGrupo;

public class ProdutoServico {

    public final ConectaBanco conecta = new ConectaBanco();
    private String host, caminho;
    private MovimentoService movimentoService;
    private ItemMovimentoService itemMovimentoService;

    public ProdutoServico() {
        BuscarCaminho();
    }

    public void salvar(Produto produto) {
        Produto p = buscarProduto(produto.getReferencia());
        if (p != null) {
            alterar(produto);
        } else {
            persistir(produto);
        }

    }

    private void persistir(Produto produto) {
        if (conecta.conexao(host, caminho)) {
            try {
                String sql = "INSERT INTO SCEA01 (PRREFERE,PRDESCRI,PRCODBAR,PRREFLIM,PRCGRUPO,PRSUBGRP,PRUNDCPR,PRUNIDAD,PRPOSTRI,PRSPOTRI,PRCLASSI,PRCDCEST,PRIDENTI,PRIVESEF,PRQTDATA,PRULTALT)";
                sql += " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement pst = conecta.getConn().prepareStatement(sql);
                pst.setString(1, produto.getReferencia());
                if (produto.getDescricao().length() >= 36) {
                    pst.setString(2, produto.getDescricao().substring(0, 35));
                } else {
                    pst.setString(2, produto.getDescricao());
                }
                pst.setString(3, produto.getCodigoBarra());
                pst.setString(4, produto.getReferencia());
                pst.setString(5, produto.getGrupo());
                pst.setString(6, produto.getSubgrupo());
                pst.setString(7, produto.getUnidade());
                pst.setString(8, produto.getUnidade());
                pst.setString(9, produto.getTributacao());
                pst.setString(10, produto.getTributacao());
                pst.setString(11, produto.getNcm());
                pst.setString(12, produto.getCest());
                pst.setString(13, "ADM");
                pst.setString(14, "1");
                pst.setDouble(15, produto.getQtdAtacado());
                pst.setString(16, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                pst.execute();
                conecta.getConn().commit();
                String data = LocalDate.now().toString();
                List<String> empresas = buscarEmpresa();
                for (String empresa : empresas) {
                    sql = "INSERT INTO SCEA07 (EECODEMP,EEREFERE,EEDTTAB1,EEPBRTB1,EEPLQTB1,EEDTTAB2,EEPBRTB2,EEPLQTB2,EEDTTAB3,EEPBRTB3,EEPLQTB3,EET2PVD1)";
                    sql += " VALUES ('" + empresa + "','" + produto.getReferencia() + "','" + data + "'," + produto.getPreco() + "," + produto.getPreco()
                            + ",'" + data + "','" + produto.getPreco() + "'," + produto.getPreco()
                            + ",'" + data + "'," + produto.getPreco() + "," + produto.getPreco() + "," + produto.getPrecoAtacado() + ")";
                    PreparedStatement pst2 = conecta.getConn().prepareStatement(sql);
                    pst2.execute();
                    conecta.getConn().commit();
                    if (produto.getQuantidade() != 0) {
                        this.movimentoService = new MovimentoService();
                        this.itemMovimentoService = new ItemMovimentoService();
                        Movimento movimento = new Movimento(
                                empresa,
                                "ENAQ",
                                new SimpleDateFormat("ddMMyyyy").format(new Date()),
                                "Estoque Inicial", "ADM", new Date(), new Date());
                        Movimento m = movimentoService.verificarMovimento(movimento);
                        if(m == null)
                           movimentoService.salvar(movimento);
                        itemMovimentoService.salvar(new ItemMovimento(movimento, "01", produto.getReferencia(), produto.getQuantidade(), produto.getPreco()));
                    }
                }
                pst = conecta.getConn().prepareStatement("insert into scea09 values(?,?,?)");
                pst.setString(1, produto.getReferencia());
                pst.setString(2, produto.getReferencia());
                pst.setString(3, produto.getReferencia());
                pst.execute();
                conecta.getConn().commit();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("SUCESSO!!");
                alert.setContentText("Produto salvo com sucesso.");
                alert.showAndWait();
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                try {
                    conecta.getConn().rollback();
                } catch (SQLException ex1) {
                    alert.setTitle("Erro");
                    alert.setContentText("Erro ao Cadastrar Produto " + ex1.getMessage());
                    alert.show();
                }
                alert.setTitle("Erro");
                alert.setContentText("Erro ao Cadastrar o Produto\n detalhe do erro: " + ex.getMessage());
                alert.show();
            }
            conecta.desconecta();
        }
    }

    private void alterar(Produto produto) {
        if (conecta.conexao(host, caminho)) {
            try {
                PreparedStatement pst = conecta.getConn().prepareStatement("update scea07 set EEPBRTB1=?,EEPLQTB1=?,EEPBRTB2=?,EEPLQTB2=?,EET2PVD1=?  where EEREFERE=?");
                pst.setDouble(1, produto.getPreco());
                pst.setDouble(2, produto.getPreco());
                pst.setDouble(3, produto.getPreco());
                pst.setDouble(4, produto.getPreco());
                pst.setDouble(5, produto.getPrecoAtacado());
                pst.setString(6, produto.getReferencia());
                pst.execute();
                pst = conecta.getConn().prepareStatement("update scea01 set PRQTDATA=?,PRCLASSI=?,PRCDCEST=?,prpostri=?,prspotri=?,prdescri=?,PRUNDCPR=?,PRUNIDAD=?,prcodbar=?,PRCGRUPO=?,PRSUBGRP=? where prrefere=?");
                pst.setDouble(1, produto.getQtdAtacado());
                pst.setString(2, produto.getNcm());
                pst.setString(3, produto.getCest());
                pst.setString(4, produto.getTributacao());
                pst.setString(5, produto.getTributacao());
                pst.setString(6, produto.getDescricao());
                pst.setString(7, produto.getUnidade());
                pst.setString(8, produto.getUnidade());
                pst.setString(9, produto.getCodigoBarra());
                pst.setString(10, produto.getGrupo());
                pst.setString(11, produto.getSubgrupo());
                pst.setString(12, produto.getReferencia());
                pst.execute();
                conecta.getConn().commit();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("SUCESSO!!");
                alert.setContentText("Produto salvo com sucesso.");
                alert.showAndWait();
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                try {
                    conecta.getConn().rollback();
                } catch (SQLException ex1) {
                    alert.setTitle("Erro");
                    alert.setContentText("Erro ao Alterar o preço\n detalhe do erro: " + ex1.getMessage());
                    alert.show();
                }
                alert.setTitle("Erro");
                alert.setContentText("Erro ao Alterar o preço\n detalhe do erro: " + ex.getMessage());
                alert.show();
            }
            conecta.desconecta();
        }
    }

    public void excluirProduto(String refencia) throws SQLException {
        if (conecta.conexao(host, caminho)) {
            PreparedStatement preparedStatement = conecta.getConn().prepareStatement("delete from scea09 where rarefere = ?");
            preparedStatement.setString(1, refencia);
            preparedStatement.execute();
            preparedStatement = conecta.getConn().prepareStatement("delete from scea07 where eerefere = ?");
            preparedStatement.setString(1, refencia);
            preparedStatement.execute();
            preparedStatement = conecta.getConn().prepareStatement("delete from scea01 where prrefere = ?");
            preparedStatement.setString(1, refencia);
            preparedStatement.execute();
            conecta.getConn().commit();
            conecta.desconecta();
        }
    }

    public List<Grupo> listarGrupos() throws SQLException {
        if (conecta.conexao(host, caminho)) {
            String sql = "SELECT T51CDGRP as codigo,T51DSGRP as nome FROM LAPT51 ORDER BY T51DSGRP";
            if (conecta.executaSQL(sql)) {
                List<Grupo> grupos = new ArrayList<>();
                if (conecta.getRs().first()) {
                    do {
                        grupos.add(new Grupo(conecta.getRs().getString("codigo"), conecta.getRs().getString("nome")));
                    } while (conecta.getRs().next());
                    return grupos;
                }
            }
        }
        return null;
    }

    public List<SubGrupo> listarSubGrupos() throws SQLException {
        if (conecta.conexao(host, caminho)) {
            String sql = "SELECT T52CDSGR as codigo,T52DSSGR as nome FROM LAPT52 ORDER BY T52DSSGR";
            if (conecta.executaSQL(sql)) {
                List<SubGrupo> grupos = new ArrayList<>();
                if (conecta.getRs().first()) {
                    do {
                        grupos.add(new SubGrupo(conecta.getRs().getString("codigo"), conecta.getRs().getString("nome")));
                    } while (conecta.getRs().next());
                    return grupos;
                }
            }
        }
        return null;
    }

    private List<String> buscarEmpresa() throws SQLException {
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

    public Produto buscarProduto(String referencia) {
        String sql;
        if (!referencia.trim().isEmpty()) {
            if (conecta.conexao(host, caminho)) {
                sql = "select first 1 PRREFERE,PRDESCRI,EEPBRTB1,EET2PVD1,PRQTDATA,PRCLASSI,PRCDCEST,PRPOSTRI,PRSPOTRI,PRUNIDAD,PRCODBAR,PRDATCAN,PRCGRUPO,PRSUBGRP,EEESTATU from scea01 left outer join scea07 on(eerefere=prrefere) "
                        + " where prrefere ='" + referencia + "' or PRCODBAR='" + referencia + "' ";
                if (conecta.executaSQL(sql)) {
                    try {
                        if (conecta.getRs().first()) {
                            return buscarProdutoScea01();
                        } else {
                            sql = "select first 1 PRREFERE,PRDESCRI,EEPBRTB1,EET2PVD1,PRQTDATA,PRCLASSI,PRCDCEST,prpostri,prspotri,PRUNIDAD,PRCODBAR,PRDATCAN,PRCGRUPO,PRSUBGRP,EEESTATU  from SCEA07 "
                                    + "left outer join  SCEA09 on(RAREFERE=EEREFERE) "
                                    + "left outer join SCEA01 on(PRREFERE=EEREFERE) "
                                    + "where RAREFERE='" + referencia + "' "
                                    + "or    RAREFAUX='" + referencia + "' "
                                    + "or    RAREFLIM='" + referencia + "' ";
                            if (conecta.executaSQL(sql)) {
                                if (conecta.getRs().first()) {
                                    return buscarProdutoScea01();
                                }
                            }
                        }
                    } catch (SQLException ex) {
                    }
                }
                conecta.desconecta();
            }
        }
        return null;
    }

    public void atualizarDataCancelamento(Produto produto) throws SQLException {
        if (conecta.conexao(host, caminho)) {
            String data = produto.getDataCancelamento() != null ? "'" + produto.getDataCancelamento() + "'" : null;
            String sql = "update scea01 set PRDATCAN=" + data + " where prrefere='" + produto.getReferencia() + "'";
            PreparedStatement pst = conecta.getConn().prepareStatement(sql);
            pst.execute();
            conecta.getConn().commit();
        }
    }

    public long gerarReferencia() {
        if (conecta.conexao(host, caminho)) {
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT ")
                    .append(" floor((rand()*count(*))*10000) AS SO_NUMERO ")
                    .append(" FROM SCEA01");
            String sql = sb.toString();
            if (conecta.executaSQL(sql)) {
                try {
                    if (conecta.getRs().first()) {
                        String refencia = conecta.getRs().getString("SO_NUMERO");
                        if (refencia == null) {
                            return 1;
                        }
                        while (verificarSeExisteReferncia(refencia)) {
                            refencia = String.valueOf(Integer.parseInt(refencia) + 1);
                        }
                        return (long) Double.parseDouble(refencia);
                    }
                } catch (SQLException ex) {
                }
            }
        }
        return 1;
    }

    private boolean verificarSeExisteReferncia(String refencia) throws SQLException {
        if (conecta.executaSQL("select count(*) as numero from scea01 where prrefere='" + refencia + "'")) {
            if (conecta.getRs().first()) {
                return conecta.getRs().getInt("numero") != 0;
            }
        }
        return false;
    }

    private Produto buscarProdutoScea01() throws SQLException {
        Produto produto = new Produto();
        produto.setReferencia(conecta.getRs().getString("PRREFERE"));
        produto.setCodigoBarra(conecta.getRs().getString("PRCODBAR"));
        produto.setDescricao(conecta.getRs().getString("PRDESCRI"));
        produto.setPreco(conecta.getRs().getDouble("EEPBRTB1"));
        produto.setPrecoAtacado(conecta.getRs().getDouble("EET2PVD1"));
        produto.setQtdAtacado(conecta.getRs().getDouble("PRQTDATA"));
        produto.setNcm(conecta.getRs().getString("PRCLASSI"));
        produto.setCest(conecta.getRs().getString("PRCDCEST"));
        produto.setTributacao(conecta.getRs().getString("PRPOSTRI"));
        produto.setUnidade(conecta.getRs().getString("PRUNIDAD"));
        produto.setDataCancelamento(conecta.getRs().getString("PRDATCAN"));
        produto.setGrupo(conecta.getRs().getString("PRCGRUPO"));
        produto.setSubgrupo(conecta.getRs().getString("PRSUBGRP"));
        produto.setQuantidade(conecta.getRs().getDouble("EEESTATU"));
        return produto;
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
