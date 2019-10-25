package servico;

import controle.ConectaBanco;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.scene.control.Alert;
import modelo.Grupo;
import modelo.ItemMovimento;
import modelo.Movimento;
import modelo.Produto;
import modelo.SubGrupo;

public class ProdutoServico {

    public final ConectaBanco conecta = new ConectaBanco();
    private MovimentoService movimentoService;
    private ItemMovimentoService itemMovimentoService;
    private final List<Produto> produtos;

    public ProdutoServico() {
        this.produtos = new ArrayList<>();
    }

    public void salvar(Produto produto) {
        Produto produtoEncontrado = buscarProduto(produto.getReferencia());
        if (produtoEncontrado == null) {
            persistir(produto);
            return;
        }
        alterar(produto);
    }

    private void persistir(Produto produto) {
        if (conecta.conexao()) {
            try {
                String sql = "INSERT INTO SCEA01 (PRREFERE,PRDESCRI,PRCODBAR,PRREFLIM,PRCGRUPO,PRSUBGRP,PRUNDCPR,PRUNIDAD,PRPOSTRI,PRSPOTRI,PRCLASSI,PRCDCEST,PRIDENTI,PRIVESEF,PRQTDATA,PRULTALT,PRCONFPR)";
                sql += " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
                pst.setString(17, produto.getConfirmaPreco());
                pst.execute();
                conecta.getConn().commit();
                pst.close();
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
                    pst2.close();
                    if (produto.getQuantidade() == 0) {
                        continue;
                    }
                    this.movimentoService = new MovimentoService();
                    this.itemMovimentoService = new ItemMovimentoService();
                    Movimento movimento = new Movimento(
                            empresa,
                            "ENAQ",
                            new SimpleDateFormat("ddMMyyyy").format(new Date()),
                            "Estoque Inicial", "ADM", new Date(), new Date());
                    Movimento m = movimentoService.verificarMovimento(movimento);
                    if (m == null) {
                        movimentoService.salvar(movimento);
                    }
                    itemMovimentoService.salvar(new ItemMovimento(movimento, "01", produto.getReferencia(), produto.getQuantidade(), produto.getPreco()));
                }
                pst = conecta.getConn().prepareStatement("insert into scea09 values(?,?,?)");
                pst.setString(1, produto.getReferencia());
                pst.setString(2, produto.getReferencia());
                pst.setString(3, produto.getReferencia());
                pst.execute();
                conecta.getConn().commit();
                pst.close();
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
        if (conecta.conexao()) {
            try {
                PreparedStatement pst = conecta.getConn().prepareStatement("update scea07 set EEPBRTB1=?,EEPLQTB1=?,EEPBRTB2=?,EEPLQTB2=?,EET2PVD1=?  where EEREFERE=?");
                pst.setDouble(1, produto.getPreco());
                pst.setDouble(2, produto.getPreco());
                pst.setDouble(3, produto.getPreco());
                pst.setDouble(4, produto.getPreco());
                pst.setDouble(5, produto.getPrecoAtacado());
                pst.setString(6, produto.getReferencia());
                pst.execute();
                conecta.getConn().commit();
                pst.close();
                pst = conecta.getConn().prepareStatement("update scea01 set PRQTDATA=?,PRCLASSI=?,PRCDCEST=?,prpostri=?,prspotri=?,prdescri=?,PRUNDCPR=?,PRUNIDAD=?,prcodbar=?,PRCGRUPO=?,PRSUBGRP=?,PRULTALT=?,PRDATCAN=?,PRCONFPR=? where prrefere=?");
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
                pst.setDate(12, new java.sql.Date(new Date().getTime()));
                pst.setDate(13, null);
                if (produto.getDataCancelamento() != null) {
                    pst.setDate(13, new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(produto.getDataCancelamento()).getTime()));
                }
                pst.setString(14, produto.getConfirmaPreco());
                pst.setString(15, produto.getReferencia());
                pst.execute();
                conecta.getConn().commit();
                pst.close();
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
            } catch (ParseException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setContentText("Na conversão da Data de Cancelamento");
                alert.show();
            }
            conecta.desconecta();
        }
    }

    public void excluirProduto(String refencia) throws SQLException {
        if (conecta.conexao()) {
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
            preparedStatement.close();
            conecta.desconecta();
        }
    }

    public void atualizarGrupo(String referencia, String grupo) throws SQLException {
        if (!conecta.conexao()) {
            return;
        }
        try (PreparedStatement preparedStatement = conecta.getConn().prepareStatement("update scea01 set PRCGRUPO=?,PRSUBGRP=? where PRREFERE=?")) {
            preparedStatement.setString(1, grupo);
            preparedStatement.setString(2, grupo);
            preparedStatement.setString(3, referencia);
            preparedStatement.execute();
            conecta.getConn().commit();
            preparedStatement.close();
        }
        conecta.desconecta();
    }

    public List<Grupo> listarGrupos() throws SQLException {
        if (!conecta.conexao()) {
            return null;
        }
        String sql = "SELECT T51CDGRP as codigo,T51DSGRP as nome FROM LAPT51 ORDER BY T51DSGRP";
        if (!conecta.executaSQL(sql)) {
            return null;
        }
        if (!conecta.getRs().first()) {
            return null;
        }
        List<Grupo> grupos = new ArrayList<>();
        do {
            grupos.add(new Grupo(conecta.getRs().getString("codigo"), conecta.getRs().getString("nome")));
        } while (conecta.getRs().next());
        return grupos;
    }

    public List<SubGrupo> listarSubGrupos() throws SQLException {
        if (!conecta.conexao()) {
            return null;
        }
        String sql = "SELECT T52CDSGR as codigo,T52DSSGR as nome FROM LAPT52 ORDER BY T52DSSGR";
        if (!conecta.executaSQL(sql)) {
            return null;
        }
        if (!conecta.getRs().first()) {
            return null;
        }
        List<SubGrupo> grupos = new ArrayList<>();
        do {
            grupos.add(new SubGrupo(conecta.getRs().getString("codigo"), conecta.getRs().getString("nome")));
        } while (conecta.getRs().next());
        return grupos;
    }

    private List<String> buscarEmpresa() throws SQLException {
        if (!conecta.conexao()) {
            return null;
        }
        String sql = "SELECT LDCODEMP FROM LAPA13 WHERE LDUSUARI LIKE '%SUPORTE%'";
        if (!conecta.executaSQL(sql)) {
            return null;
        }
        if (!conecta.getRs().first()) {
            return null;
        }
        List<String> empresas = new ArrayList<>();
        do {
            empresas.add(conecta.getRs().getString("LDCODEMP"));
        } while (conecta.getRs().next());
        return empresas;
    }

    public Produto buscarProduto(String referencia) {
        String sql;
        if (referencia.trim().isEmpty()) {
            return null;
        }
        if (!conecta.conexao()) {
            return null;
        }
        sql = "select first 1 PRREFERE,PRDESCRI,EEPBRTB1,EET2PVD1,PRQTDATA,PRCLASSI,PRCDCEST,PRPOSTRI,PRSPOTRI,PRUNIDAD,PRCODBAR,PRDATCAN,PRCGRUPO,PRSUBGRP,EEESTATU,PRCONFPR from scea01 left outer join scea07 on(eerefere=prrefere) "
                + " where prrefere ='" + referencia + "' or PRCODBAR='" + referencia + "' ";
        if (!conecta.executaSQL(sql)) {
            return null;
        }
        try {
            if (conecta.getRs().first()) {
                Produto produto = buscarProdutoScea01();
                conecta.desconecta();
                return produto;
            }
            sql = "select first 1 PRREFERE,PRDESCRI,EEPBRTB1,EET2PVD1,PRQTDATA,PRCLASSI,PRCDCEST,prpostri,prspotri,PRUNIDAD,PRCODBAR,PRDATCAN,PRCGRUPO,PRSUBGRP,EEESTATU,PRCONFPR  from SCEA07 "
                    + "left outer join  SCEA09 on(RAREFERE=EEREFERE) "
                    + "left outer join SCEA01 on(PRREFERE=EEREFERE) "
                    + "where RAREFERE='" + referencia + "' "
                    + "or    RAREFAUX='" + referencia + "' "
                    + "or    RAREFLIM='" + referencia + "' ";
            if (!conecta.executaSQL(sql)) {
                return null;
            }
            if (!conecta.getRs().first()) {
                return null;
            }
            Produto produto = buscarProdutoScea01();
            conecta.desconecta();
            return produto;
        } catch (SQLException ex) {
            conecta.desconecta();
        }
        return null;
    }

    public List<Produto> pesquisarProduto(String pesquisa) {
        String sql;
        this.produtos.clear();
        if (pesquisa.trim().isEmpty()) {
            return null;
        }
        if (!conecta.conexao()) {
            return null;
        }
        sql = "select PRREFERE,PRDESCRI,PRCODBAR from scea01 "
                + " where prrefere ='" + pesquisa + "' or PRCODBAR='" + pesquisa + "' or PRDESCRI like '%" + pesquisa + "%'";
        if (!conecta.executaSQL(sql)) {
            return this.produtos;
        }
        try {
            if (!conecta.getRs().first()) {
                return this.produtos;
            }
            do {
                Produto produto = new Produto();
                produto.setReferencia(this.conecta.getRs().getString("PRREFERE"));
                produto.setDescricao(this.conecta.getRs().getString("PRDESCRI"));
                produto.setCodigoBarra(this.conecta.getRs().getString("PRCODBAR"));
                this.produtos.add(produto);
            } while (this.conecta.getRs().next());
            conecta.desconecta();
        } catch (SQLException ex) {
            conecta.desconecta();
        }
        return this.produtos;
    }

    public void atualizarDataCancelamento(Produto produto) throws SQLException {
        if (conecta.conexao()) {
            String data = produto.getDataCancelamento() != null ? "'" + produto.getDataCancelamento() + "'" : null;
            String sql = "update scea01 set PRDATCAN=" + data + " where prrefere='" + produto.getReferencia() + "'";
            PreparedStatement pst = conecta.getConn().prepareStatement(sql);
            pst.execute();
            conecta.getConn().commit();
            pst.close();
        }
    }

    public String gerarReferencia() {
        if (!conecta.conexao()) {
            return "1";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append(" cast(floor((rand()*count(*))*1000) as integer) AS SO_NUMERO ").append(" FROM SCEA01");
        String sql = sb.toString();
        if (!conecta.executaSQL(sql)) {
            return "1";
        }
        try {
            if (!conecta.getRs().first()) {
                return "1";
            }
            String refencia = conecta.getRs().getString("SO_NUMERO");
            if (refencia == null) {
                return "1";
            }
            while (verificarSeExisteReferncia(refencia)) {
                refencia = String.valueOf(Integer.parseInt(refencia) + 1);
            }
            return refencia;
        } catch (SQLException ex) {
            return "1";
        }
    }

    private boolean verificarSeExisteReferncia(String refencia) throws SQLException {
        if (!conecta.executaSQL("select count(*) as numero from scea01 where prrefere='" + refencia + "'")) {
            return false;
        }
        if (!conecta.getRs().first()) {
            return false;
        }
        return conecta.getRs().getInt("numero") != 0;
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
        produto.setConfirmaPreco(conecta.getRs().getString("PRCONFPR"));
        produto.setQuantidade(conecta.getRs().getDouble("EEESTATU"));
        return produto;
    }
}
